package net.micwin.openspace.dao;

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
import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.entities.characters.User.Role;
import net.micwin.openspace.entities.characters.User.State;

/**
 * A dao to access user entities.
 * 
 * @author MicWin
 * 
 */
public interface IUserDao extends IElysiumEntityDao<User> {

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
