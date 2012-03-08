package net.micwin.elysium.bpo;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.dao.IAvatarDao;
import net.micwin.elysium.dao.IBluePrintDao;
import net.micwin.elysium.dao.IGalaxyDao;
import net.micwin.elysium.dao.INanitesDao;
import net.micwin.elysium.dao.ISysParamDao;
import net.micwin.elysium.dao.IUserDao;
import net.micwin.elysium.model.GalaxyTimer;
import net.micwin.elysium.view.ElysiumApplication;

import org.apache.wicket.Application;

/**
 * some base utilities for BPOs.
 * 
 * @author MicWin
 * 
 */
public class BaseBPO {

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
		return ((ElysiumApplication) Application.get()).getGalaxyTimer();
	}

	public IGalaxyDao getGalaxyDao() {
		return getDaoManager().getGalaxyDao();
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

}
