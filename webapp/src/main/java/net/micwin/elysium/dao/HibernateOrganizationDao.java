package net.micwin.elysium.dao;

import net.micwin.elysium.entities.characters.Organization;

import org.hibernate.SessionFactory;

public class HibernateOrganizationDao extends ElysiumHibernateDaoSupport<Organization> implements IOrganizationDao {

	protected HibernateOrganizationDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public Class<Organization> getEntityClass() {
		return Organization.class;
	}

}
