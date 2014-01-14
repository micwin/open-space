package net.micwin.openspace.bpo;

import java.util.LinkedList;

import net.micwin.openspace.entities.galaxy.Planet;
import net.micwin.openspace.entities.galaxy.Position;
import net.micwin.openspace.entities.galaxy.Sector;
import net.micwin.openspace.entities.galaxy.SolarSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
/**
 * A bpo that handles planets, sywstems etc.
 * 
 * @author MicWin
 * 
 */
public class GalaxyBPO extends BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(GalaxyBPO.class);
	private static final int MAX_PLANETS_COUNT = 11;
	private static final int MIN_PLANETS_COUNT = 3;

	/**
	 * Creates a new solar system.
	 * 
	 * @param forceHabitable
	 *            If true, then the main planet is habitable.-
	 * @return
	 **/

	public SolarSystem createSolarSystem(Sector sector) {

		if (L.isDebugEnabled()) {
			L.debug("creating solar system in sector " + sector);
		}

		SolarSystem solarSystem = new SolarSystem();
		solarSystem.setHeight(100);
		solarSystem.setWidth(100);
		Position position = new Position();
		position.setEnvironment(sector);
		position.setX((int) (Math.random() * sector.getWidth()));
		position.setY((int) (Math.random() * sector.getHeight()));

		solarSystem.setPosition(position);

		solarSystem.setName(sector.getName() + "/" + position.getX() + "," + position.getY());

		getGalaxyDao().save(solarSystem);

		fillWithPlanets(solarSystem, (int) (Math.random() * (MAX_PLANETS_COUNT - MIN_PLANETS_COUNT))
						+ MIN_PLANETS_COUNT);

		Planet main = solarSystem.getMainPlanet();
		getGatesDao().create(
						new Position(main, (int) (Math.random() * main.getWidth()), (int) (Math.random() * main
										.getHeight())));

		getGalaxyDao().save(solarSystem);

		sector.getSolarSystems().add(solarSystem);

		getGalaxyDao().save(sector);

		return solarSystem;
	}

	private void fillWithPlanets(SolarSystem solarSystem, int planetsCount) {

		int mainPlanetIndex = (int) (Math.random() * planetsCount);
		if (L.isDebugEnabled()) {
			L.debug("filling solar system with " + planetsCount + " planets ...");
		}

		for (int i = 0; i < planetsCount; i++) {
			Planet planet = createPlanet(solarSystem);

			if (i == mainPlanetIndex) {
				solarSystem.setMainPlanet(planet);
			}

		}

		getGalaxyDao().save(solarSystem);
	}

	/**
	 * Creates a planet and puts it into the passed solar system.
	 * 
	 * @param solarSystem
	 * @return
	 */
	protected Planet createPlanet(SolarSystem solarSystem) {
		Planet planet = new Planet();
		Position position = new Position();
		position.setEnvironment(solarSystem);
		position.setX((int) (Math.random() * solarSystem.getWidth()));
		position.setY((int) (Math.random() * solarSystem.getHeight()));
		planet.setPosition(position);
		getGalaxyDao().save(planet);
		solarSystem.getPlanets().add(planet);
		planet.setName(solarSystem.getName() + " " + (solarSystem.getPlanets().size() + 1));

		if (L.isDebugEnabled()) {
			L.debug("created planet " + planet);
		}

		return planet;
	}

	public Sector createSector(int x, int y) {

		if (L.isDebugEnabled()) {
			L.debug("creating sector " + x + "/" + y);
		}
		Sector sector = new Sector();
		Position position = new Position();
		position.setX(x);
		position.setY(y);
		sector.setPosition(position);
		sector.setHeight(100);
		sector.setWidth(100);

		sector.setSolarSystems(new LinkedList<SolarSystem>());

		getGalaxyDao().save(sector);
		return sector;
	}

}
