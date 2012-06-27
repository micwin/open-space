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
import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
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

	public XpBPO() {
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
	 * Raises the usage of specified appliance by one. Checks for label
	 * overflow, max level etc.
	 * 
	 * @param avatar
	 * @param appliance
	 */
	public void raiseUsage(Avatar avatar, Appliance appliance) {

		Utilization talent = getTalent(avatar, appliance);
		int newCount = talent.getCount() + 1;

		if (L.isDebugEnabled()) {
			L.debug("trying to raise usage of utilization/appliance " + talent.getAppliance() + " of avatar '"
							+ avatar.getName() + "' from level/usages " + talent.getLevel() + "/" + talent.getCount());
			L.debug("new count is " + newCount);
		}

		talent.setCount(newCount);

		long nextLevelUsages = computeNextLevelUsages(talent);

		long usagesToGo = nextLevelUsages - talent.getCount();
		if (L.isDebugEnabled()) {
			L.debug("usages to go : " + usagesToGo);
		}

		if (talent.getLevel() < talent.getMaxLevel() && usagesToGo < 1) {
			int newLevel = talent.getLevel() + 1;
			if (L.isDebugEnabled()) {
				L.debug("raising level");
			}
			talent.setLevel(newLevel);
			talent.setCount(0);
		}

		if (L.isDebugEnabled()) {
			L.debug("new level / usages is " + talent.getLevel() + " / " + talent.getCount());
		}

		getTalentsDao().update(talent, true);

	}

	/**
	 * The number of usages of this level needed to reach next level of usage.
	 * 
	 * @param talent
	 * @return
	 */
	public long computeNextLevelUsages(Utilization talent) {

		return (long) (10 * Math.pow(1.3, talent.getLevel()));
	}
}
