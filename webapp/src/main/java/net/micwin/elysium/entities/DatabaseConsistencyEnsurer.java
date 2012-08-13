package net.micwin.elysium.entities;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.bpo.GalaxyBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.dao.IAvatarDao;
import net.micwin.elysium.dao.IGalaxyDao;
import net.micwin.elysium.dao.IGatesDao;
import net.micwin.elysium.dao.ISysParamDao;
import net.micwin.elysium.dao.ITalentsDao;
import net.micwin.elysium.dao.IUserDao;
import net.micwin.elysium.entities.appliances.Appliance;
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
import org.hibernate.exception.SQLGrammarException;
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

	/**
	 * THis works as a marker what the current database version is.
	 */
	private static final SysParam DEFAULT_DATABASE_VERSION = new SysParam("dbVersion", "1");

	private static final Logger L = org.slf4j.LoggerFactory.getLogger(DatabaseConsistencyEnsurer.class);

	protected SysParam dbVersion;

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

				loadDbVersion();
				migrateToV1(session);

				L.info("closing session after data consistency check");

				return null;
			}

			private void loadDbVersion() {

				dbVersion = getSysParamDao().findByKey(DEFAULT_DATABASE_VERSION.getKey(), null);

			}

			private void migrateToV1(Session session) {

				if (dbVersion == null) {
					insertDeathCount();
					checkAvatars();
					checkNaniteGroups();
					ensureLostSystemPresent();
					L.info("lost systems ensured.");
					ensureAdminPresent();
					L.info("admin ensured.");
					session.flush();
					ensureAcademyPresent();
					L.info("academy ensured.");
					session.flush();
					ensureNoStory();
					ensureScanningPresent();
					L.info("scanning presence ensured.");
					setDbVersion(1);
				}
			}

			private void setDbVersion(int version) {
				if (dbVersion == null) {
					dbVersion = new SysParam(DEFAULT_DATABASE_VERSION.getKey(), "" + version);
					getSysParamDao().insert(dbVersion, true);
				} else {
					dbVersion.setValue("" + version);
					getSysParamDao().update(dbVersion, true);
				}

			}

			public void insertDeathCount() {
				try {
					int result = getSession().createSQLQuery("ALTER TABLE Avatar ADD COLUMN DEATHCOUNT int default 0")
									.executeUpdate();
				} catch (SQLGrammarException e) {

					// checking for "Column already exists"

					if (e.getCause().getMessage().contains("Column already exists")) {
						L.error("cannot alter table Avatar for having DEATHCOUNT - already present.");
					} else
						throw e;
				}

			}

			private void checkNaniteGroups() {

				LinkedList<NaniteGroup> all = new LinkedList<NaniteGroup>();
				DaoManager.I.getNanitesDao().loadAll(all);
				for (NaniteGroup naniteGroup : all) {
					if (naniteGroup.getController() == null) {
						L.info("removing orphaned nanite group " + naniteGroup);
						DaoManager.I.getNanitesDao().delete(naniteGroup, true);
					}
				}

			}

			private void ensureScanningPresent() {

				Collection<Avatar> allAvatars = getAvatarDao().loadAll(null);
				avatarLoop: for (Iterator<Avatar> iterator = allAvatars.iterator(); iterator.hasNext();) {
					Avatar avatar = iterator.next();

					Collection<Utilization> talents = getTalentsDao().findByController(avatar);

					for (Iterator<Utilization> talentsIter = talents.iterator(); talentsIter.hasNext();) {
						Utilization talent = talentsIter.next();
						if (talent.getAppliance() == Appliance.SHORT_RANGE_SCANS) {
							// found; check next avatar
							continue avatarLoop;
						}

					}

					// not found; adding.

					Utilization scanning = Utilization.Factory.create(Appliance.SHORT_RANGE_SCANS, 0, 99);
					getTalentsDao().insert(scanning, true);

					L.info("adding " + scanning + " to avatar " + avatar);

					talents.add(scanning);
					avatar.setTalents(talents);
					getAvatarDao().update(avatar, false);
				}

				getAvatarDao().flush();

			}

		});

		L.info("database sanity ensured");

	}

	protected void checkAvatars() {

		// check skills and controllers
		LinkedList<Avatar> all = new LinkedList<Avatar>();
		getAvatarDao().loadAll(all);

		for (Avatar avatar : all) {

			boolean addedSomething = false;

			for (Utilization initialTalent : avatar.getPersonality().getInitialTalents()) {

				Utilization talent = new AvatarBPO().getTalent(avatar, initialTalent.getAppliance());

				if (talent == null) {
					L.info("adding skill '" + initialTalent.getAppliance() + "' to avatar '" + avatar.getName() + "'");

					Utilization utilization = Utilization.Factory.create(initialTalent.getAppliance(),
									initialTalent.getLevel(), initialTalent.getMaxLevel());

					utilization.setController(avatar);
					avatar.getTalents().add(utilization);
					getTalentsDao().update(utilization, true);
					addedSomething = true;
				}

				if (addedSomething) {
					getAvatarDao().update(avatar, true);
				}
			}
		}

	}

	private void ensureNoStory() {
		int itemCount = 0;

		Collection<Avatar> allAvatars = getAvatarDao().loadAll(null);
		for (Iterator<Avatar> iterator = allAvatars.iterator(); iterator.hasNext();) {
			Avatar avatar = iterator.next();

			if (avatar.getStoryLineItem() != null) {
				L.info("clearing story line item '" + avatar.getStoryLineItem() + "' of avatar " + avatar);
				avatar.setStoryLineItem(null);
				itemCount++;
			}
		}
		getAvatarDao().update(allAvatars, true);

		L.info(itemCount + " story items killed");

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
			L.info("arena found - assuming lost sector is present");

			// cross checking wether the arena environment sticks to the correct
			// gate
			Planet arenaPlanet = (Planet) arenaGate.getPosition().getEnvironment();
			Collection<Gate> gatesKnottedToArenaPlanet = getGatesDao().findByEnvironment(arenaPlanet);
			if (gatesKnottedToArenaPlanet.size() > 1) {
				L.warn("too many gates point to arena - rectifying ...");
				for (Gate gate : gatesKnottedToArenaPlanet) {
					if (!gate.equals(arenaGate)) {
						L.warn("deleting gate " + gate.getGateAdress() + " ...");
						getGatesDao().delete(gate, true);
					}
				}
			}

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

		L.info("checking for admin avatar ...");

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
		Collection<Utilization> talents = getTalentsDao().findByController(adminAvatar);

		for (Utilization talent : talents) {
			talent.setLevel(ADMIN_TALENTS_LEVEL);
		}
		getTalentsDao().update(talents, true);

		DaoManager.I.getAvatarDao().update(adminAvatar, true);
		L.info("admin avatar rectified");

		DaoManager.I.getUserDao().update(admin, true);

		L.info("admin user rectified");
	}

	private IAvatarDao getAvatarDao() {
		return DaoManager.I.getAvatarDao();
	}

	private ITalentsDao getTalentsDao() {
		return DaoManager.I.getTalentsDao();
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
