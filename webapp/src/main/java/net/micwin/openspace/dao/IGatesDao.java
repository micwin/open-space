package net.micwin.openspace.dao;

import java.util.Collection;

import net.micwin.openspace.entities.galaxy.Environment;
import net.micwin.openspace.entities.galaxy.Position;
import net.micwin.openspace.entities.gates.Gate;

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
	public void delete(Gate gate);

	/**
	 * Returns the list of public gate adresses, ie adresses of gates that are
	 * not home adresses and are not locked.
	 * 
	 * @return
	 */
	public Collection<String> findPublicGateAdresses();
}
