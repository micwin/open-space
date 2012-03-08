package net.micwin.elysium.dao;

import java.util.Collection;
import java.util.Date;

import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.Avatar.Personality;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.galaxy.Position;

/**
 * A dao to locate and save avatars.
 * 
 * @author MicWin
 * 
 */
public interface IAvatarDao extends IElysiumEntityDao<Avatar> {

	/**
	 * Find avatar by its controller.
	 * 
	 * @param controller
	 * @return
	 */
	Avatar findByController(User controller);

	/**
	 * Creates a new avatar.
	 * 
	 * @param user
	 * @param name
	 * @param personality
	 * @param talents
	 * @param talentPoints
	 * @param position
	 *            The actual position the avatar should be in.
	 * @param birthDate
	 * @param storyLineItem
	 * @return
	 */
	Avatar create(User user, String name, Personality personality, Collection<Utilization> talents, int talentPoints,
					Position position, Date birthDate);

	/**
	 * Checks wether or not the specified Avatar Name already exists.
	 * 
	 * @param name
	 * @return
	 */
	boolean nameExists(String name);
}
