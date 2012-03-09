package net.micwin.elysium.bpo;

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

import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.characters.User.Role;
import net.micwin.elysium.model.characters.User.State;

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

		if (L.isDebugEnabled()) {
			L.debug("trying to register user '" + login + "' with pass1='" + pass + "' and pass2='" + pass2 + "'");
		} else {
			L.info("trying to register user '" + login + "' ...");
		}
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
		User user = getUserDao().getUser(login, pass);
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
