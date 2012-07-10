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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.MessageKeys;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.Race;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.entities.galaxy.Planet;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.galaxy.Sector;
import net.micwin.elysium.entities.galaxy.SolarSystem;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.entities.replication.BluePrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvatarBPO extends BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(AvatarBPO.class);

	public AvatarBPO() {
	}

	/**
	 * Creates a new Avatar with the given values.
	 * 
	 * @param user
	 * @param name
	 * @param race
	 * @param createSystem
	 *            wether or not to create a system for this character. If not,
	 *            simply place it on the elysium planet.
	 * @return
	 */
	public Avatar create(User user, String name, Race race, boolean createSystem) {

		Collection<Utilization> talentsList = getTalentsDao().createInitialTalents(race);

		for (Utilization utilization : talentsList) {

			getTalentsDao().insert(utilization, true);

		}		
		
		Position position;

		if (createSystem) {

			Sector thinnestSector = getGalaxyDao().findThinnestSector();

			if (thinnestSector == null) {
				thinnestSector = getGalaxyBPO().createSector(0, 0);

			}

			SolarSystem solarSystem = getGalaxyBPO().createSolarSystem(thinnestSector);

			position = randomizeStartingPosition(solarSystem.getMainPlanet());

		} else {

			// do not create solar system - so we place it in the elysium
			Gate elysiumGate = getGatesDao().findByGateAdress("elysium");

			Planet planet = (Planet) elysiumGate.getPosition().getEnvironment();
			position = elysiumGate.getPosition();
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(getGalaxyTimer().getGalaxyDate());
		cal.roll(Calendar.YEAR, -25);

		Date birthDate = cal.getTime();

		Collection<NaniteGroup> nanites = new LinkedList<NaniteGroup>();
		NaniteGroup initialNanitesGroup = getNanitesDao().create(race.getInitialNanites(), position);

		nanites.add(initialNanitesGroup);

		Avatar avatar = getAvatarDao().create(user, name, race, talentsList, position, birthDate, nanites);
		avatar.setUser(user);

		initialNanitesGroup.setController(avatar);
		getNanitesDao().insert(initialNanitesGroup, true);

		if (createSystem) {

			Collection<Gate> gates = getGatesDao().findByEnvironment(avatar.getPosition().getEnvironment());

			if (gates.size() > 0) {

				avatar.setHomeGateAdress(gates.iterator().next().getGateAdress());
			}
		} else {
			avatar.setHomeGateAdress("elysium");
		}

		if (L.isDebugEnabled()) {
			L.debug("home gate adress of avatar " + avatar.getName() + " set to " + avatar.getHomeGateAdress());
		}

		getAvatarDao().update(avatar, true);

		return avatar;
	}

	private Position randomizeStartingPosition(Planet mainPlanet) {
		Position position = new Position();
		position.setEnvironment(mainPlanet);
		position.setX((int) (Math.random() * mainPlanet.getWidth()));
		position.setY((int) (Math.random() * mainPlanet.getHeight()));
		return position;
	}

	public String validateCreate(User user, String name, Race race) {
		Avatar alreadyExisting = getAvatarDao().findByUser(user);
		if (alreadyExisting != null) {
			return MessageKeys.EK_USER_ALREADY_HAS_AN_AVATAR;
		}

		name = name.trim();
		if (name.length() < 6) {
			return MessageKeys.EK_NAME_TOO_SHORT;
		} else if (name.length() > 25) {
			return MessageKeys.EK_NAME_TOO_LONG;
		}

		if (race == null) {
			return MessageKeys.EK_NO_PERSONALITY_SELECTED;
		}
		return null;
	}

	public boolean hasAvatar(User user) {

		Avatar avatar = getAvatarDao().findByUser(user);
		return avatar != null;
	}

	public Avatar findByUser(User user) {
		return getAvatarDao().findByUser(user);
	}

	public List<BluePrint> getBluePrintsUsableBy(Avatar avatar) {
		return getBluePrintDao().findByController(avatar);
	}

	/**
	 * Checks wether or not there is a noob protection activated between both
	 * parties. This is the case if the level of one of both is double as high
	 * as the other one.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public boolean isLevelBasedProtectionProtectionEngaged(Avatar first, Avatar second) {
		double ratio = (1.0 * second.getLevel()) / first.getLevel();
		return ratio <= 0.5 || ratio >= 2.0;
	}

}
