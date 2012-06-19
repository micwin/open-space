package net.micwin.elysium.bpo;

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
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.characters.Avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The bpo that does xp related processes.
 * 
 * @author MicWin
 * 
 */
public class XpBPO extends BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(XpBPO.class);

	/**
	 * The xp needed to reach level 1.
	 */
	private static final long XP_FOR_LEVEL_1 = 100;

	public XpBPO() {
	}

	/**
	 * The xp needed to got to the specified level.
	 * 
	 * @param level
	 * @return
	 */
	public long computeXpForLevel(int level) {
		if (level == 0)
			return 0;
		return (long) (XP_FOR_LEVEL_1 * Math.pow(1.5, level - 1));
	}

	/**
	 * Raises the avatars xp.
	 * 
	 * @param avatar
	 * @param newXp
	 */
	public void raiseXp(Avatar avatar, long newXp) {

		if (L.isDebugEnabled()) {
			L.debug("raising xp of avatar " + avatar.getName() + " by " + newXp);
			L.debug("avatar has stored level=" + avatar.getLevel() + ", xp=" + avatar.getXp());
		}

		if (newXp < 1)
			return;

		avatar.setXp(avatar.getXp() + newXp);

		long nextLevelXP = computeXpForLevel(avatar.getLevel() + 1);

		int oldLevel = avatar.getLevel();

		// only raise by 1 level and throw away overhung xp
		if (nextLevelXP <= avatar.getXp()) {

			// raise by 1 level
			avatar.setTalentPoints(avatar.getTalentPoints() + 1);
			avatar.setLevel(avatar.getLevel() + 1);
			avatar.setXp(0l);
		}

		if (avatar.getLevel() != oldLevel && L.isDebugEnabled()) {
			L.debug("avatar changed level from " + oldLevel + " to " + avatar.getLevel() + " and gained "
							+ (avatar.getLevel() - oldLevel) + " talent points");
		}
		getAvatarDao().update(avatar, false);
	}

	/**
	 * Calculates the level based bonus / malus for the specified level
	 * difference. Positive numbers get a bonus (result > 1), negative numbers a
	 * malus (result < 1, zero difference returns 1.
	 * 
	 * @param levelDiff
	 * @return
	 */
	protected double computeLevelBasedFactor(int levelDiff) {

		if (levelDiff == 0) {
			return 1;
		}

		double factorPerLevel = levelDiff > 0 ? 1.1 : 0.9;
		double factor = Math.pow(factorPerLevel, Math.abs(levelDiff));
		return factor;
	}

	/**
	 * Computes the amount of xp the attacker gets for this damage dealt.
	 * 
	 * @param attacker
	 * @param defender
	 * @param damageDoneToDefender
	 * @return
	 */
	public long computeXpForDamage(NaniteGroup attacker, NaniteGroup defender, long damageDoneToDefender) {
		double levelBasedFactor = computeLevelBasedFactor(defender.getController().getLevel()
						- attacker.getController().getLevel());
		if (L.isDebugEnabled()) {
			L.debug("level based factor for att lvl=" + attacker.getController().getLevel() + " and def lvl="
							+ defender.getController().getLevel() + " is " + levelBasedFactor);
		}
		double xp = damageDoneToDefender * levelBasedFactor;
		return (long) xp;
	}
}
