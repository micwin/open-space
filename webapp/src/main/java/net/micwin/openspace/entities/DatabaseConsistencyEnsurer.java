package net.micwin.openspace.entities;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.micwin.openspace.bpo.AvatarBPO;
import net.micwin.openspace.bpo.GalaxyBPO;
import net.micwin.openspace.bpo.NaniteBPO;
import net.micwin.openspace.bpo.UserBPO;
import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.dao.IAvatarDao;
import net.micwin.openspace.dao.IGalaxyDao;
import net.micwin.openspace.dao.IGatesDao;
import net.micwin.openspace.dao.ISysParamDao;
import net.micwin.openspace.dao.ITalentsDao;
import net.micwin.openspace.dao.IUserDao;
import net.micwin.openspace.dao.TxBracelet;
import net.micwin.openspace.entities.appliances.Appliance;
import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.characters.Organization;
import net.micwin.openspace.entities.characters.Race;
import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.entities.characters.User.Role;
import net.micwin.openspace.entities.characters.User.State;
import net.micwin.openspace.entities.galaxy.Planet;
import net.micwin.openspace.entities.galaxy.Position;
import net.micwin.openspace.entities.galaxy.Sector;
import net.micwin.openspace.entities.galaxy.SolarSystem;
import net.micwin.openspace.entities.gates.Gate;
import net.micwin.openspace.jobs.NPCAdvancer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simply said, a sort of special DAO that manages its own hibernate session to
 * be able to run out of session in view pattern..
 * 
 * @author MicWin
 * 
 */

@Transactional
public class DatabaseConsistencyEnsurer {

	/**
	 * The level each admin talent gets from the start.
	 */
	private static final int ADMIN_TALENTS_LEVEL = 65535;

	private static final long HUNDRET_YEARS_MILLIS = (long) (1000 * 60 * 60 * 24 * 365.25 * 100);

	/**
	 * THis works as a marker what the current database version is.
	 */
	private static final SysParam DEFAULT_DATABASE_VERSION = new SysParam("dbVersion", "3");

	private static final Logger L = org.slf4j.LoggerFactory.getLogger(DatabaseConsistencyEnsurer.class);

	protected SysParam dbVersion;

	private SessionFactory sessionFactory;

	/**
	 * Ensures database consistency. If missing, creates admin, first sector,
	 * lost sector, first solar system etc pp.
	 */
	@Transactional
	public void ensureDatabaseConsistency() {

		L.info("starting DatabaseEnsurer");
		/**
		 * Do all in a session managed by hibernate.
		 */

		Session session = sessionFactory.getCurrentSession();

		session.beginTransaction();

		loadDbVersion();
		createInitialDbEntries();
		migrateToV2(session);
		insertNPC();
		correctMessages();
		clearOutGarbage();

		session.getTransaction().commit();

		L.info("closing session after data consistency check");
	}

	private void correctMessages() {

		Integer count = new TxBracelet<Integer>(sessionFactory) {

			@Override
			public Integer doWork(Session session, Transaction tx) {
				return session.createQuery("update Message set mailBox=receiverID where mailbox is null")
								.executeUpdate();
			}
		}.execute();

		L.info(count + " message mailboxes set to receiverId");
	}

	/**
	 * Insert an npc into the game.
	 */
	private void insertNPC() {
		
		// step 1 : look wehter is must get created
		Collection<Avatar> result = DaoManager.I.getAvatarDao().findByStringProperty("name", NPCAdvancer.NAME_AI_0);
		Avatar npcAvatar = result.isEmpty() ? null : result.iterator().next();

		if (npcAvatar == null) {
			String pass = NPCAdvancer.NAME_AI_0 + (Math.random() * Integer.MAX_VALUE);
			new UserBPO().register(NPCAdvancer.NAME_AI_0, pass, pass);
			User npcUser = DaoManager.I.getUserDao().findByLogin(NPCAdvancer.NAME_AI_0);
			npcAvatar = new AvatarBPO().create(npcUser, NPCAdvancer.NAME_AI_0, Race.NANITE, true);
			new NaniteBPO().entrench(npcAvatar.getNanites().iterator().next());
			npcUser.setLastLoginDate(new Date());
			npcUser.setState(State.NPC);
			DaoManager.I.getUserDao().update(npcUser);
		}
	}

	private void clearOutGarbage() {
	}

	private void loadDbVersion() {

		dbVersion = getSysParamDao().findByKey(DEFAULT_DATABASE_VERSION.getKey(), null);
		L.info("current db version is " + dbVersion);

	}

