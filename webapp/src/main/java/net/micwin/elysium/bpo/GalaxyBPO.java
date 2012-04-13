package net.micwin.elysium.bpo;

import net.micwin.elysium.model.galaxy.Planet;
import net.micwin.elysium.model.galaxy.Position;
import net.micwin.elysium.model.galaxy.Sector;
import net.micwin.elysium.model.galaxy.SolarSystem;
import net.micwin.elysium.model.gates.Gate;

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

	/**
	 * Creates a new solar system.
	 * 
	 * @param forceHabitable
	 *            If true, then the main planet is habitable.-
	 * @return
	 */
	public SolarSystem createSolarSystem(Sector sector) {

		int planetsCount = (int) (Math.random() * 10 + 1);

		SolarSystem solarSystem = new SolarSystem();
		solarSystem.setHeight(100);
		solarSystem.setWidth(100);
		Position position = new Position();
		position.setEnvironment(sector);
		position.setX((int) (Math.random() * sector.getWidth()));
		position.setY((int) (Math.random() * sector.getHeight()));

		solarSystem.setPosition(position);

		getGalaxyDao().save(solarSystem);

		fillWithPlanets(solarSystem, planetsCount);

		getGalaxyDao().save(solarSystem);

		sector.addSolarSystem(solarSystem);

		getGalaxyDao().save(sector);

		return solarSystem;
	}

	private void fillWithPlanets(SolarSystem solarSystem, int planetsCount) {

		int mainPlanetIndex = (int) (Math.random() * planetsCount);
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
		if (L.isDebugEnabled()) {
			L.debug("created planet " + planet);
		}

		return planet;
	}

	public Sector createSector() {
		Sector sector = new Sector();
		Position position = new Position();
		position.setX(0);
		position.setY(0);
		sector.setPosition(position);
		sector.setHeight(100);
		sector.setWidth(100);

		getGalaxyDao().save(sector);
		return sector;
	}

	public void createGates(SolarSystem solarSystem) {
		for (Planet planet : solarSystem.getPlanets()) {

			getGatesDao().create(new Position(planet, -1, -1));
		}
	}

}
