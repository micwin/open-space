package net.micwin.openspace.bpo;

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

import java.io.Serializable;
import java.util.Date;

import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.entities.characters.User.Role;
import net.micwin.openspace.entities.characters.User.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the bpo that handles business logic about registration. Stateless, fast to
 * create and to throw away.
 * 
 * @author MicWin
 * 
 */
public class UserBPO extends BaseBPO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5573755004691006468L;
	public static final String MESSAGE_KEY_EMPTY_PASSWORD_NOT_ALLOWED = "emptyPasswordNotAllowed";
	public static String MESSAGE_KEY_PASSWORDS_DONT_MATCH = "passwordMismatch";

	private static final Logger L = LoggerFactory.getLogger(UserBPO.class);

	public static final String MESSAGE_KEY_EMPTY_LOGIN = "loginEmpty";
	public static final String MESSAGE_KEY_LOGIN_TOO_SHORT = "loginTooShort";
	public static final String MESSAGE_KEY_LOGIN_TOO_LONG = "loginTooLong";

	public UserBPO() {
		L.debug("instantiated.");
	}

	public String register(String login, String pass, String pass2) {

		L.info("trying to register user '" + login + "' ...");

		String validationResult = validate(login, pass, pass2);
		if (validationResult != null) {
			return validationResult;
		}

		User user = getUserDao().create(login, pass, State.IN_REGISTRATION, Role.ACTOR);

		L.info("user '" + user.getLogin() + "' registered in state '" + user.getState().name() + "'");

		return null;
	}

	/**
	 * Validates the registration data.
	 * 
	 * @param login
	 *            a login between 6 and 12 characters.
	 * @param pass
	 *            a password
	 * @param pass2
	 *            the same password, typed again.
	 * @return <code>null</code> if everything is fine. A message code of the
	 *         error otherwise. See constants MESSAKE_KEY_xxx in this class.
	 */
	public String validate(String login, String pass, String pass2) {

		login = login == null ? "" : login.trim();

		if (login.length() < 1) {
			return MESSAGE_KEY_EMPTY_LOGIN;

		}
		if (login.length() < 6) {
			return MESSAGE_KEY_LOGIN_TOO_SHORT;

		} else if (login.length() > 12) {
			return MESSAGE_KEY_LOGIN_TOO_LONG;
		}

		if (pass == null || pass.length() < 1) {
			return MESSAGE_KEY_EMPTY_PASSWORD_NOT_ALLOWED;
		}
		if (!pass.equals(pass2)) {
			return MESSAGE_KEY_PASSWORDS_DONT_MATCH;
		}

		// everything fine.

		return null;
	}

	public User login(String login, String pass) {
		if (L.isDebugEnabled()) {
			L.debug("logging in user '" + login + "'");
		}

		User user = getUserDao().getUser(login, pass);
		if (user == null) {
			L.error("user '" + login + "' not logged in");
			return null;
		} else if (user.getState() != State.ACTIVE && user.getState() != State.IN_REGISTRATION
						&& user.getState() != State.NPC) {
			L.error("user '" + login + "' in state " + user.getState());
			return null;
		}

		L.info("logged in user '" + user.getLogin() + "'");

		user.setLastLoginDate(new Date());
		getUserDao().update(user);

		return user;
	}

	/**
	 * Logs a user out.
	 * 
	 * @param user
	 */
	public void logout(User user) {
		L.debug("logged out user '" + user + "'");
	}
}
