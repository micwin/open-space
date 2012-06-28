package net.micwin.elysium.entities;

import java.sql.SQLException;
import java.util.Date;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.bpo.GalaxyBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.dao.IAvatarDao;
import net.micwin.elysium.dao.IGalaxyDao;
import net.micwin.elysium.dao.IGatesDao;
import net.micwin.elysium.dao.IOrganizationDao;
import net.micwin.elysium.dao.ISysParamDao;
import net.micwin.elysium.dao.IUserDao;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.Organization;
import net.micwin.elysium.entities.characters.Race;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.entities.characters.User.State;
import net.micwin.elysium.entities.galaxy.Planet;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.galaxy.Sector;
import net.micwin.elysium.entities.galaxy.SolarSystem;
import net.micwin.elysium.entities.gates.Gate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Simply said, a sort of special DAO that manages its own hibernate session to
 * be able to run out of session in view pattern..
 * 
 * @author MicWin
 * 
 */

public class DatabaseConsistencyEnsurer extends HibernateDaoSupport {

	/**
	 * The level each admin talent gets from the start.
	 */
	private static final int ADMIN_TALENTS_LEVEL = 65535;

	private static final long HUNDRET_YEARS_MILLIS = (long) (1000 * 60 * 60 * 24 * 365.25 * 100);

	private static final Logger L = org.slf4j.LoggerFactory.getLogger(DatabaseConsistencyEnsurer.class);

	/**
	 * Ensures database consistency. If missing, creates admin, first sector,
	 * lost sector, first solar system etc pp.
	 */
	public void ensureDatabaseConsistency() {

		GalaxyTimer.set(loadGalaxyTimer());

		/**
		 * Do all in a session managed by hibernate.
		 */
		getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {

				ensureLostSystemPresent();
				L.info("lost systems ensured.");
				ensureAdminPresent();
				L.info("admin ensured.");
				session.flush();
				ensureAcademyPresent();
				L.info("academy ensured.");
				session.flush();
				L.info("closing session after data consistency check");

				return null;
			}
		});

		L.info("database sanity ensured");

	}

	private void ensureAcademyPresent() {
		User adminUser = getUserDao().findByStringProperty("login", "admin").iterator().next();
		Avatar admin = getAvatarDao().findByUser(adminUser);

		if (admin.getOrganization() == null) {
			L.info("admin does not have a organization assigned - create OSA");
			Organization orga = Organization.create("Open Space Academy", "OSA");
			orga.setController(admin);
			DaoManager.I.getOrganizationDao().insert(orga, false);
			admin.setOrganization(orga);
			DaoManager.I.getAvatarDao().update(admin, false);
			L.info("Open Space Academy created and admin made controller");
		}
	}

	private void ensureLostSystemPresent() {

		L.info("checking presence of lost sector ...");
		Gate arenaGate = getGatesDao().findByGateAdress("arena");
		if (arenaGate == null) {
			// creating lost sector
			L.warn("creating lost sector");

			Sector lostSector = getGalaxyBPO().createSector(10000, 10000);
			SolarSystem lostSystem = getGalaxyBPO().createSolarSystem(lostSector);
			getGalaxyDao().save(lostSector);

			L.warn("creating arena and elysium planets");

			// first, arena
			arenaGate = getGatesDao().create(new Position(lostSystem.getPlanets().get(0), 0, 0));
			arenaGate.setGateAdress("arena");
			getGatesDao().update(arenaGate, false);

			// then, elysium
			Planet elysium = lostSystem.getPlanets().get(lostSystem.getPlanets().size() - 1);
			elysium.setElysium(true);
			Gate elysiumGate = getGatesDao().create(new Position(elysium, 0, 0));
			elysiumGate.setGateAdress("elysium");
			getGatesDao().update(elysiumGate, false);
			getGalaxyDao().save(elysium);

			L.info("lost sector  created");
		} else {
			L.info("arena lost sector present");
		}

	}

	private IGalaxyDao getGalaxyDao() {
		return DaoManager.I.getGalaxyDao();
	}

	private GalaxyBPO getGalaxyBPO() {
		return new GalaxyBPO();
	}

	private IGatesDao getGatesDao() {
		return DaoManager.I.getGatesDao();
	}

	protected void ensureAdminPresent() {
		L.info("checking for admin present...");
		User admin = getUserDao().findByLogin("admin");
		if (admin == null) {
			L.info("admin missing - creating one ...");
			admin = getUserDao().create("admin", "admin", State.ACTIVE, Role.ADMIN);
			getUserDao().flush();
		} else {
			L.info("admin already present");
		}

		L.info("checking for admins avatar ...");

		Avatar adminAvatar = getAvatarDao().findByUser(admin);

		if (adminAvatar == null) {
			L.info("create admin avatar...");
			adminAvatar = new AvatarBPO().create(admin, "admin", Race.NANITE, false);
		}

		if (adminAvatar == null) {
			throw new IllegalStateException("adminAvatar still not present!");
		}
		L.info("Checking skills");

		// make sure the admin has enough points to conquer any thread
		for (Utilization talent : adminAvatar.getTalents()) {
			talent.setLevel(ADMIN_TALENTS_LEVEL);
		}

		DaoManager.I.getAvatarDao().update(adminAvatar, true);
		L.info("admin avatar rectified");

		DaoManager.I.getUserDao().update(admin, true);

		L.info("admin user rectified");
	}

	private IAvatarDao getAvatarDao() {
		return DaoManager.I.getAvatarDao();
	}

	private IUserDao getUserDao() {
		return DaoManager.I.getUserDao();
	}

	public void update(final GalaxyTimer galaxyTimer) {

		getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SysParam galaxyTimeparam = getSysParamDao().findByKey("galaxyTime", null);
				if (galaxyTimeparam == null) {
					galaxyTimeparam = getSysParamDao().create("galaxyTime", "" + galaxyTimer.getGalaxyDate().getTime());

				} else {
					galaxyTimeparam.setValue("" + galaxyTimer.getGalaxyDate().getTime());

				}
				session.update(galaxyTimeparam);
				return null;
			}
		});

	}

	private ISysParamDao getSysParamDao() {
		return DaoManager.I.getSysParamDao();
	}

	public GalaxyTimer loadGalaxyTimer() {

		GalaxyTimer galaxyTimer = (GalaxyTimer) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public GalaxyTimer doInHibernate(Session session) throws HibernateException, SQLException {
				SysParam galaxyTimeParam = getSysParamDao().findByKey("galaxyTime", null);
				if (galaxyTimeParam == null)
					galaxyTimeParam = getSysParamDao().create("galaxyTime",
									"" + (System.currentTimeMillis() + HUNDRET_YEARS_MILLIS));

				return new GalaxyTimer(Long.valueOf(galaxyTimeParam.getValue()));

			}

		});

		return galaxyTimer;
	}
}
