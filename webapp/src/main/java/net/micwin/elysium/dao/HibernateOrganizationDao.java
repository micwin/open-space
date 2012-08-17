package net.micwin.elysium.dao;

import org.hibernate.SessionFactory;

import net.micwin.elysium.entities.characters.Organization;

public class HibernateOrganizationDao extends ElysiumHibernateDaoSupport<Organization> implements IOrganizationDao {

	protected HibernateOrganizationDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public Class<Organization> getEntityClass() {
		return Organization.class;
	}

}
