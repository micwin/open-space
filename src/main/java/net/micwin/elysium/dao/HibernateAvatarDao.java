package net.micwin.elysium.dao;

/* This file is part of open-space.

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

 open-space ist Freie Software: Sie k�nnen es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
 ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es n�tzlich sein wird, aber
 OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
 Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License f�r weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.Avatar.Personality;
import net.micwin.elysium.model.characters.Organization;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.galaxy.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateAvatarDao extends ElysiumHibernateDaoSupport<Avatar> implements IAvatarDao {

	private static final Logger L = LoggerFactory.getLogger(HibernateAvatarDao.class);

	public HibernateAvatarDao() {
	}

	@Override
	public Avatar findByController(User user) {
		if (L.isDebugEnabled()) {
			L.debug("looking for avatar of user " + user);
		}

		if (user == null) {
			return null;
		}

		List<Avatar> result = lookupHql(" from Avatar where controller.id=" + user.getId());

		if (L.isDebugEnabled()) {
			L.debug("result: " + result);
		}
		if (result == null || result.size() != 1) {
			return null;
		} else {
			return (Avatar) result.get(0);
		}
	}

	@Override
	public Avatar create(User user, String name, Personality personality, Collection<Utilization> talents,
					int talentPoints, Position position, Date birthDate) {
		Avatar avatar = new Avatar();
		avatar.setController(user);
		avatar.setName(name);
		avatar.setPersonality(personality);
		avatar.setCreationDate(birthDate);
		avatar.setLevel(0);
		avatar.setXp(0l);
		avatar.setTalentPoints(talentPoints);
		avatar.setPosition(position);
		avatar.setStoryLineItem(personality.getFirstStoryItem());

		Organization organization = new Organization();
		organization.setController(avatar);
		avatar.setOrganization(organization);
		getHibernateTemplate().save(organization);

		getHibernateTemplate().saveOrUpdateAll(talents);
		avatar.setTalents(talents);

		save(avatar);
		getHibernateTemplate().flush();

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

	@Override
	public Avatar loadById(Serializable id) {
		return super.loadById((Long) id);
	}
}
