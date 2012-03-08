package net.micwin.elysium.dao;

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
