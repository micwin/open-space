package net.micwin.openspace.bpo;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.appliances.Appliance;
import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.characters.User.Role;
import net.micwin.openspace.entities.characters.User.State;
import net.micwin.openspace.entities.galaxy.Position;
import net.micwin.openspace.entities.gates.Gate;
import net.micwin.openspace.entities.nanites.NaniteGroup;
import net.micwin.openspace.entities.nanites.NaniteState;

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

	private static long LEVEL_1_STRUCTURE_POINTS = 10000;

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
		getNanitesDao().update(nanitesGroup);
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
	public NaniteGroup split(NaniteGroup naniteGroup, boolean splitCount, boolean copyStructures) {

		getNanitesDao().refresh(naniteGroup);

		if (!canRaiseGroupCount(naniteGroup.getController())) {
			L.debug("cannot split - cannot raise group count");
			return null;
		}

		if (splitCount && naniteGroup.getNaniteCount() < 2) {
			L.debug("cannot split - size of source group < 2");
			return null;
		}

		long oldNaniteCount = naniteGroup.getNaniteCount();
		long newNaniteCount = splitCount ? naniteGroup.getNaniteCount() / 2 : oldNaniteCount;

		if (!splitCount) {

			long remainingMaxCount = computeMaxTotalCount(naniteGroup.getController())
							- countNanites(naniteGroup.getController());

			if (remainingMaxCount < 1) {
				return null;
			}
			newNaniteCount = Math.min(remainingMaxCount, newNaniteCount);

		} else {
			naniteGroup.setNaniteCount(naniteGroup.getNaniteCount() / 2);
		}

		NaniteGroup newGroup = getNanitesDao().create(newNaniteCount, naniteGroup.getPosition());

		if (L.isDebugEnabled()) {
			L.debug("created new nanite group " + newGroup);
		}

		naniteGroup.getController().getNanites().add(newGroup);
		newGroup.setController(naniteGroup.getController());

		newGroup.getController().getNanites().add(newGroup);
		newGroup.setBattleCounter(naniteGroup.getBattleCounter());

		if (copyStructures) {

			newGroup.setState(NaniteState.UPGRADING);
			copyStructureTo(naniteGroup, newGroup);
		}

		raiseUsage(naniteGroup.getController(), Appliance.NANITE_MANAGEMENT, false);

		getNanitesDao().update(naniteGroup);
		getNanitesDao().update(newGroup);
		getAvatarDao().update(naniteGroup.getController());

		return newGroup;
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
		getAvatarDao().refresh(controller);
		long count = 0;
		for (NaniteGroup group : controller.getNanites()) {
			count += group.getNaniteCount() + group.getAmbushSquads() + group.getSatellites();
		}

		return count;
	}

	/**
	 * starts a planetary gate travel for a group of nanites.
	 * 
	 * @param naniteGroup
	 * @param targetAdress
	 * @return true if the travel has been started successfully, false
	 *         otherwise.
	 */
	public boolean gateTravel(NaniteGroup naniteGroup, String targetAdress) {

		// check wether local gate is free

		Collection<Gate> localGate = getGatesDao().findByEnvironment(naniteGroup.getPosition().getEnvironment());
		boolean isAdmin = naniteGroup.getController().getUser().getRole() == Role.ADMIN;
		if (!isAdmin) {
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
		} else if (!isAdmin && targetGate.getGatePass() != null) {
			getMessageBPO().send(naniteGroup, naniteGroup.getController(),
							"Kann Adresse '" + targetAdress + "' nicht anspringen - Tor abgesperrt");
			L.warn("cannot jump to gate adress '" + targetAdress + "' - gate locked");
			return false;
		}

		naniteGroup.setPosition(targetGate.getPosition());
		raiseUsage(naniteGroup.getController(), Appliance.NANITE_MANAGEMENT, false);

		getNanitesDao().update(naniteGroup);
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

		boolean stopAttack = attackCatapult(attacker, defender) || attackAmbush(attacker, defender);

		if (stopAttack) {
			if (defender.getNaniteCount() < 1) {
				long activeAmbushSquads = computeActiveAmbush(attacker, defender);
				if (activeAmbushSquads > 0) {
					defender.setNaniteCount(activeAmbushSquads);
					getNanitesDao().update(defender);
					new MessageBPO().send(defender, defender.getController(),
									"unsere A.M.B.U.S.H-Schwadron konnte einer Zerstörung entgegenwirken und hat "
													+ activeAmbushSquads + " Einheiten reaktiviert.");
				} else {
					kill(defender);
				}
			}
			if (attacker.getNaniteCount() < 1)
				kill(attacker);

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

	/**
	 * processes an ambushed attack.
	 * 
	 * @param attacker
	 * @param defender
	 *            @ return false if the attack continues, true if the attack has
	 *            been stopped.
	 */
	private boolean attackAmbush(NaniteGroup attacker, NaniteGroup defender) {

		int activeAmbush = computeActiveAmbush(attacker, defender);

		if (activeAmbush < 1) {
			return false;
		}

		Utilization naMa = getTalent(defender.getController(), Appliance.NANITE_MANAGEMENT);
		Utilization ecs = getTalent(defender.getController(), Appliance.EMISSION_CONTROL);
		Utilization nat = getTalent(defender.getController(), Appliance.NANITE_BATTLE);

		double natFactor = nat != null && nat.getLevel() > 0 ? nat.getLevel() : 1;
		double ecFactor = ecs != null && ecs.getLevel() > 0 ? ecs.getLevel() : 1;

		double killednanitesPerSquad = (long) (naMa.getLevel() * ecFactor * natFactor);

		long totalKill = (long) (killednanitesPerSquad * activeAmbush);
		if (totalKill < 1) {

			return false;
		}

		if (totalKill > attacker.getNaniteCount()) {
			totalKill = attacker.getNaniteCount();
			new MessageBPO().send(defender, defender.getController(),
							"Unsere A.M.B.U.S.H.-Einheiten konnten eine angreifende Gruppe komplett zerstören!");
			new MessageBPO().send(attacker, attacker.getController(), "Wir wurden von A.M.B.U.S.H.-Einheiten zerstört!");
		}

		attacker.setNaniteCount(attacker.getNaniteCount() - totalKill);

		return attacker.getNaniteCount() < attacker.getMinNaniteCount();

	}

	public int computeActiveAmbush(NaniteGroup attacker, NaniteGroup defender) {
		return defender.getAmbushSquads() - attacker.getSatellites();
	}

	private boolean attackCatapult(NaniteGroup attacker, NaniteGroup defender) {

		NaniteGroup first, second;

		if (attacker.getBattleCounter() >= defender.getBattleCounter()) {
			first = attacker;
			second = defender;
		} else {
			first = defender;
			second = attacker;
		}

		int firstCanonsToFire = first.getCatapults();
		int secondCanonsToFire = second.getCatapults();

		boolean anotherRound = true;
		while (anotherRound) {

			if (firstCanonsToFire > 0) {
				firstCanonsToFire--;
				shootArtilleryOnce(first, second);
			}
			if (secondCanonsToFire > 0) {
				secondCanonsToFire--;
				shootArtilleryOnce(second, first);
			}

			anotherRound = (firstCanonsToFire > 0 || secondCanonsToFire > 0) && canAttack(first, second);
		}

		return attacker.getNaniteCount() < 1 || defender.getNaniteCount() < 1;

	}

	private void shootArtilleryOnce(NaniteGroup shooter, NaniteGroup target) {

		if (shooter.getCatapults() < 1)
			return;

		long projectiles = Math.max(
						(long) (getTalent(shooter.getController(), Appliance.NANITE_MANAGEMENT).getLevel() * 100),
						shooter.getNaniteCount() / 1000);

		long deactivatedProjectiles = 0;

		if (target.getState() != NaniteState.PASSIVATED) {
			Utilization trgEC = getTalent(target.getController(), Appliance.EMISSION_CONTROL);
			Utilization trgNM = getTalent(target.getController(), Appliance.NANITE_MANAGEMENT);
			deactivatedProjectiles += (target.getSatellites() * 1000 * (trgNM == null ? 0.5 : trgNM.getLevel()) + target
							.getFlaks() * 10000 * (trgEC == null ? 0.5 : trgEC.getLevel()));
		}
		projectiles -= deactivatedProjectiles;
		if (projectiles < 1) {
			return;
		}

		long damageCount = projectiles * (long) computeExplosionDamagePerNanite(shooter);
		doDamage(target, damageCount);

	}

	public long computeExplosionDamagePerNanite(NaniteGroup naniteGroup) {
		Utilization ec = getTalent(naniteGroup.getController(), Appliance.EMISSION_CONTROL);
		return (long) (2 * (ec == null ? 0.5 : ec.getLevel()));
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

		getNanitesDao().update(naniteGroup);

		return damageDone;

	}

	public void kill(NaniteGroup naniteGroup) {

		Avatar controller = naniteGroup.getController();
		if (L.isDebugEnabled()) {
			L.debug("killing nanite group " + naniteGroup);
			L.debug("controller nanites before removal" + controller.getNanites());
		}

		List<NaniteGroup> entities = getNanitesDao().findByEnvironment(naniteGroup);

		for (NaniteGroup content : entities) {
			content.setPosition(naniteGroup.getPosition());
			if (!naniteGroup.getPosition().getEnvironment().needsPassivation()) {
				content.returnToPreviousState();
			}
			getMessageBPO().send(content, content.getController(),
							"Unser Transporter wurde zerstört. Wir befinden uns nun auf " + content.getPosition());
		}

		getNanitesDao().update(entities);

		// since this is removeOrphaned=true, this also removes the naniteGroup
		// from the database
		controller.getNanites().remove(naniteGroup);
		getNanitesDao().delete(naniteGroup);

		controller = getAvatarDao().refresh(controller);

		if (controller.getNanites().size() < 1) {
			// oops, died ....
			controller.setDeathCount(controller.getDeathCount() + 1);
			L.info("controller '" + controller.getName() + "' dying with new death count " + controller.getDeathCount()
							+ "...");
		}
		getAvatarDao().update(controller);

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

		double factor = nanitesBattle == null || attacker.getNaniteCount() < attacker.getMinNaniteCount() ? 0.3 : Math
						.pow(1.1, nanitesBattle.getLevel() - 1);

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

		long attackingNanitesCount = Math.max(attacker.getNaniteCount() - attacker.getMinNaniteCount(), 0);

		long totalAttackDamage = (long) (factor * attackingNanitesCount * BASE_DAMAGE_PER_NANITE);
		L.debug("nanite group " + attacker + " attacks with count " + attackingNanitesCount
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

		if (getNanitesDao().hasVanished(attacker) || getNanitesDao().hasVanished(defender)) {
			return false;
		}

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

		// enough nanites to attack?
		if (attacker.getNaniteCount() < attacker.getMinNaniteCount()) {
			return false;
		}

		// NPC always attackable
		if (defender.getController().getUser().getState() == State.NPC) {
			return true;
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

		long fortifyingEndDateMillis = System.currentTimeMillis() + computeEntrenchingDuration(group);

		group.setState(NaniteState.ENTRENCHING);
		Date endDate = new Date(fortifyingEndDateMillis);
		group.setStateEndGT(endDate);
		getNanitesDao().update(group);
	}

	private long computeEntrenchingDuration(NaniteGroup group) {

		// for now, we need 5 Seconds for each level, multiplied by group level
		// plus one
		return group.getController().getLevel() * 5 * 1000 * (group.getGroupLevel() + 1);
	}

	public boolean canSplit(NaniteGroup nanitesGroup, boolean splitCount, boolean copyStructure) {

		return (!splitCount || nanitesGroup.getNaniteCount() > 1) && nanitesGroup.getState().canSplit()
						&& !(nanitesGroup.getPosition().getEnvironment() instanceof NaniteGroup)
						&& canRaiseGroupCount(nanitesGroup.getController());

	}

	public boolean canJumpGate(NaniteGroup naniteGroup) {
		if (naniteGroup.getState() != NaniteState.IDLE) {
			return false;
		}

		if (naniteGroup.getGroupLevel() > GateBPO.MAX_GROUP_LEVEL_FOR_GATE_TRAVEL) {
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

		if (group.getState() != NaniteState.IDLE) {
			return false;
		}

		if (group.getPosition().getEnvironment() instanceof NaniteGroup) {
			return false;
		}

		return true;

	}

	public void untrenchArena(List<NaniteGroup> naniteGroupsNearGate) {
		for (NaniteGroup naniteGroup : naniteGroupsNearGate) {
			switch (naniteGroup.getState()) {
			case ENTRENCHING:
			case ENTRENCHED:
				naniteGroup.setState(NaniteState.IDLE);
				naniteGroup.setStateEndGT(null);
				DaoManager.I.getNanitesDao().update(naniteGroup);
				new MessageBPO().send(naniteGroup, naniteGroup.getController(),
								"Eine unbeschreibliche Macht hat uns aus dem Boden gehoben und das Subraumtor gesperrt. Wir sitzen hier fest!");
				break;

			}
		}
	};

	public boolean canUpgrade(NaniteGroup naniteGroup) {

		if (naniteGroup.getController().getUser().getRole() == Role.ADMIN) {
			return true;
		}

		if (naniteGroup.getPosition().getEnvironment() instanceof NaniteGroup) {
			return false;
		}

		if (naniteGroup.getNaniteCount() < naniteGroup.getMinNaniteCount()) {
			L.debug("cannot upgrade - count below min count of nanites");
			return false;
		}

		if (naniteGroup.getState() != NaniteState.IDLE && naniteGroup.getState() != NaniteState.ENTRENCHED) {
			L.debug("cannot upgrade - neiuther entrenched nor idle");
			return false;
		}

		if (naniteGroup.getGroupLevel() >= naniteGroup.getController().getLevel() / 10) {
			L.debug("cannot upgrade - controller level too low");
			return false;
		}

		if (getTalent(naniteGroup.getController(), Appliance.NANITE_MANAGEMENT).getLevel() <= naniteGroup
						.getGroupLevel()) {
			L.debug("cannot upgrade - nanite management too low");
			return false;
		}

		if (naniteGroup.getState() == NaniteState.ENTRENCHED) {
			return true;
		}

		long nextLevelBattles = computeRequiredBattleCounteForNextGroupLevel(naniteGroup);
		if (nextLevelBattles > naniteGroup.getBattleCounter()) {
			L.debug("cannot upgrade - battle counter too low");
			return false;
		}

		return true;
	}

	public long computeRequiredBattleCounteForNextGroupLevel(NaniteGroup naniteGroup) {
		long nextLevelBattles = computeBattleCounterForGroupLevel(naniteGroup.getGroupLevel() + 1);
		return nextLevelBattles;
	}

	public long computeBattleCounterForGroupLevel(int groupLevel) {
		return (long) (25 * Math.pow(1.5, groupLevel - 1));
	}

	public void upgrade(NaniteGroup group) {
		if (!canUpgrade(group))
			return;

		group.setGroupLevel(group.getGroupLevel() + 1);
		group.setState(NaniteState.UPGRADING);
		getNanitesDao().update(group);
	}

	public long computeStructurePoints(NaniteGroup group) {
		long levelbased = (long) (LEVEL_1_STRUCTURE_POINTS * Math.pow(1.5, group.getGroupLevel()));
		return levelbased;
	}

	public int computeFreeSlots(NaniteGroup group) {
		if (group.getGroupLevel() == 0)
			return 0;
		int totalSlots = group.getGroupLevel() * 2;
		return totalSlots - group.getCatapults() - group.getNaniteSlots() - group.getSatellites() - group.getFlaks()
						- group.getAmbushSquads();
	}

	public boolean canRaiseComponents(NaniteGroup group) {
		return new NaniteBPO().computeFreeSlots(group) > 0;
	}

	public boolean canEnter(NaniteGroup object, NaniteGroup container) {
		if (object.getState() != NaniteState.IDLE) {
			return false;
		}

		if (object.getGroupLevel() >= container.getGroupLevel()) {
			return false;
		}

		if (!container.getController().equals(object.getController())) {
			return false;
		}

		if (container.getState() != NaniteState.IDLE && container.getState() != NaniteState.ENTRENCHED) {
			return false;
		}

		int count = getNanitesDao().findByEnvironment(container).size();
		return count < container.getNaniteSlots();

	}

	public void enter(NaniteGroup subject, NaniteGroup environment) {

		if (!canEnter(subject, environment)) {
			return;
		}

		Position position = new Position(environment, 0, 0);
		subject.setPosition(position);
		subject.setState(NaniteState.PASSIVATED);

		getNanitesDao().update(subject);

	}

	public boolean canExit(NaniteGroup object) {
		if (object.getPosition().getEnvironment() instanceof NaniteGroup)
			return true;
		return false;
	}

	public void exit(NaniteGroup group) {
		if (!canExit(group)) {
			return;
		}

		Position newPosition = group.getPosition().getEnvironment().getPosition();

		group.setPosition(newPosition);

		if (!newPosition.getEnvironment().needsPassivation()) {
			group.setState(NaniteState.IDLE);
		}

		getNanitesDao().update(group);

	}

	public void raiseFlaks(NaniteGroup group) {

		if (!new NaniteBPO().canRaiseComponents(group)) {
			return;
		}

		group.setFlaks(group.getFlaks() + 1);
		DaoManager.I.getNanitesDao().update(group);

	}

	public void raiseAmbushSquads(NaniteGroup group) {

		if (!new NaniteBPO().canRaiseComponents(group)) {
			return;
		}

		group.setAmbushSquads(group.getAmbushSquads() + 1);
		DaoManager.I.getNanitesDao().update(group);
	}

	/**
	 * Chrecks wether a naniteGroup basically can take another form.
	 * 
	 * @param naniteGroup
	 * @return
	 */
	public boolean canReform(NaniteGroup naniteGroup) {
		if (!naniteGroup.getState().canReform()) {
			return false;
		}
		if (naniteGroup.getNaniteCount() < naniteGroup.getMinNaniteCount()) {
			return false;
		}
		return true;
	}

	public boolean canReformTo(NaniteGroup subject, NaniteGroup template) {

		if (subject.equals(template)) {
			// same group
			return false;
		}

		if (!canReform(subject)) {
			return false;
		}

		if (!subject.getController().equals(template.getController())) {
			// different controller
			return false;
		}

		if (subject.getBattleCounter() < computeBattleCounterForGroupLevel(template.getGroupLevel())) {
			return false;
		}

		if (subject.getNaniteSlots() > template.getNaniteSlots()
						&& getNanitesDao().findByEnvironment(subject).size() > template.getNaniteSlots()) {

			// template has fewer naniteSlots AND SUBJECT CONTAINS MORE GROUPS
			// THAN TEMPLATE COULD CARRY
			return false;
		}

		return true;

	}

	public void reform(NaniteGroup subject, NaniteGroup template) {
		if (!canReformTo(subject, template)) {
			return;
		}

		copyStructureTo(template, subject);

		subject.setState(NaniteState.UPGRADING);
		subject.setStructurePoints(0);

		getNanitesDao().update(subject);
		raiseUsage(subject.getController(), Appliance.NANITE_MANAGEMENT, true);
	}

	public void copyStructureTo(NaniteGroup template, NaniteGroup target) {
		target.setCatapults(template.getCatapults());
		target.setElysium(template.isElysium());
		target.setFlaks(template.getFlaks());
		target.setGroupLevel(template.getGroupLevel());
		target.setHeight(template.getHeight());
		target.setWidth(template.getWidth());
		target.setAmbushSquads(template.getAmbushSquads());
		target.setNaniteSlots(template.getNaniteSlots());

	}

	public List<NaniteGroup> findFittingTemplates(NaniteGroup naniteGroup) {
		List<NaniteGroup> result = new LinkedList<NaniteGroup>();
		List<NaniteGroup> byController = (List<NaniteGroup>) getNanitesDao().findByController(
						naniteGroup.getController());
		for (NaniteGroup candidate : byController) {
			if (canReformTo(naniteGroup, candidate)) {
				result.add(candidate);
			}
		}

		return result;
	}
}
