package net.micwin.elysium.bpo;

import net.micwin.elysium.model.GalaxyTimer;
import net.micwin.elysium.model.SysParam;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.characters.User.Role;
import net.micwin.elysium.model.characters.User.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The bpo responsible for administrative tasks.
 * 
 * @author MicWin
 * 
 */
public class AdminBPO extends BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(AdminBPO.class);

	private static final long HUNDRET_YEARS_MILLIS = (long) (1000 * 60 * 60 * 24 * 365.25 * 100);

	/**
	 * Ensures that the initial db setup has been run. The check is done by
	 * looking up the admin user: if present, then the setup already has been
	 * done.
	 * 
	 * @return
	 */
	public synchronized void ensureInitialDbSetup() {

		User admin = getUserDao().findByLogin("admin");
		if (admin != null) {
			return;
		}

		getUserDao().create("admin", "admin", State.ACTIVE, Role.ADMIN);
		getSysParamDao().create("galaxyTime", "" + (System.currentTimeMillis() + HUNDRET_YEARS_MILLIS));

	}

	public GalaxyTimer restoreGalaxyTimer() {
		SysParam galaxyParam = getSysParamDao().findByKey("galaxyTime", "0");
		Long galaxyTime = Long.valueOf(galaxyParam.getValue());
		return new GalaxyTimer(galaxyTime);
	}

	public void saveGalaxyTimer(GalaxyTimer galaxyTimer) {
		getSysParamDao().create("galaxyTime", "" + galaxyTimer.getGalaxyDate().getTime());
		L.debug("galaxy timer saved.");
	}
}