	private void createInitialDbEntries() {

		if (dbVersion == null) {
			L.info("----------------------------------------");
			L.info("initial database setup to V" + DEFAULT_DATABASE_VERSION.getValue() + "...");
			L.info("----------------------------------------");
			checkAvatars();
			ensureLostSystemPresent();
			L.info("lost systems ensured.");
			ensureAdminPresent();
			L.info("admin ensured.");

			ensureAcademyPresent();
			L.info("academy ensured.");
			ensureNoStory();
			ensureScanningPresent();
			L.info("scanning presence ensured.");
			setDbVersion(Integer.valueOf(DEFAULT_DATABASE_VERSION.getValue()));
			L.info("migration to V" + DEFAULT_DATABASE_VERSION.getValue() + " done");
		}
	}

	private void setDbVersion(int version) {
		if (dbVersion == null) {
			dbVersion = new SysParam(DEFAULT_DATABASE_VERSION.getKey(), "" + version);
			getSysParamDao().insert(dbVersion);
		} else {
			dbVersion.setValue("" + version);
			getSysParamDao().update(dbVersion);
		}

	}

	public void migrateToV2(Session session) {

		if (dbVersion.getValue().equals("1")) {

			L.info("----------------------------------------");
			L.info("migrating database to V2 ...");
			L.info("----------------------------------------");

			try {
				int result = sessionFactory.getCurrentSession()
								.createSQLQuery("ALTER TABLE Avatar ADD COLUMN DEATHCOUNT int default 0")
								.executeUpdate();
			} catch (SQLGrammarException e) {

				// checking for "Column already exists"

				if (e.getCause().getMessage().contains("Column already exists")) {
					L.error("cannot alter table Avatar for having DEATHCOUNT - already present.");
				} else
					throw e;
			}

			setDbVersion(2);
			L.info("migration done.");
		}

		L.info("database sanity ensured");

	}

	private void ensureScanningPresent() {

		Collection<Avatar> allAvatars = getAvatarDao().loadAll();
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
			getTalentsDao().insert(scanning);

			L.info("adding " + scanning + " to avatar " + avatar);

			talents.add(scanning);
			avatar.setTalents(talents);
			getAvatarDao().update(avatar);
		}

	}

	protected void checkAvatars() {

		// check skills and controllers
		Collection<Avatar> all = getAvatarDao().loadAll();

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
					getTalentsDao().update(utilization);
					addedSomething = true;
				}

				if (addedSomething) {
					getAvatarDao().update(avatar);
				}
			}
		}

	}

	private void ensureNoStory() {
		int itemCount = 0;

		Collection<Avatar> allAvatars = getAvatarDao().loadAll();
		for (Iterator<Avatar> iterator = allAvatars.iterator(); iterator.hasNext();) {
			Avatar avatar = iterator.next();

			if (avatar.getStoryLineItem() != null) {
				L.info("clearing story line item '" + avatar.getStoryLineItem() + "' of avatar " + avatar);
				avatar.setStoryLineItem(null);
				itemCount++;
			}
		}
		getAvatarDao().update(allAvatars);

		L.info(itemCount + " story items killed");

	}

	private void ensureAcademyPresent() {
		User adminUser = getUserDao().findByStringProperty("login", "admin").iterator().next();
		Avatar admin = getAvatarDao().findByUser(adminUser);

		if (admin.getOrganization() == null) {
			L.info("admin does not have a organization assigned - create OSA");
			Organization orga = Organization.create("Open Space Academy", "OSA");
			orga.setController(admin);
			DaoManager.I.getOrganizationDao().insert(orga);
			admin.setOrganization(orga);
			DaoManager.I.getAvatarDao().update(admin);
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
			getGatesDao().update(arenaGate);

			// then, elysium
			Planet elysium = lostSystem.getPlanets().get(lostSystem.getPlanets().size() - 1);
			elysium.setElysium(true);
			Gate elysiumGate = getGatesDao().create(new Position(elysium, 0, 0));
			elysiumGate.setGateAdress("elysium");
			getGatesDao().update(elysiumGate);
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
						getGatesDao().delete(gate);
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
		getTalentsDao().update(talents);

		DaoManager.I.getAvatarDao().update(adminAvatar);
		L.info("admin avatar rectified");

		DaoManager.I.getUserDao().update(admin);

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

	private ISysParamDao getSysParamDao() {
		return DaoManager.I.getSysParamDao();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
