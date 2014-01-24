package net.micwin.openspace.dao.hibernate;

import net.micwin.openspace.dao.IOrganizationDao;
import net.micwin.openspace.dao.OpenSpaceHibernateDaoSupport;
import net.micwin.openspace.entities.characters.Organization;

import org.hibernate.SessionFactory;

public class HibernateOrganizationDao extends OpenSpaceHibernateDaoSupport<Organization> implements IOrganizationDao {

	protected HibernateOrganizationDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public Class<Organization> getEntityClass() {
		return Organization.class;
	}

}
