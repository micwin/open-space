package net.micwin.elysium.dao;

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
