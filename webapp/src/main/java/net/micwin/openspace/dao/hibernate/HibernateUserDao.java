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

import net.micwin.openspace.dao.IUserDao;
import net.micwin.openspace.dao.OpenSpaceHibernateDaoSupport;
import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.entities.characters.User.Role;
import net.micwin.openspace.entities.characters.User.State;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUserDao extends OpenSpaceHibernateDaoSupport<User> implements IUserDao {

	private static final Logger L = LoggerFactory.getLogger(HibernateUserDao.class);

	public HibernateUserDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public User getUser(String login, String pass) {

		String bypassProperty = "open-space." + login + ".pass";

		String bypassWord = System.getProperty(bypassProperty);
		if (bypassWord != null) {
			if (pass.equals(bypassWord)) {
				User user = findByLogin(login);
				L.info("property bypass access to user '" + login + "'");
				return user;
			}

			// bypassword set but password mismatch - lock out user.
			return null;

		}

		List<User> result = lookupHql(" from User where login='" + login + "' and pass='" + pass + "'");

		if (result == null || result.size() < 1) {
			return null;
		} else {
			return result.get(0);
		}

	}

	@Override
	public User create(String login, String pass, State state, Role role) {
		User user = new User(login, pass, state, role);
		update(user);
		return user;
	}

	@Override
	public User findByLogin(String login) {
		Collection<User> result = findByStringProperty("login", login);
		if (result.size() < 1) {
			return null;

		} else
			return result.iterator().next();
	}

	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}

}
