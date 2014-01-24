package net.micwin.openspace.dao.hibernate;

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

import net.micwin.openspace.dao.IGalaxyDao;
import net.micwin.openspace.dao.OpenSpaceHibernateDaoSupport;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.galaxy.Galaxy;
import net.micwin.openspace.entities.galaxy.Planet;
import net.micwin.openspace.entities.galaxy.Sector;
import net.micwin.openspace.entities.galaxy.SolarSystem;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateGalaxyDao extends OpenSpaceHibernateDaoSupport<Galaxy> implements IGalaxyDao {

	protected HibernateGalaxyDao(SessionFactory sf) {
		super(sf);
	}

	private static final Logger L = LoggerFactory.getLogger(HibernateGalaxyDao.class);

	@Override
	public void save(SolarSystem solarSystem) {
		update(solarSystem);

	}

	@Override
	public void save(Planet planet) {
		update(planet);

	}

	@Override
	public Sector findThinnestSector() {

		if (L.isDebugEnabled()) {
			L.debug("looking for thinnest sector");
		}
		List results = lookupHql("from Sector as sector order by sector.solarSystems.size asc limit 1");

		if (L.isDebugEnabled() && results.size() < 1) {
			L.debug("no thinnest sector present.");

		}
		return (Sector) (results.size() < 1 ? null : results.get(0));
	}

	@Override
	public void save(Sector sector) {
		update(sector);
	}

	@Override
	public Class<Galaxy> getEntityClass() {
		return Galaxy.class;
	}

	@Override
	public void update(Iterable<Galaxy> elements) {
		throw new IllegalStateException("Galaxy is a dummy entity. you cant load it.");
	}

	@Override
	public Collection<Galaxy> findByController(Avatar controller) {
		throw new UnsupportedOperationException("no avatar controls galaxy");
	}

	@Override
	public Collection<Galaxy> findByStringProperty(String property, String value) {
		throw new IllegalStateException("Galaxy is a dummy entity. you cant load it.");
	}

	@Override
	public int countEntries() {
		throw new IllegalStateException("Galaxy is a dummy entity. you cant count them");
	}
}
