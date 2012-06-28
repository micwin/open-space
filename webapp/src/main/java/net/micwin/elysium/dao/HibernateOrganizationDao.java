package net.micwin.elysium.dao;

import net.micwin.elysium.entities.characters.Organization;

public class HibernateOrganizationDao extends ElysiumHibernateDaoSupport<Organization> implements IOrganizationDao {

	@Override
	public Class<Organization> getEntityClass() {
		return Organization.class;
	}

}
