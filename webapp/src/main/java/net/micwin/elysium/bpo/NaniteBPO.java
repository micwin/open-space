package net.micwin.elysium.bpo;

import java.util.Iterator;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.gates.Gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 (c) 2012 micwin.net

 This file is part of open-space.

 open-space is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 open-space is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with open-space.  If not, see http://www.gnu.org/licenses.

 Diese Datei ist Teil von open-space.

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */
/**
 * A bpo handling with nanite processes.
 * 
 * @author MicWin
 * 
 */
public class NaniteBPO extends BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(NaniteBPO.class);

	/**
	 * The maximum nanites group size on level 1 of NANITE_MANAGEMENT.
	 */
	private static final int BASE_MAX_NANITES_GROUP_SIZE = 256;

	private static final int MAX_NOOB_LEVEL = 100;

	private static final double BASE_DAMAGE_PER_NANITE = 0.01;

	private static final int MAX_RESURRECTION_LEVEL = 1000;

	/**
	 * The percentage of level and skill points that survives death if
	 * resurrected.
	 */
	private static final double REVOCATION_FACTOR = 0.3;

	public void doubleCount(NaniteGroup nanitesGroup) {

		long maxCount = computeMaxTotalCount(nanitesGroup.getController());

		long currentTotalCount = countNanites(nanitesGroup.getController());

		long raiseCount = Math.min(nanitesGroup.getNaniteCount(), maxCount - currentTotalCount);

		long newCount = nanitesGroup.getNaniteCount() + raiseCount;

		if (L.isDebugEnabled()) {
			L.debug("setting count from nanites group " + nanitesGroup.getId() + " from "
							+ nanitesGroup.getNaniteCount() + " to " + newCount);
		}
		nanitesGroup.setNaniteCount(newCount);
		getNanitesDao().update(nanitesGroup, true);
	}

	/**
	 * Computes the maximunm number of nanites (not groups!) this avatar can
	 * control.
	 * 
	 * @param avatar
	 * @return
	 */
	public long computeMaxTotalCount(Avatar avatar) {
		Utilization naniteManagement = getTalent(avatar, Appliance.NANITE_MANAGEMENT);

		long maxCount = (long) (BASE_MAX_NANITES_GROUP_SIZE * Math.pow(2, naniteManagement.getLevel() - 1));
		return maxCount;
	}

	/**
	 * splits a nanite group into two with half the size each.
	 * 
	 * @param naniteGroup
	 */
	public void split(NaniteGroup naniteGroup) {

		getNanitesDao().refresh(naniteGroup);

		if (!canRaiseGroupCount(naniteGroup.getController())) {
			L.debug("cannot split - cannot raise group count");
			return;
		}

		if (naniteGroup.getNaniteCount() < 2) {
			L.debug("cannot split - size of source group < 2");
			return;
		}

		long halfNaniteCount = naniteGroup.getNaniteCount() / 2;
		if (halfNaniteCount < 1) {
			return;
		}
		naniteGroup.setNaniteCount(naniteGroup.getNaniteCount() - halfNaniteCount);

		NaniteGroup newGroup = getNanitesDao().create(halfNaniteCount, naniteGroup.getPosition());

		if (L.isDebugEnabled()) {
			L.debug("created new nanite group " + newGroup);
		}

		newGroup.setController(naniteGroup.getController());
		naniteGroup.getController().getNanites().add(newGroup);

		getNanitesDao().update(naniteGroup, true);
		getNanitesDao().update(newGroup, true);
		getAvatarDao().update(naniteGroup.getController(), true);

	}

	public boolean canRaiseGroupCount(Avatar avatar) {

		if (avatar == null)
			throw new NullPointerException("argument 'avatar' is null");
		Utilization talent = getTalent(avatar, Appliance.NANITE_MANAGEMENT);
		if (talent == null) {
			return false;
		}
		return talent.getLevel() > avatar.getNanites().size();
	}

	/**
	 * Counts the total number of nanites the avatar is controlling right now.
	 * 
	 * @param controller
	 * @return
	 */
	public long countNanites(Avatar controller) {
		long count = 0;
		for (NaniteGroup group : controller.getNanites()) {
			count += group.getNaniteCount();
		}

		return count;
	}

	/**
	 * starts a planetary gate travel fport a group of nanites.
	 * 
	 * @param naniteGroup
	 * @param targetAdress
	 * @return true if the travel has been started successfully, false
	 *         otherwise.
	 */
	public boolean gateTravel(NaniteGroup naniteGroup, String targetAdress) {
		Gate targetGate = getGatesDao().findByGateAdress(rectifyGateAdress(targetAdress));
		if (targetGate == null) {
			return false;
		}

		naniteGroup.setPosition(targetGate.getPosition());
		getNanitesDao().update(naniteGroup, true);
		return true;
	}

	public String rectifyGateAdress(String gateAdress) {
		L.warn("gate adress '' not rectified - not yet implemented!");
		return gateAdress;
	}

	/**
	 * Two nanite groups battling each other.
	 * 
	 * @param attacker
	 * @param defender
	 */
	public void attack(NaniteGroup attacker, NaniteGroup defender) {

		flush();
		// first - can they attack?
		if (!canAttack(attacker, defender)) {

			return;
		}

		long attackerStrength = calculateAttackDamage(attacker);
		long defenderStrength = calculateAttackDamage(defender);

		long damageDoneToDefender = doDamage(defender, attackerStrength);
		long damageDoneToAttacker = doDamage(attacker, defenderStrength);

		flush();

		if (L.isDebugEnabled()) {
			L.debug("damage effective done to defender : " + damageDoneToDefender);
			L.debug("damage effective done to attacker : " + damageDoneToAttacker);

		}

		// distribute xp

		XpBPO xpBPO = new XpBPO();

		long attackerXp = xpBPO.computeXpForDamage(attacker, defender, damageDoneToDefender);
		long defenderXp = xpBPO.computeXpForDamage(defender, attacker, damageDoneToAttacker);

		xpBPO.raiseXp(attacker.getController(), attackerXp);
		xpBPO.raiseXp(defender.getController(), defenderXp);

		flush();

	}

	private long doDamage(NaniteGroup naniteGroup, long damage) {

		double factor = 1;

		Utilization damageControl = new AvatarBPO().getTalent(naniteGroup.getController(),
						Appliance.NANITE_DAMAGE_CONTROL);

		if (L.isDebugEnabled()) {

			L.debug("dealing  " + damage + " damage to naniteGroup " + naniteGroup.getController().getName() + "@"
							+ naniteGroup.getPosition().getEnvironment() + " with damaage control="
							+ (damageControl == null ? 0 : damageControl.getLevel()));
		}

		if (damageControl != null) {
			factor *= Math.pow(0.9, damageControl.getLevel() - 1);
		}

		long damageDone = (long) (damage * factor);

		if (L.isDebugEnabled()) {

			L.debug("effective damage is " + damageDone);
		}

		long newCount = Math.round(naniteGroup.getNaniteCount() - damageDone);

		if ((naniteGroup.getController().getLevel() < MAX_NOOB_LEVEL) && (newCount < 1)) {
			// newbie protection
			if (L.isDebugEnabled())
				L.debug("engaging newbie protection  lvl < " + MAX_NOOB_LEVEL + " - last nanite group not killed");
			newCount = 1;
		}

		if (newCount > 0) {
			damageDone = naniteGroup.getNaniteCount() - newCount;
			naniteGroup.setNaniteCount(newCount);

			if (L.isDebugEnabled()) {
				L.debug(" - resulting in a new size of " + newCount);
			}

			getNanitesDao().update(naniteGroup, true);
		} else {

			L.debug(" - resulting in killing group");

			damageDone = naniteGroup.getNaniteCount();
			kill(naniteGroup);
		}

		return damageDone;

	}

	protected void kill(NaniteGroup naniteGroup) {

		if (naniteGroup.getController().getNanites().size() == 1) {
			// oopsie, will kill avatar too.
			kill(naniteGroup.getController());
			return;
		}

		Avatar controller = naniteGroup.getController();

		controller.getNanites().remove(naniteGroup);
		getAvatarDao().update(controller, false);
		getNanitesDao().delete(naniteGroup, false);
	}

	/**
	 * Kills avatar and all its nanite groups. If level &lt;=
	 * MAX_RESURRECTION_LEVEL, resurects the avatar.
	 * 
	 * @param avatar
	 */
	public void kill(Avatar avatar) {

		flush();
		if (avatar.getLevel() <= MAX_RESURRECTION_LEVEL) {
			revoke(avatar);
			flush();
			return;
		} else {

			// remove nanite groups
			for (NaniteGroup naniteGroup : avatar.getNanites()) {
				kill(naniteGroup);
			}
			getAvatarDao().update(avatar, true);
			getAvatarDao().delete(avatar, true);
		}
	}

	/**
	 * Revokes the avatar by a bunch of levels.
	 * 
	 * @param avatar
	 */
	private void revoke(Avatar avatar) {
		L.debug("revoking avatar " + avatar);
		int newLevel = (int) (avatar.getLevel() * REVOCATION_FACTOR);

		// clearing out talents
		for (Utilization utilization : avatar.getTalents()) {
			utilization.setLevel(0);
		}

		// delete nearly every nanite group (except one)
		while (avatar.getNanites().size() > 1) {
			NaniteGroup naniteGroup = avatar.getNanites().iterator().next();
			kill(naniteGroup);
		}

		// set last group to base count
		avatar.getNanites().iterator().next().setNaniteCount(BASE_MAX_NANITES_GROUP_SIZE);

		// set level and talent points to new values
		avatar.setLevel(newLevel);
		avatar.setTalentPoints(newLevel);

	}

	/**
	 * calculates the active attack strength of specified attacker.
	 * 
	 * @param attacker
	 * @return
	 */
	public long calculateAttackDamage(NaniteGroup attacker) {

		Utilization nanitesBattle = new AvatarBPO().getTalent(getAvatarDao().refresh(attacker.getController()),
						Appliance.NANITE_BATTLE);

		double factor = nanitesBattle == null ? 0.3 : Math.pow(1.1, nanitesBattle.getLevel() - 1);
		long totalAttackDamage = (long) (factor * attacker.getNaniteCount() * BASE_DAMAGE_PER_NANITE);
		L.debug("nanite group " + attacker + " attacks with count " + attacker.getNaniteCount()
						+ " and nanites battle factor = " + factor + ", resulting in a total attack damage of "
						+ totalAttackDamage);
		return totalAttackDamage;
	}

	/**
	 * Checks wether frist nanite group can attack latter.
	 * 
	 * @param attacker
	 * @param defender
	 * @return
	 */
	public boolean canAttack(NaniteGroup attacker, NaniteGroup defender) {

		// attacker not idle?
		if (attacker.getState() != State.IDLE) {
			return false;
		}

		// same owner?
		if (attacker.getController().equals(defender.getController())) {
			L.debug("cannot attack - same controller");
			return false;
		}

		// same environment?
		if (!attacker.getPosition().getEnvironment().equals(defender.getPosition().getEnvironment())) {
			L.debug("cannot attack - different environment");
			return false;
		}

		// both noobs?
		if (attacker.getController().getLevel() <= MAX_NOOB_LEVEL
						&& defender.getController().getLevel() <= MAX_NOOB_LEVEL) {
			return true;
		}

		// level based protection ?
		if (new AvatarBPO().isLevelBasedProtectionProtectionEngaged(attacker.getController(), defender.getController())) {
			L.debug("cannot attack - avatar level based protection");
			return false;
		}

		// well then, ok, anything ready.
		L.debug("yes - can attack.");
		return true;
	}

}
