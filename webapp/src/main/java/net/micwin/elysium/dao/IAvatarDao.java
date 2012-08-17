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
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.Race;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.entities.galaxy.Position;

/**
 * A dao to locate and save avatars.
 * 
 * @author MicWin
 * 
 */
public interface IAvatarDao extends IElysiumEntityDao<Avatar> {

	/**
	 * Find avatar by its user.
	 * 
	 * @param user
	 * @return
	 */
	Avatar findByUser(User user);

	/**
	 * Creates a new avatar.
	 * 
	 * @param user
	 * @param name
	 * @param race
	 * @param talents
	 * @param position
	 *            The actual position the avatar should be in.
	 * @param birthDate
	 * @param storyLineItem
	 * @return
	 */
	Avatar create(User user, String name, Race race, Collection<Utilization> talents, Position position,
					Date birthDate, Collection<NaniteGroup> nanites);

	/**
	 * Checks wether or not the specified Avatar Name already exists.
	 * 
	 * @param name
	 * @return
	 */
	boolean nameExists(String name);

	/**
	 * Refetches (ie reloads) the specified avatar and insures on that way that
	 * all references are present (preload problem).
	 * 
	 * @param controller
	 * @return
	 */
	Avatar refresh(Avatar controller);

	/**
	 * Removes an avatar from the database.
	 * 
	 * @param avatar
	 */
	void delete(Avatar avatar, boolean flush);

	/**
	 * Returns all avatars fitting to specified role.
	 * 
	 * @param role
	 * @return
	 */
	List<Avatar> findByRole(Role role);

	
	Collection<Avatar> loadAll();

}
