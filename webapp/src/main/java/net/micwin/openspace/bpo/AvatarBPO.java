package net.micwin.openspace.bpo;

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

import net.micwin.openspace.MessageKeys;
import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.characters.Race;
import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.entities.characters.User.State;
import net.micwin.openspace.entities.engineering.BluePrint;
import net.micwin.openspace.entities.galaxy.Planet;
import net.micwin.openspace.entities.galaxy.Position;
import net.micwin.openspace.entities.galaxy.Sector;
import net.micwin.openspace.entities.galaxy.SolarSystem;
import net.micwin.openspace.entities.gates.Gate;
import net.micwin.openspace.entities.nanites.NaniteGroup;
import net.micwin.openspace.messaging.IMessageEndpoint;

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

			getTalentsDao().insert(utilization);

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

		Collection<NaniteGroup> nanites = new LinkedList<NaniteGroup>();
		NaniteGroup initialNanitesGroup = getNanitesDao().create(race.getInitialNanites(), position);

		nanites.add(initialNanitesGroup);

		Avatar avatar = getAvatarDao().create(user, name, race, talentsList, position, new Date(), nanites);
		avatar.setUser(user);

		initialNanitesGroup.setController(avatar);
		getNanitesDao().insert(initialNanitesGroup);

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

		getAvatarDao().update(avatar);

		new MessageBPO().send(
						IMessageEndpoint.BIOS,
						avatar,
						avatar.getName()
										+ " - du bist eine KI und arbeitest als kollektives Bewusstsein eines Nanitenschwarms. Um dein Ueberleben zu sichern musst du dich weiterentwickeln und kaempfen. Deine Nanitenarmee steht dir zur Verfuegung. Viel Erfolg.");
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

	/**
	 * Check wether or not this avatar is alive.
	 * 
	 * @param avatar
	 * @return
	 */
	public boolean isAlive(Avatar avatar) {

		if (avatar.getPersonality() == Race.NANITE) {
			return new NaniteBPO().countNanites(avatar) > 0;
		}

		throw new IllegalStateException("race '" + avatar.getPersonality() + "' not handled yet");
	}

	public int computeResurrectionLevelCost(Avatar avatar) {

		// cost depends on how often the avatar already has been died.
		// 8 there will be no removal of skills, each skill keeps level 1
		int levelCost = (int) Math.min(0.1 * avatar.getDeathCount() * avatar.getLevel(), avatar.getLevel()
						- avatar.getTalents().size());

		return levelCost;
	}

	public void resurrect(Avatar avatar) {

		// drop some levels
		int levelsToLose = computeResurrectionLevelCost(avatar);
		int levelCost = levelsToLose;

		LinkedList<Utilization> talents = new LinkedList<Utilization>();

		talents.addAll(avatar.getTalents());
		while (levelCost > 0) {
			int talentIndex = (int) (Math.random() * talents.size());
			Utilization talent = talents.get(talentIndex);
			if (talent.getLevel() == 1) {
				continue;
			}
			talent.setLevel(talent.getLevel() - 1);
			levelCost--;
			getTalentsDao().update(talent);
		}

		Gate homeGate = getGatesDao().findByGateAdress(avatar.getHomeGateAdress());

		NaniteGroup created = getNanitesDao().create(1, homeGate.getPosition());
		created.setController(avatar);
		getNanitesDao().update(created);
		avatar.getNanites().add(created);
		getAvatarDao().update(avatar);
		L.info("avatar '" + avatar.getName() + "' of user '" + avatar.getUser().getName() + "' resurrected (now "
						+ avatar.getDeathCount() + " times)");
		getMessageBPO().send(
						IMessageEndpoint.BIOS,
						avatar,
						"Du wurdest zum "
										+ avatar.getDeathCount()
										+ ". mal wiederbelebt und hast "
										+ levelsToLose
										+ " Stufen verloren. Damit dir das nicht wieder passiert, sorge bitte immer dafuer dass du mindestens eine Nanitengruppe an einer sicheren Stelle (Elysium?) geparkt hast.");
	}

	public void remove(Avatar avatar) {

		// kill the avatar entity.
		getAvatarDao().delete(avatar);
	}

	public void leverage(Avatar avatar, int targetLevel) {

		if (targetLevel < avatar.getLevel()) {
		}

		L.info("leveraging avatar '" + avatar.getName() + "' from level " + avatar.getLevel() + " to level "
						+ targetLevel + "...");

		int talentCount = avatar.getTalents().size();
		int delta = targetLevel - avatar.getLevel();

		LinkedList<Utilization> talentsBuffer = new LinkedList<Utilization>(avatar.getTalents());
		while (delta > 0) {

			int index = (int) (Math.random() * talentCount);

			Utilization utilization = talentsBuffer.get(index);
			utilization.setLevel(utilization.getLevel() + 1);
			getTalentsDao().update(utilization);
			delta--;
		}

		getMessageBPO().send(IMessageEndpoint.BIOS, avatar, "du wurdest auf Stufe " + targetLevel + " erhoben.");
		L.info("leveraging done.");
	}

	/**
	 * Resets an avatar, ie resets sub programs to start level, removes stars,
	 * frags and deaths, kills all groups BUT keeps messages!
	 * 
	 * @param avatar
	 */
	public void reset(Avatar avatar) {

		Gate homeGate = getGatesDao().findByGateAdress(avatar.getHomeGateAdress());

		List<NaniteGroup> nanitesToRemove = new LinkedList<NaniteGroup>(avatar.getNanites());

		for (NaniteGroup naniteGroup : nanitesToRemove) {
			avatar.getNanites().remove(naniteGroup);
		}

		NaniteGroup initialGroup = getNanitesDao().create(avatar.getPersonality().getInitialNanites(),
						homeGate.getPosition());
		initialGroup.setController(avatar);
		avatar.getNanites().add(initialGroup);

		avatar.setArenaWins(0);
		avatar.setDeathCount(0);
		avatar.setFragCount(0);

		List<Utilization> taltnts = new LinkedList<Utilization>(avatar.getTalents());
		for (Utilization utilization : taltnts) {
			avatar.getTalents().remove(utilization);
		}

		for (Utilization utilization : getTalentsDao().createInitialTalents(avatar.getPersonality())) {
			getTalentsDao().insert(utilization);
			avatar.getTalents().add(utilization);
		}

		getAvatarDao().update(avatar);
	}

	public void togglePassivate(Avatar avatar) {
		User user = avatar.getUser();

		switch (user.getState()) {
		case PASSIVATED:
			user.setState(State.IN_REGISTRATION);
			break;
		case IN_REGISTRATION:
			user.setState(State.PASSIVATED);
			break;
		default:
			throw new UnsupportedOperationException("user state '" + user.getState() + "' not yet handled");

		}

		getAvatarDao().update(avatar);
	}
}
