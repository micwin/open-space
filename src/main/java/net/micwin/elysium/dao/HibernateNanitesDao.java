package net.micwin.elysium.dao;

import java.io.Serializable;

import net.micwin.elysium.model.NanoBotHive;

public class HibernateNanitesDao extends ElysiumHibernateDaoSupport<NanoBotHive> implements INanitesDao {

	public Class<NanoBotHive> getEntityClass() {
		return NanoBotHive.class;
	}

	@Override
	public NanoBotHive loadById(Serializable id) {
		return super.loadById((Long) id);
	}
}
