package net.micwin.elysium.dao;

import java.io.Serializable;
import java.util.List;

import net.micwin.elysium.model.galaxy.Galaxy;
import net.micwin.elysium.model.galaxy.Planet;
import net.micwin.elysium.model.galaxy.Sector;
import net.micwin.elysium.model.galaxy.SolarSystem;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateGalaxyDao extends HibernateDaoSupport implements IGalaxyDao {

	@Override
	public void save(SolarSystem solarSystem) {
		getHibernateTemplate().saveOrUpdate(solarSystem);

	}

	@Override
	public void save(Planet planet) {
		getHibernateTemplate().saveOrUpdate(planet);

	}

	@Override
	public Sector findThinnestSector() {

		List results = getSession().createQuery(" from Sector as sector order by sector.systemCount asc limit 1")
						.list();
		return (Sector) (results.size() < 1 ? null : results.get(0));
	}

	@Override
	public void save(Sector sector) {
		getHibernateTemplate().save(sector);
	}

	@Override
	public Galaxy loadById(Serializable id) {
		throw new IllegalStateException("Galax yis a dummy entity. you cant load it.");
	}

	@Override
	public Class<Galaxy> getEntityClass() {
		return Galaxy.class;
	}

}
