package net.micwin.elysium.dao;

import org.hibernate.SessionFactory;

import net.micwin.elysium.entities.colossus.Colossus;

public class HibernateColossusDao extends ElysiumHibernateDaoSupport<Colossus> implements IColossusDao {

	protected HibernateColossusDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public Class<Colossus> getEntityClass() {
		return Colossus.class;
	}

}
