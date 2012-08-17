package net.micwin.elysium.bpo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.GalaxyTimer;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User.Role;
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

	public static final int MAX_NOOB_LEVEL = 50;

	private static final double BASE_DAMAGE_PER_NANITE = 0.01;

	private static final double BASE_MAX_NANITE_COUNT = 256;

	private static final int FORTIFICATION_MIN_LEVEL = 50;

	public void doubleCount(NaniteGroup nanitesGroup) {

		getNanitesDao().refresh(nanitesGroup);

		// compute teh maximum overall number of nanites this avatar can control
		long maxTotalCount = computeMaxTotalCount(nanitesGroup.getController());

		// sum up all nanites the controller actually controls
		long currentTotalCount = countNanites(nanitesGroup.getController());

		// compute how much this group can grow at max
		long maxGroupRaise = Math.min(NaniteGroup.MAX_NANITES_COUNT - nanitesGroup.getNaniteCount(), maxTotalCount
						- currentTotalCount);

		// compute how much the group effectively grows
		long raiseCount = Math.min(nanitesGroup.getNaniteCount(), maxGroupRaise);

		// compute the new size of group
		long newCount = nanitesGroup.getNaniteCount() + raiseCount;

		if (L.isDebugEnabled()) {
			L.debug("max total count is " + maxTotalCount);
			L.debug("current total count is " + currentTotalCount);
			L.debug("max group raise is " + maxGroupRaise);
			L.debug("raise count is " + raiseCount);
			L.debug("setting count from nanites group " + nanitesGroup.getId() + " from "
							+ nanitesGroup.getNaniteCount() + " to " + newCount);
		}

		// setting and updating
		nanitesGroup.setNaniteCount(newCount);

		if (raiseCount > 0) {
			raiseUsage(nanitesGroup.getController(), Appliance.NANITE_MANAGEMENT, false);
		}
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

		long maxCount = (long) (BASE_MAX_NANITE_COUNT * Math.pow(2, naniteManagement.getLevel()));
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

		naniteGroup.getController().getNanites().add(newGroup);
		newGroup.setController(naniteGroup.getController());

		raiseUsage(naniteGroup.getController(), Appliance.NANITE_MANAGEMENT, false);

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
		return talent.getLevel() >= avatar.getNanites().size();
	}

	/**
	 * Counts the total number of nanites the avatar is controlling right now.
	 * 
	 * @param controller
	 * @return
	 */
	public long countNanites(Avatar controller) {
		getAvatarDao().refresh(controller);
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

		// check wether local gate is free

		Collection<Gate> localGate = getGatesDao().findByEnvironment(naniteGroup.getPosition().getEnvironment());
		if (localGate.isEmpty()) {
			getMessageBPO().send(naniteGroup, naniteGroup.getController(),
							"Kann Adresse '" + targetAdress + "' nicht anspringen - kein Absprungtor verfügbar");
			L.warn("cannot jump to gate adress '" + targetAdress + "' - no gate to enter in environment");
			return false;
		} else if (localGate.iterator().next().getGatePass() != null) {
			getMessageBPO().send(
							naniteGroup,
							naniteGroup.getController(),
							"Kann Adresse '" + targetAdress
											+ "' nicht anspringen - lokales Absprungtor ist abgeschlossen");
			L.warn("cannot jump to gate adress '" + targetAdress + "' - lacal gate locked.");
			return false;

		}

		targetAdress = rectifyGateAdress(targetAdress);
		Gate targetGate = getGatesDao().findByGateAdress(targetAdress);
		if (targetGate == null) {
			getMessageBPO().send(
							naniteGroup,
							naniteGroup.getController(),
							"Kann Adresse '" + targetAdress
											+ "' nicht anspringen - Adresse unbekannt oder Tor nicht (mehr) vorhanden");
			L.warn("cannot jump to gate adress '" + targetAdress + "' - adress not found");
			return false;
		} else if (targetGate.getGatePass() != null) {
			getMessageBPO().send(naniteGroup, naniteGroup.getController(),
							"Kann Adresse '" + targetAdress + "' nicht anspringen - Tor abgesperrt");
			L.warn("cannot jump to gate adress '" + targetAdress + "' - gate locked");
			return false;
		}

		naniteGroup.setPosition(targetGate.getPosition());
		raiseUsage(naniteGroup.getController(), Appliance.NANITE_MANAGEMENT, false);

		getNanitesDao().update(naniteGroup, true);
		return true;
	}

	public String rectifyGateAdress(String gateAdress) {
		return gateAdress.trim().toLowerCase();
	}

	/**
	 * Two nanite groups battling each other.
	 * 
	 * @param attacker
	 * @param defender
	 */
	public void attack(NaniteGroup attacker, NaniteGroup defender) {

		// first - can they attack?
		if (!canAttack(attacker, defender)) {

			return;
		}

		long attackerStrength = calculateAttackDamage(attacker, true, defender);
		long defenderStrength = calculateAttackDamage(defender, false, attacker);

		long damageDoneToDefender = doDamage(defender, attackerStrength);
		long damageDoneToAttacker = doDamage(attacker, defenderStrength);

		attacker.raiseBattleCounter();
		defender.raiseBattleCounter();

		if (L.isDebugEnabled()) {
			L.debug("damage effective done to defender : " + damageDoneToDefender);
			L.debug("damage effective done to attacker : " + damageDoneToAttacker);

		}

		// distribute xp

		Avatar attackingAvatar = attacker.getController();
		Avatar defendingAvatar = defender.getController();

		raiseUsage(attackingAvatar, Appliance.NANITE_BATTLE, false);
		raiseUsage(defendingAvatar, Appliance.NANITE_DAMAGE_CONTROL, false);

		if (attacker.getNaniteCount() < 1) {

			L.debug("killing attacker group");
			kill(attacker);
			defender.getController().raiseFragCount();
			raiseUsage(defendingAvatar, Appliance.NANITE_CRITICAL_HIT, false);
		}

		if (defender.getNaniteCount() < 1) {

			L.debug("killing defender group");
			kill(defender);
			getMessageBPO().send(defender, defender.getController(),
							"Gruppe zerstoert durch " + attackingAvatar.getName());
			attacker.getController().raiseFragCount();
			raiseUsage(attackingAvatar, Appliance.NANITE_CRITICAL_HIT, false);
		}

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
			int level = damageControl.getLevel() - 1;
			factor *= Math.pow(0.9, level);
		}

		long damageDone = (long) (damage * factor * naniteGroup.getState().getReceivingDamageFactor());

		if (L.isDebugEnabled()) {

			L.debug("effective damage is " + damageDone);
		}

		long newCount = Math.round(naniteGroup.getNaniteCount() - damageDone);

		if ((naniteGroup.getController().getLevel() < MAX_NOOB_LEVEL) && (newCount < 1)
						&& naniteGroup.getController().getNanites().size() == 1) {
			// newbie protection
			if (L.isDebugEnabled())
				L.debug("engaging newbie protection  lvl < " + MAX_NOOB_LEVEL
								+ " - last nanite group not killed and moved to elysium");
			newCount = 1;
			Gate elysiumGate = getGatesDao().findByGateAdress("elysium");
			naniteGroup.setPosition(elysiumGate.getPosition());
		}

		if (newCount > 0) {
			damageDone = naniteGroup.getNaniteCount() - newCount;
			naniteGroup.setNaniteCount(newCount);

			if (L.isDebugEnabled()) {
				L.debug(" - resulting in a new size of " + newCount);
			}

		} else {

			L.debug(" - resulting in putting group near death");
			damageDone = naniteGroup.getNaniteCount();
			naniteGroup.setNaniteCount(0);
		}

		getNanitesDao().update(naniteGroup, true);

		return damageDone;

	}

	public void kill(NaniteGroup naniteGroup) {
		Avatar controller = naniteGroup.getController();
		if (L.isDebugEnabled()) {
			L.debug("killing nanite group " + naniteGroup);
			L.debug("controller nanites before removal" + controller.getNanites());
		}

		// since this is removeOrphaned=true, this also removes the naniteGroup
		// from the database
		controller.getNanites().remove(naniteGroup);
		getNanitesDao().delete(naniteGroup, true);

		controller = getAvatarDao().refresh(controller);

		if (controller.getNanites().size() < 1) {
			// oops, died ....
			controller.setDeathCount(controller.getDeathCount() + 1);
			L.info("controller '" + controller.getName() + "' dying with new death count " + controller.getDeathCount()
							+ "...");
		}
		getAvatarDao().update(controller, true);

		if (L.isDebugEnabled()) {
			L.debug("...nanites after removal = " + getAvatarDao().refresh(controller).getNanites());
		}

	}

	/**
	 * calculates the active attack strength of specified attacker.
	 * 
	 * @param attacker
	 * @param attacker
	 * @return
	 */
	public long calculateAttackDamage(NaniteGroup attacker, boolean offensive, NaniteGroup defender) {

		Utilization nanitesBattle = new AvatarBPO().getTalent(getAvatarDao().refresh(attacker.getController()),
						Appliance.NANITE_BATTLE);

		double factor = nanitesBattle == null ? 0.3 : Math.pow(1.1, nanitesBattle.getLevel() - 1);

		factor *= offensive ? attacker.getState().getAttackDamageFactor() : attacker.getState()
						.getCounterStrikeDamageFactor() * computeNumberBasedEfficiencyFactor(attacker.getNaniteCount());

		// evaluate and add critical hit
		Utilization critical = new AvatarBPO().getTalent(getAvatarDao().refresh(attacker.getController()),
						Appliance.NANITE_CRITICAL_HIT);

		if (critical != null && critical.getLevel() > 0) {
			int criticalProbability = critical.getLevel() * 5;

			boolean doesCritical = Math.random() * 100 <= criticalProbability;
			if (doesCritical) {
				L.debug("critical hit!");
				factor *= 3;
			}
		}

		long totalAttackDamage = (long) (factor * attacker.getNaniteCount() * BASE_DAMAGE_PER_NANITE);
		L.debug("nanite group " + attacker + " attacks with count " + attacker.getNaniteCount()
						+ " and nanites battle factor = " + factor + ", resulting in a total attack damage of "
						+ totalAttackDamage);
		return totalAttackDamage;
	}

	public double computeNumberBasedEfficiencyFactor(long count) {
		// how long the way between max and 1 ?
		double log = Math.log(count);
		return Math.pow(0.9, log);

	}

	/**
	 * Checks wether frist nanite group can attack latter.
	 * 
	 * @param attacker
	 * @param defender
	 * @return
	 */
	public boolean canAttack(NaniteGroup attacker, NaniteGroup defender) {

		if (!attacker.getState().mayAttack()) {
			return false;
		}

		// same owner?
		if (attacker.getController().equals(defender.getController())) {
			L.debug("cannot attack - same controller");
			return false;
		}

		if (attacker.getController().getUser().getRole() == Role.ADMIN) {
			return true;
		}

		// same environment?
		if (!attacker.getPosition().getEnvironment().equals(defender.getPosition().getEnvironment())) {
			L.debug("cannot attack - different environment");
			return false;
		}

		// in an elysium environment?
		if (attacker.getPosition().getEnvironment().isElysium()) {
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

	/**
	 * Checks wether or not a specific nanite group can get raised.
	 * 
	 * @param naniteGroup
	 * @return
	 */
	public boolean canRaiseNanitesCount(NaniteGroup naniteGroup) {
		if (!naniteGroup.getState().canRaiseNanitesCount()) {
			return false;
		}
		if (naniteGroup.getNaniteCount() >= NaniteGroup.MAX_NANITES_COUNT)
			return false;

		return computeMaxTotalCount(naniteGroup.getController()) - countNanites(naniteGroup.getController()) > 0;
	}

	/**
	 * Checks wether the given nanite group can fortify, ie become a planetary
	 * stronghold.
	 * 
	 * @return
	 */
	public void entrench(NaniteGroup group) {
		if (!canEntrench(group)) {
			return;
		}

		long fortifyingEndDateMillis = GalaxyTimer.get().getGalaxyDate().getTime() + computeEntrenchingDuration(group);

		group.setState(State.ENTRENCHING);
		Date endDate = new Date(fortifyingEndDateMillis);
		group.setStateEndGT(endDate);
		getNanitesDao().update(group, true);
	}

	private long computeEntrenchingDuration(NaniteGroup group) {

		// for now, we need 5 Seconds for each level.
		return group.getController().getLevel() * 5 * 1000;
	}

	public boolean canSplit(NaniteGroup nanitesGroup) {

		return nanitesGroup.getNaniteCount() > 1 && nanitesGroup.getState().canSplit()
						&& canRaiseGroupCount(nanitesGroup.getController());

	}

	public boolean canJumpGate(NaniteGroup naniteGroup) {
		if (naniteGroup.getState() != State.IDLE) {
			return false;
		}

		return new GateBPO().hasPublicGate(naniteGroup.getPosition().getEnvironment());

	}

	/**
	 * Checks wether the given nanite group can fortify, ie become a planetary
	 * stronghold.
	 * 
	 * @return
	 */
	public boolean canEntrench(NaniteGroup group) {
		if (group.getState() != State.IDLE) {
			return false;
		}

		if (group.getNaniteCount() > 0) {
			return true;
		}

		Avatar avatar = group.getController();
		if (avatar.getUser().getRole() == Role.ADMIN) {
			return true;
		}

		return false;

	}

	public void untrenchArena(List<NaniteGroup> naniteGroupsNearGate) {
		for (NaniteGroup naniteGroup : naniteGroupsNearGate) {
			switch (naniteGroup.getState()) {
			case ENTRENCHING:
			case ENTRENCHED:
				naniteGroup.setState(State.IDLE);
				naniteGroup.setStateEndGT(null);
				DaoManager.I.getNanitesDao().update(naniteGroup, true);
				new MessageBPO().send(naniteGroup, naniteGroup.getController(),
								"Eine unbeschreibliche Macht hat uns aus dem Boden gehoben und das Subraumtor gesperrt. Wir sitzen hier fest!");
				break;

			}
		}
	};
}
