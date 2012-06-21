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
import net.micwin.elysium.entities.GalaxyTimer;
import net.micwin.elysium.entities.SysParam;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.Race;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.entities.characters.User.State;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.galaxy.Sector;
import net.micwin.elysium.entities.galaxy.SolarSystem;
import net.micwin.elysium.entities.gates.Gate;

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

		ensureAdminPresent();
		ensureArenaPresent();

		getSysParamDao().create("galaxyTime", "" + (System.currentTimeMillis() + HUNDRET_YEARS_MILLIS));

		getSysParamDao().closeSession(true);
		L.info("database sanity ensured");
	}

	private void ensureArenaPresent() {
		Gate arenaGate = getGatesDao().findByGateAdress("arena");
		if (arenaGate == null) {
			L.warn("creating arena planet");
			Sector lostSector = getGalaxyBPO().createSector();
			SolarSystem lostSystem = getGalaxyBPO().createSolarSystem(lostSector);
			getGalaxyDao().save(lostSector);
			arenaGate = getGatesDao().create(new Position(lostSystem.getPlanets().get(0), 0, 0));
			arenaGate.setGateAdress("arena");
			getGatesDao().update(arenaGate, true);
		}
		L.info("arena present");

	}

	protected void ensureAdminPresent() {
		User admin = getUserDao().findByLogin("admin");
		if (admin == null) {
			admin = getUserDao().create("admin", "admin", State.ACTIVE, Role.ADMIN);
		}

		Avatar adminAvatar = getAvatarDao().findByUser(admin);

		if (adminAvatar != null)

		{
			// make sure the admin has enough points to conquer any thread
			L.debug("setting talent points of existing admin avatar to 65535");
			adminAvatar.setTalentPoints(65535);
			getAvatarDao().update(adminAvatar, true);
		}

		getUserDao().update(admin, true);

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
