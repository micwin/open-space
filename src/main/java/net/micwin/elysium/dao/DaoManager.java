package net.micwin.elysium.dao;

/* This file is part of open-space.

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
 OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

import java.util.HashMap;

import net.micwin.elysium.model.ElysiumEntity;
import net.micwin.elysium.model.NanoBotHive;
import net.micwin.elysium.model.SysParam;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.galaxy.Galaxy;
import net.micwin.elysium.model.replication.BluePrint;
import net.micwin.elysium.model.replication.BuildPlan;

public class DaoManager {

	private HashMap<Class<? extends ElysiumEntity>, IElysiumEntityDao<? extends ElysiumEntity>> daoInstances = new HashMap<Class<? extends ElysiumEntity>, IElysiumEntityDao<? extends ElysiumEntity>>();

	public static DaoManager I;

	public DaoManager() {
		if (I != null) {
			throw new IllegalStateException("already initialized!");
		}

		I = this;
	}

	public IUserDao getUserDao() {
		return (IUserDao) getDao(User.class);
	}

	public void setUserDao(IUserDao userDao) {
		checkDaoNotPresent(User.class);
		daoInstances.put(User.class, userDao);

	}

	private void checkDaoNotPresent(Class<? extends ElysiumEntity> class1) {
		if (daoInstances.containsKey(class1)) {
			throw new IllegalStateException("dao for entity '" + class1 + "' already present");
		}

	}

	public IAvatarDao getAvatarDao() {
		return (IAvatarDao) getDao(Avatar.class);
	}

	public void setAvatarDao(IAvatarDao avatarDao) {
		checkDaoNotPresent(Avatar.class);
		daoInstances.put(Avatar.class, avatarDao);
	}

	public INanitesDao getNanitesDao() {
		return (INanitesDao) getDao(NanoBotHive.class);
	}

	public void setNanitesDao(INanitesDao nanitesDao) {
		checkDaoNotPresent(NanoBotHive.class);
		daoInstances.put(NanoBotHive.class, nanitesDao);
	}

	public IBluePrintDao getBluePrintDao() {

		return (IBluePrintDao) getDao(BluePrint.class);
	}

	public void setBluePrintDao(IBluePrintDao bluePrintDao) {
		checkDaoNotPresent(BluePrint.class);
		daoInstances.put(BluePrint.class, bluePrintDao);
	}

	public void setSysParamDao(ISysParamDao sysParamDao) {
		checkDaoNotPresent(SysParam.class);
		daoInstances.put(SysParam.class, sysParamDao);
	}

	public ISysParamDao getSysParamDao() {
		return (ISysParamDao) getDao(SysParam.class);
	}

	public IBuildPlanDao getBuildPlanDao() {
		return (IBuildPlanDao) getDao(BuildPlan.class);
	}

	public void setBuildPlanDao(IBuildPlanDao buildPlanDao) {

		checkDaoNotPresent(BuildPlan.class);
		daoInstances.put(BuildPlan.class, buildPlanDao);
	}

	public IElysiumEntityDao<? extends ElysiumEntity> getDao(Class<? extends ElysiumEntity> entityClass) {
		return daoInstances.get(entityClass);
	}

	public IGalaxyDao getGalaxyDao() {
		return (IGalaxyDao) getDao(Galaxy.class);
	}

	public void setGalaxyDao(IGalaxyDao galaxyDao) {
		checkDaoNotPresent(Galaxy.class);
		daoInstances.put(Galaxy.class, galaxyDao);
	}

}
