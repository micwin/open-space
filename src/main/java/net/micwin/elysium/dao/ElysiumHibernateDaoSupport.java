package net.micwin.elysium.dao;

/* This file is part of open-space.

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

 open-space ist Freie Software: Sie k�nnen es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
 ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es n�tzlich sein wird, aber
 OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
 Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License f�r weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

import java.util.List;

import net.micwin.elysium.model.ElysiumEntity;
import net.micwin.elysium.model.replication.BluePrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class ElysiumHibernateDaoSupport<T extends ElysiumEntity> extends HibernateDaoSupport {

	private static final Logger L = LoggerFactory.getLogger(ElysiumHibernateDaoSupport.class);

	protected void saveAll(Iterable<T> elements) {
		for (T element : elements) {
			save(element);
		}

	}

	protected List<T> lookupHql(String hqlString) {
		List list = getSession().createQuery(hqlString).list();
		if (L.isDebugEnabled()) {
			L.debug("query " + hqlString + " returns " + list.getClass() + " (" + list + ")");
		}
		return list;
	}

	public final void save(T entity) {
		if (L.isDebugEnabled()) {
			L.debug("saved entity '" + getEntityClass().getSimpleName() + "' (" + entity.getId() + ")");
		}

		getHibernateTemplate().save(entity);
		return;
	}

	public final T loadById(Long id) {
		return (T) getHibernateTemplate().load(getEntityClass(), id);
	}

	protected abstract Class<T> getEntityClass();

}
