package net.micwin.elysium.bpo;

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
