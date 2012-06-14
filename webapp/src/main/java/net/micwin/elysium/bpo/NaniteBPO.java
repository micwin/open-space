package net.micwin.elysium.bpo;

import net.micwin.elysium.entities.NaniteGroup;
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

	private static final int NOOB_PROTECTION_LEVEL = 15;

	public void doubleCount(NaniteGroup nanitesGroup) {

		Utilization naniteManagement = getTalent(nanitesGroup.getController(), Appliance.NANITE_MANAGEMENT);

		long maxCount = (long) (BASE_MAX_NANITES_GROUP_SIZE * Math.pow(2, naniteManagement.getLevel() - 1));

		long newCount = Math.min(nanitesGroup.getNaniteCount() * 2, maxCount);

		if (L.isDebugEnabled()) {
			L.debug("setting count from nanites group " + nanitesGroup.getId() + " from "
							+ nanitesGroup.getNaniteCount() + " to " + newCount);
		}
		nanitesGroup.setNaniteCount(newCount);
		getNanitesDao().save(nanitesGroup, true);
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
		getNanitesDao().save(naniteGroup, true);
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

		// first - can they attack?
		if (!canAttack(attacker, defender)) {

			return;
		}

		long attackerStrength = calculateAttackStrength(attacker);
		long defenderStrength = calculateAttackStrength(defender);

		long damageDoneToDefender = doDamage(defender, attackerStrength);
		long damageDoneToAttacker = doDamage(attacker, defenderStrength);

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

		long newCount = Math.round(naniteGroup.getNaniteCount() - damage);

		long damageDone = damage / 4;

		if (L.isDebugEnabled()) {

			L.debug("dealing  " + damage + " damage to naniteGroup " + naniteGroup.getController().getName() + "@"
							+ naniteGroup.getPosition().getEnvironment());
		}

		if ((naniteGroup.getController().getLevel() < NOOB_PROTECTION_LEVEL) && (newCount < 1)) {
			// newbie protection
			if (L.isDebugEnabled())
				L.debug("engaging newbie protection  lvl < " + NOOB_PROTECTION_LEVEL);
			newCount = 1;
		}

		if (newCount > 0) {
			damageDone = naniteGroup.getNaniteCount() - newCount;
			naniteGroup.setNaniteCount(newCount);

			if (L.isDebugEnabled()) {
				L.debug(" - resulting in a new size of " + newCount);
			}

			getNanitesDao().save(naniteGroup, false);
		} else {

			L.debug(" - resulting in killing group");

			damageDone = naniteGroup.getNaniteCount();
			kill(naniteGroup);
		}

		flush();

		return damageDone;

	}

	protected void kill(NaniteGroup naniteGroup) {
		Avatar controller = naniteGroup.getController();

		controller.getNanites().remove(naniteGroup);
		getAvatarDao().save(controller, false);
		getNanitesDao().delete(naniteGroup, false);
	}

	/**
	 * calculates the active attack strength of specified attacker.
	 * 
	 * @param attacker
	 * @return
	 */
	public long calculateAttackStrength(NaniteGroup attacker) {

		return attacker.getNaniteCount() / 4;
	}

	/**
	 * Checks wether frist nanite group can attack latter.
	 * 
	 * @param attacker
	 * @param defender
	 * @return
	 */
	public boolean canAttack(NaniteGroup attacker, NaniteGroup defender) {

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

		// noob protection?

		if (new AvatarBPO().isLevelBasedProtectionProtectionEngaged(attacker.getController(), defender.getController())) {
			L.debug("cannot attack - avatar level based protection");
			return false;
		}

		// well then, ok, anything ready.
		L.debug("yes - can attack.");
		return true;
	}

}
