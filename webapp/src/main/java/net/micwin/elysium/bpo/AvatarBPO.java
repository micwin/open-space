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

import net.micwin.elysium.Constants;
import net.micwin.elysium.MessageKeys;
import net.micwin.elysium.model.NaniteGroup;
import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.Race;
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
	 * @param race
	 * @return
	 */
	public String create(User user, String name, Race race) {
		String error = validate(user, name, race);
		if (error != null) {
			return error;
		}

		Collection<Utilization> talentsList = getTalentsDao().createInitialTalents(race);
		
		getTalentsDao().saveAll (talentsList) ; 

		Sector thinnestSector = getGalaxyDao().findThinnestSector();

		if (thinnestSector == null) {
			thinnestSector = getGalaxyBPO().createSector();

		}
		SolarSystem solarSystem = getGalaxyBPO().createSolarSystem(thinnestSector);

		Position position = randomizeStartingPosition(solarSystem.getMainPlanet());
		
		getGalaxyBPO().createGates(solarSystem) ; 
		

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(getGalaxyTimer().getGalaxyDate());
		cal.roll(Calendar.YEAR, -25);

		Date birthDate = cal.getTime();

		Collection<NaniteGroup> nanites = new LinkedList<NaniteGroup>();
		nanites.add(getNanitesDao().create(race.getInitialNanites(), position));
		Avatar avatar = getAvatarDao().create(user, name, race, talentsList, Constants.TALENT_POINTS_UPON_CREATION,
						position, birthDate, nanites);
		return null;
	}

	private Position randomizeStartingPosition(Planet mainPlanet) {
		Position position = new Position();
		position.setEnvironment(mainPlanet);
		position.setX((int) (Math.random() * mainPlanet.getWidth()));
		position.setY((int) (Math.random() * mainPlanet.getHeight()));
		return position;
	}


	public String validate(User user, String name, Race race) {
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

		if (race == null) {
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