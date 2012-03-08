package net.micwin.elysium.dao;

import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.characters.User.Role;
import net.micwin.elysium.model.characters.User.State;

/**
 * A dao to access user entities.
 * 
 * @author MicWin
 * 
 */
public interface IUserDao  extends IElysiumEntityDao<User>{

	/**
	 * Creates a new persistent user entity.
	 * 
	 * @param login
	 * @param pass
	 * @param state
	 * @param role
	 * @return
	 */
	User create(String login, String pass, State state, Role role);

	/**
	 * Returns the specified user if password match.
	 * 
	 * @param login
	 * @param pass
	 * @return
	 */
	User getUser(String login, String pass);

	/**
	 * Finds a user by its login.
	 * 
	 * @param login
	 * @return
	 */
	User findByLogin(String login);

}
