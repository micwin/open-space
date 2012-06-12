package net.micwin.elysium.dao;

import java.util.Collection;

import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Race;

public interface ITalentsDao extends IElysiumEntityDao<Utilization> {

	public void saveAll(Iterable<Utilization> entities);

	/**
	 * Creates an initial talents list copied from the race's template talents.
	 * 
	 * @param race
	 * @return
	 */
	public Collection<Utilization> createInitialTalents(Race race);
}
