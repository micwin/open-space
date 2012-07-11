package net.micwin.elysium.dao;

import java.util.Collection;

import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.gates.Gate;

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
	public Gate create(Position position);

	/**
	 * Looks up a gate by its gate code.
	 * 
	 * @param code
	 * @return
	 */
	public Gate findByGateAdress(String code);

	/**
	 * Deletes specific gate from the database.
	 * 
	 * @param gate
	 */
	public void delete(Gate gate, boolean flush);
}
