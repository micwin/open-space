package net.micwin.elysium.dao;

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
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.SysParam;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.Organization;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.engineering.BluePrint;
import net.micwin.elysium.entities.engineering.BuildPlan;
import net.micwin.elysium.entities.galaxy.Galaxy;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.entities.messaging.Message;

public class DaoManager {

	private HashMap<Class, IElysiumEntityDao> daoInstances = new HashMap<Class, IElysiumEntityDao>();

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

	@Autowired
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

	@Autowired
	public void setAvatarDao(IAvatarDao avatarDao) {
		checkDaoNotPresent(Avatar.class);
		daoInstances.put(Avatar.class, avatarDao);
	}

	public INanitesDao getNanitesDao() {
		return (INanitesDao) getDao(NaniteGroup.class);
	}

	@Autowired
	public void setNanitesDao(INanitesDao nanitesDao) {
		checkDaoNotPresent(NaniteGroup.class);
		daoInstances.put(NaniteGroup.class, nanitesDao);
	}

	public IBluePrintDao getBluePrintDao() {

		return (IBluePrintDao) getDao(BluePrint.class);
	}

	@Autowired
	public void setBluePrintDao(IBluePrintDao bluePrintDao) {
		checkDaoNotPresent(BluePrint.class);
		daoInstances.put(BluePrint.class, bluePrintDao);
	}

	@Autowired
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

	@Autowired
	public void setBuildPlanDao(IBuildPlanDao buildPlanDao) {

		checkDaoNotPresent(BuildPlan.class);
		daoInstances.put(BuildPlan.class, buildPlanDao);
	}

	@SuppressWarnings("unchecked")
	public <T extends ElysiumEntity> IElysiumEntityDao<T> getDao(Class<T> entityClass) {
		return daoInstances.get(entityClass);
	}

	public IGalaxyDao getGalaxyDao() {
		return (IGalaxyDao) getDao(Galaxy.class);
	}

	@Autowired
	public void setGalaxyDao(IGalaxyDao galaxyDao) {
		checkDaoNotPresent(Galaxy.class);
		daoInstances.put(Galaxy.class, galaxyDao);
	}

	public ITalentsDao getTalentsDao() {
		return (ITalentsDao) getDao(Utilization.class);
	}

	@Autowired
	public void setTalentsDao(ITalentsDao talentsDao) {
		checkDaoNotPresent(Utilization.class);
		daoInstances.put(Utilization.class, talentsDao);
	}

	public IGatesDao getGatesDao() {
		return (IGatesDao) getDao(Gate.class);
	}

	@Autowired
	public void setGatesDao(IGatesDao gatesDao) {
		checkDaoNotPresent(Gate.class);
		daoInstances.put(Gate.class, gatesDao);
	}

	public IOrganizationDao getOrganizationDao() {
		return (IOrganizationDao) getDao(Organization.class);
	}

	@Autowired
	public void setOrganizationDao(IOrganizationDao orgaDao) {
		checkDaoNotPresent(Organization.class);
		daoInstances.put(Organization.class, orgaDao);
	}

	public IMessageDao getMessageDao() {
		return (IMessageDao) getDao(Message.class);
	}

	@Autowired
	public void setMessageDao(IMessageDao msgDao) {
		checkDaoNotPresent(Message.class);
		daoInstances.put(Message.class, msgDao);
	}
}
