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

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.dao.IAvatarDao;
import net.micwin.elysium.dao.IBluePrintDao;
import net.micwin.elysium.dao.IGalaxyDao;
import net.micwin.elysium.dao.IGatesDao;
import net.micwin.elysium.dao.INanitesDao;
import net.micwin.elysium.dao.ISysParamDao;
import net.micwin.elysium.dao.ITalentsDao;
import net.micwin.elysium.dao.IUserDao;
import net.micwin.elysium.entities.GalaxyTimer;
import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * some base utilities for BPOs.
 * 
 * @author MicWin
 * 
 */
public class BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(BaseBPO.class);

	private static DaoManager daoManager;

	protected IUserDao getUserDao() {
		return getDaoManager().getUserDao();
	}

	protected IAvatarDao getAvatarDao() {
		return getDaoManager().getAvatarDao();
	}

	public ISysParamDao getSysParamDao() {
		return getDaoManager().getSysParamDao();
	}

	public GalaxyTimer getGalaxyTimer() {
		return GalaxyTimer.get();
	}

	public IGalaxyDao getGalaxyDao() {
		return getDaoManager().getGalaxyDao();
	}

	public ITalentsDao getTalentsDao() {
		return getDaoManager().getTalentsDao();
	}

	public INanitesDao getNanitesDao() {
		return getDaoManager().getNanitesDao();
	}

	public IBluePrintDao getBluePrintDao() {
		return getDaoManager().getBluePrintDao();
	}


	/**
	 * DONT CALL THIS METHOD! Its a hack and surely will be replaced by a more
	 * elegant construct.
	 * 
	 * @param newDaoManager
	 */
	@Deprecated
	public void setDaoManager(DaoManager newDaoManager) {
		daoManager = newDaoManager;
	}

	protected DaoManager getDaoManager() {
		return daoManager;
	}

	protected IGatesDao getGatesDao() {
		return getDaoManager().getGatesDao();
	}

	/**
	 * Retrieve a specific talent from a person.
	 * 
	 * @param person
	 * @param appliance
	 * @param createIfMissing
	 * @return
	 */
	public Utilization getTalent(Avatar person, Appliance appliance) {

		if (L.isDebugEnabled()) {
			L.debug("retrieving talent '" + appliance + "' for avatar '" + person + "'");
		}
		for (Utilization talent : person.getTalents()) {
			if (talent.getAppliance() == appliance) {
				return talent;
			}
		}

		return null;

	}

	protected GalaxyBPO getGalaxyBPO() {
		return new GalaxyBPO();
	}

	/**
	 * Flushes all data changes being cached in memory to the database.
	 */
	protected void flush() {
		getSysParamDao().flush();
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
	public void raiseUsage(Avatar avatar, Appliance appliance, boolean createIfMissing) {

		Utilization talent = getTalent(avatar, appliance);

		if (talent == null && createIfMissing) {

			talent = Utilization.Factory.create(appliance, 0, 99);
			getTalentsDao().insert(talent, true);

			L.info("adding " + talent + " to avatar " + avatar);
			avatar.getTalents().add(talent);
			getAvatarDao().update(avatar, false);
		}
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
