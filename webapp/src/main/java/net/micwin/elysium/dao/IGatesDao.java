package net.micwin.elysium.dao;

import java.util.Collection;

import net.micwin.elysium.model.galaxy.Environment;
import net.micwin.elysium.model.galaxy.Position;
import net.micwin.elysium.model.gates.Gate;

public interface IGatesDao extends IElysiumEntityDao<Gate> {

	/**
	 * Gets all gates of that specific environment. Use with care.
	 * 
	 * @param environment
	 * @return
	 */
	public Collection<Gate> findByEnvironment(Environment environment);

	/**
	 * Creates and saves a new Gate at the given position.
	 * 
	 * @param position
	 */
	public void create(Position position);
}
