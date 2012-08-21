package net.micwin.elysium.dao;

import java.util.Collection;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.colossus.Colossus;
import net.micwin.elysium.entities.colossus.Colossus.ColossusState;

/**
 * A dao for colossus instances
 * 
 * @author MicWin
 * 
 */
public interface IColossusDao extends IElysiumEntityDao<Colossus> {

	/**
	 * Creates a new Colossus Entry built from this nanite group.
	 * 
	 * @return
	 */
	Colossus convert(NaniteGroup naniteGroup);

	/**
	 * Returns all colossuses in specified state.
	 * 
	 * @param state
	 * @return
	 */
	Collection<Colossus> findByState(ColossusState state);
}
