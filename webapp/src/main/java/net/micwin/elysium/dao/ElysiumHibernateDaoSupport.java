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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class ElysiumHibernateDaoSupport<T extends ElysiumEntity> extends HibernateDaoSupport {

	private static final Logger L = LoggerFactory.getLogger(ElysiumHibernateDaoSupport.class);

	/**
	 * Updates already existing entities.
	 * 
	 * @param elements
	 * @param flush
	 */
	public void update(final Iterable<T> elements, boolean flush) {

		for (T element : elements) {
			update(element, false);
		}

		if (flush) {
			getHibernateTemplate().flush();
		}

	}

	protected List<T> lookupHql(final String hqlString) {

		List<T> result = getHibernateTemplate().find(hqlString);

		if (L.isDebugEnabled()) {
			L.debug("query " + hqlString + " returns " + result.getClass() + " (" + result + ")");
		}

		return new LinkedList<T>(result);
	}

	/**
	 * Updates an already existing entities.
	 * 
	 * @param elements
	 * @param flush
	 */
	public final void update(T entity, boolean flush) {
		if (L.isDebugEnabled()) {
			L.debug("saved entity '" + getEntityClass().getSimpleName() + "' (" + entity.getId() + ")");
		}
		getHibernateTemplate().saveOrUpdate(entity);

		if (flush) {
			getHibernateTemplate().flush();
		}
		return;
	}

	/**
	 * inserts a new entity into database.
	 * 
	 * @param entity
	 *            the entity to be inserted.
	 * @param flush
	 */
	public final void insert(T entity, boolean flush) {

		getHibernateTemplate().saveOrUpdate(entity);

		if (L.isDebugEnabled()) {
			L.debug("inserted entity '" + getEntityClass().getSimpleName() + "' (" + entity.getId() + ")");
		}

		if (flush) {
			getHibernateTemplate().flush();
		}
		return;
	}

	public void delete(T entity, boolean flush) {
		getHibernateTemplate().delete(entity);
		if (flush)
			getHibernateTemplate().flush();

		getHibernateTemplate().evict(entity);
	}

	public final T loadById(Long id) {
		return (T) getHibernateTemplate().load(getEntityClass(), id);
	}

	public abstract Class<T> getEntityClass();

	/**
	 * Flushes changes in memory to the db.
	 */
	public void flush() {
		getHibernateTemplate().flush();
	}

	public Collection<T> findByController(Avatar controller) {
		return lookupHql("from " + getEntityClass().getSimpleName() + " where controller.id=" + controller.getId());
	}

	public T refresh(T entity) {

		getHibernateTemplate().refresh(entity, LockMode.OPTIMISTIC_FORCE_INCREMENT);
		return entity;
	}

	public Collection<T> findByStringProperty(String property, String value) {
		return lookupHql("from " + getEntityClass().getSimpleName() + " where " + property + "='" + value + "'");
	};

	public Collection<T> loadAll(Collection<T> target) {
		if (target == null)
			target = new LinkedList<T>();

		target.addAll(getHibernateTemplate().loadAll(getEntityClass()));
		return target;
	}

}
