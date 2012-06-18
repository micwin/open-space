package net.micwin.elysium.dao;

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
import net.micwin.elysium.entities.ElysiumEntity;

public interface IElysiumEntityDao<T extends ElysiumEntity> {

	/**
	 * Loads an entity by its id.
	 * 
	 * @param id
	 * @return
	 */
	T loadById(Long id);

	/**
	 * Returns the entity class this dao works upon.
	 * 
	 * @return
	 */
	Class<T> getEntityClass();

	/**
	 * Inserts a new Entity.
	 * 
	 * @param entity
	 * @param flush
	 */
	void insert(T entity, boolean flush);

	/**
	 * Saves existing entity.
	 * 
	 * @param entity
	 * @param flush
	 */
	void update(T entity, boolean flush);

	/**
	 * Saves all those entities and flushes at the end, if desired.
	 * 
	 * @param entities
	 * @param flush
	 */
	public void update(Iterable<T> elements, boolean flush);

	/**
	 * Flushes changes in memory to the db.
	 */
	void flush();

}
