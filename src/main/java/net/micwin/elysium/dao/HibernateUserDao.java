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
import java.util.List;

import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.characters.User.Role;
import net.micwin.elysium.model.characters.User.State;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateUserDao extends ElysiumHibernateDaoSupport<User> implements IUserDao {

	public HibernateUserDao() {
	}

	@Override
	public User getUser(String login, String pass) {

		List<User> result = getSession().createQuery(" from User where login='" + login + "' and pass='" + pass + "'")
						.list();

		if (result == null || result.size() < 1) {
			return null;
		} else {
			return (User) result.get(0);
		}

	}

	@Override
	public User create(String login, String pass, State state, Role role) {
		User user = new User(login, pass, state, role);
		getHibernateTemplate().save(user);
		return user;
	}

	@Override
	public User findByLogin(String login) {
		List<User> result = getSession().createQuery(" from User where login='" + login + "'").list();
		if (result.size() < 1) {
			return null;

		} else
			return result.get(0);
	}

	@Override
	public User loadById(Serializable id) {
		return super.loadById((Long) id);
	}

	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}

}
