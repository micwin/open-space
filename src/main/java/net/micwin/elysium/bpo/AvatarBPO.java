package net.micwin.elysium.bpo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.Constants;
import net.micwin.elysium.MessageKeys;
import net.micwin.elysium.model.appliances.Appliance;
import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.Avatar.Personality;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.galaxy.Planet;
import net.micwin.elysium.model.galaxy.Position;
import net.micwin.elysium.model.galaxy.Sector;
import net.micwin.elysium.model.galaxy.SolarSystem;
import net.micwin.elysium.model.replication.BluePrint;

public class AvatarBPO extends BaseBPO {

	public AvatarBPO() {
	}

	/**
	 * Creates a new Avatar with the given values.
	 * 
	 * @param user
	 * @param name
	 * @param personality
	 * @return
	 */
	public String create(User user, String name, Personality personality) {
		String error = validate(user, name, personality);
		if (error != null) {
			return error;
		}

		Collection<Utilization> talentsList = fillInTalents(personality);

		Sector thinnestSector = getGalaxyDao().findThinnestSector();

		if (thinnestSector == null) {
			thinnestSector = getGalaxyBPO().createSector();

		}
		SolarSystem solarSystem = getGalaxyBPO().createSolarSystem(thinnestSector);

		Position position = randomizeStartingPosition(solarSystem.getMainPlanet());

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(getGalaxyTimer().getGalaxyDate());
		cal.roll(Calendar.YEAR, -25);

		Date birthDate = cal.getTime();

		Avatar avatar = getAvatarDao().create(user, name, personality, talentsList,
						Constants.TALENT_POINTS_UPON_CREATION, position, birthDate);

		BluePrint baseBase = getBluePrintDao().create(avatar, MessageKeys.PLANETARY_BASE_STRUCTURE,
						Utilization.Factory.create(Appliance.ARCHITECTURE, 1),
						Utilization.Factory.create(Appliance.HABITATS, 1));

		return null;
	}

	private Position randomizeStartingPosition(Planet mainPlanet) {
		Position position = new Position();
		position.setEnvironment(mainPlanet);
		position.setX((int) (Math.random() * mainPlanet.getWidth()));
		position.setY((int) (Math.random() * mainPlanet.getHeight()));
		return position;
	}

	protected List<Utilization> fillInTalents(Personality personality) {
		List<Utilization> talentsList = new LinkedList<Utilization>();

		switch (personality) {
		// case MILITARY:
		// talentsList.add(new Talent(Appliance.HUMAN_INTERACTION, 0, 0, 0,
		// Talent.SCOPE_DISABLED));
		// talentsList
		// .add(new Talent(Appliance.OFFENSIVE_WARFARE, 5, 1, 1, 1));
		// talentsList.add(new Talent(Appliance.NANITE_WARFARE, 0, 0, 0, 0));
		// talentsList.add(new Talent(Appliance.MARTIAL_ARTS, 2, 0, 0, 1));
		//
		// break;
		case ENGINEER:
			talentsList.add(Utilization.Factory.create(Appliance.ARCHITECTURE, 1));
			talentsList.add(Utilization.Factory.create(Appliance.HABITATS, 1));
			break;

		// case PRESERVER:
		// talentsList
		// .add(new Talent(Appliance.HUMAN_INTERACTION, 4, 3, 2, 1));
		// talentsList.add(new Talent(Appliance.MARTIAL_ARTS, 0, 0, 0, 0));
		// talentsList
		// .add(new Talent(Appliance.DEFENSIVE_WARFARE, 5, 5, 5, 5));
		// talentsList.add(new Talent(Appliance.RESEARCH, 0, 0, 0, 5));
		//
		// break;
		default:
			throw new IllegalStateException("case '" + personality.name() + "' not covered");

		}

		return talentsList;
	}

	public String validate(User user, String name, Personality personality) {
		Avatar alreadyExisting = getAvatarDao().findByController(user);
		if (alreadyExisting != null) {
			return MessageKeys.EK_USER_ALREADY_HAS_AN_AVATAR;
		}

		name = name.trim();
		if (name.length() < 6) {
			return MessageKeys.EK_NAME_TOO_SHORT;
		} else if (name.length() > 25) {
			return MessageKeys.EK_NAME_TOO_LONG;
		}

		if (personality == null) {
			return MessageKeys.EK_NO_PERSONALITY_SELECTED;
		}
		return null;
	}

	public boolean hasAvatar(User user) {

		Avatar avatar = getAvatarDao().findByController(user);
		return avatar != null;
	}

	public Avatar findByController(User user) {
		return getAvatarDao().findByController(user);
	}

	protected GalaxyBPO getGalaxyBPO() {
		return new GalaxyBPO();
	}

	public List<BluePrint> getBluePrintsUsableBy(Avatar avatar) {
		return getBluePrintDao().findByController(avatar);
	}
}
