package net.micwin.openspace.dao;

import java.util.Collection;

import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.Race;

public interface ITalentsDao extends IElysiumEntityDao<Utilization> {

	/**
	 * Creates an initial talents list copied from the race's template talents.
	 * 
	 * @param race
	 * @return
	 */
	public Collection<Utilization> createInitialTalents(Race race);
}
