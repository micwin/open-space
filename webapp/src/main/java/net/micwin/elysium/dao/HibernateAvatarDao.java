package net.micwin.elysium.dao;

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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.Organization;
import net.micwin.elysium.entities.characters.Race;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.galaxy.Position;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateAvatarDao extends ElysiumHibernateDaoSupport<Avatar> implements IAvatarDao {

	private static final Logger L = LoggerFactory.getLogger(HibernateAvatarDao.class);

	public HibernateAvatarDao() {
	}

	@Override
	public Avatar findByUser(User user) {
		if (L.isDebugEnabled()) {
			L.debug("looking for avatar of user " + user);
		}

		if (user == null) {
			return null;
		}

		List<Avatar> result = lookupHql(" from Avatar where user.id=" + user.getId());

		if (L.isDebugEnabled()) {
			L.debug("result: " + result);
		}
		if (result == null || result.size() != 1) {
			return null;
		} else {
			return result.get(0);
		}
	}

	@Override
	public Avatar create(User user, String name, Race race, Collection<Utilization> talents, Position position,
					Date birthDate, Collection<NaniteGroup> nanites) {
		Avatar avatar = new Avatar();
		avatar.setUser(user);
		avatar.setName(name);
		avatar.setPersonality(race);
		avatar.setCreationDate(birthDate);
		avatar.setPosition(position);
		avatar.setStoryLineItem(race.getFirstStoryItem());

		insert(avatar, true);

		Organization organization = new Organization();
		organization.setController(avatar);
		avatar.setOrganization(organization);
		getHibernateTemplate().save(organization);

		for (Iterator talentsIter = talents.iterator(); talentsIter.hasNext();) {
			Utilization utilization = (Utilization) talentsIter.next();
			utilization.setController(avatar);
		}

		getHibernateTemplate().saveOrUpdateAll(talents);
		avatar.setTalents(new LinkedList<Utilization>(talents));

		avatar.setNanites(nanites);

		update(avatar, true);

		return avatar;
	}

	@Override
	public boolean nameExists(String name) {
		Avatar avatar = new Avatar();
		avatar.setName(name);
		return getHibernateTemplate().findByExample(avatar).size() > 0;
	}

	@Override
	public Class<Avatar> getEntityClass() {
		return Avatar.class;
	}

}
