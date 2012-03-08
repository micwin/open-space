package net.micwin.elysium.dao;

import java.io.Serializable;

import net.micwin.elysium.model.ElysiumEntity;

public interface IElysiumEntityDao<T extends ElysiumEntity> {

	/**
	 * Loads an entity by its id.
	 * 
	 * @param id
	 * @return
	 */
	T loadById(Serializable id);

	/**
	 * Returns the entity class this dao works upon.
	 * 
	 * @return
	 */
	Class<T> getEntityClass();
}
