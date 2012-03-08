package net.micwin.elysium.dao;

import net.micwin.elysium.model.galaxy.Galaxy;
import net.micwin.elysium.model.galaxy.Planet;
import net.micwin.elysium.model.galaxy.Sector;
import net.micwin.elysium.model.galaxy.SolarSystem;

public interface IGalaxyDao extends IElysiumEntityDao<Galaxy> {

	/**
	 * Saves a planetary system.
	 * 
	 * @param solarSystem
	 */
	void save(SolarSystem solarSystem);

	/**
	 * Saves a planet.
	 * 
	 * @param planet
	 */
	void save(Planet planet);

	/**
	 * Returns the sector with the smallest amount of planets.
	 * 
	 * @return
	 */
	Sector findThinnestSector();

	/**
	 * Saves the specified sector.
	 * 
	 * @param sector
	 */
	void save(Sector sector);

}
