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
import java.util.List;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.galaxy.Environment;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public abstract class ElysiumHibernateDaoSupport<T extends ElysiumEntity> {

	private static final Logger L = LoggerFactory.getLogger(ElysiumHibernateDaoSupport.class);
	private final SessionFactory sf;

	protected ElysiumHibernateDaoSupport(SessionFactory sf) {
		this.sf = sf;
	}

	/**
	 * Updates already existing entities.
	 * 
	 * @param elements
	 * @param flush
	 */
	public void update(final Iterable<T> elements) {
		for (T element : elements) {
			update(element);
		}

	}

	protected List<T> lookupHql(final String hqlString) {

		return new TxBracelet<List<T>>(sf) {

			@Override
			public List<T> doWork(Session session, Transaction tx) {
				return session.createQuery(hqlString).list();
			}
		}.execute();

	}

	protected List lookupHqlBare(final String hqlString) {

		return new TxBracelet<List>(sf) {

			@Override
			public List doWork(Session session, Transaction tx) {
				return session.createQuery(hqlString).list();
			}
		}.execute();

	}

	/**
	 * Updates an already existing entities.
	 * 
	 * @param elements
	 * @param flush
	 */
	public final void update(final T entity) {

		new TxBracelet<T>(sf) {

			@Override
			public T doWork(Session session, Transaction tx) {
				if (L.isDebugEnabled()) {
					L.debug("saved entity '" + getEntityClass().getSimpleName() + "' (" + entity.getId() + ")");
				}
				session.saveOrUpdate(entity);
				return null;
			}
		}.execute();
		return;
	}

	/**
	 * Updates an already existing entities.
	 * 
	 * @param elements
	 * @param flush
	 */
	public final void update(final Object o) {

		new TxBracelet<T>(sf) {

			@Override
			public T doWork(Session session, Transaction tx) {
				session.saveOrUpdate(o);
				return null;
			}
		}.execute();
		return;
	}

	/**
	 * inserts a new entity into database.
	 * 
	 * @param entity
	 *            the entity to be inserted.
	 * @param flush
	 */
	public final void insert(final T entity) {

		new TxBracelet<T>(sf) {

			@Override
			public T doWork(Session session, Transaction tx) {
				session.save(entity);

				return null;
			}
		}.execute();

		if (L.isDebugEnabled()) {
			L.debug("inserted entity '" + getEntityClass().getSimpleName() + "' (" + entity.getId() + ")");
		}

		return;
	}

	public void delete(final T entity) {
		new TxBracelet<T>(sf) {

			@Override
			public T doWork(Session session, Transaction tx) {
				session.delete(entity);
				return null;

			}
		}.execute();

	}

	public final T loadById(final Long id) {

		try {
			return new TxBracelet<T>(sf) {

				@Override
				public T doWork(Session session, Transaction tx) {
					return (T) session.load(getEntityClass(), id);
				}
			}.execute();

		} catch (org.hibernate.ObjectNotFoundException e) {
			return null;
		}
	}

	public abstract Class<T> getEntityClass();

	public Collection<T> findByController(Avatar controller) {
		return lookupHql("from " + getEntityClass().getSimpleName() + " where controller.id=" + controller.getId());
	}

	public T refresh(final T entity) {

		new TxBracelet<T>(sf) {

			@Override
			public T doWork(Session session, Transaction tx) {
				session.refresh(entity);
				return null;
			}
		}.execute();
		return entity;
	}

	public Collection<T> findByEnvironment(Environment environment) {
		List<T> result = lookupHql(" from "+getEntityClass().getSimpleName()+" where position.environment.id=" + environment.getId());
		return result;
	}
	
	public Collection<T> findByStringProperty(String property, String value) {
		return lookupHql("from " + getEntityClass().getSimpleName() + " where " + property + "='" + value + "'");
	};

	public int countEntries() {

		return new TxBracelet<Number>(sf) {

			@Override
			public Number doWork(Session session, Transaction tx) {
				return ((Number) session.createQuery("Select Count(*) From " + getEntityClass().getSimpleName())
								.uniqueResult());
			}
		}.execute().intValue();

	}

	protected SessionFactory getSessionFactory() {
		return sf;
	}

}
