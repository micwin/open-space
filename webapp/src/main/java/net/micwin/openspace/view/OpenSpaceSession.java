package net.micwin.openspace.view;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.micwin.openspace.entities.OpenSpaceEntity;
import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.entities.characters.User.Role;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSpaceSession extends WebSession {

	private static final Logger L = LoggerFactory.getLogger(OpenSpaceSession.class);

	private static final long serialVersionUID = 3923640078723752554L;

	private Date creationDate = new Date();

	private User user;

	// a named session context to put in elysium entities wrapped by models.
	private Map<String, OpenSpaceWicketModel> ctx = new HashMap<String, OpenSpaceWicketModel>();

	private boolean storyShown = false;

	public OpenSpaceSession(Request request) {
		super(request);
	}

	/**
	 * Returns the date this session was created, ie the first contact of the
	 * client with this session.
	 * 
	 * @return
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * returns the logged in user.
	 * 
	 * @return
	 */
	public User getUser() {
		return user;
	}

	/**
	 * sets a new user for this session. Only valid if not yet called upon this
	 * session.
	 * 
	 * @param newUser
	 */
	public void setUser(User newUser) {
		if (L.isDebugEnabled()) {
			L.debug("switching session from user '" + user + "' to user '" + newUser + "'");
		}
		user = newUser;

		// We do it that way to ensure that a admin can use admin features when
		// logging in as normal player. THere is no way of getting rid of this
		// admin flag except letting the httpsession expire.
		if (user != null && user.getRole() == Role.ADMIN) {
			setAttribute("isAdmin", "true");
		}

	}

	public boolean isStoryShown() {
		return storyShown;
	}

	public void setStoryShown(boolean newState) {
		storyShown = newState;
	}

	/**
	 * Sets a named entity to be retrieved in a later request.
	 * 
	 * @param name
	 *            the name with which it can get retrieved later.
	 * @param entity
	 *            the entity to wrap in a model and put to session context.
	 */
	public <T extends OpenSpaceEntity> void setNamedEntity(String name, T entity) {
		if (entity == null)

		{
			ctx.remove(name);
		} else {
			OpenSpaceWicketModel<T> entityModel = new OpenSpaceWicketModel<T>(entity);
			ctx.put(name, entityModel);
		}
	}

	/**
	 * Retrieves a previously set named entity.
	 * 
	 * @param name
	 * @return
	 */
	public <T extends OpenSpaceEntity> T getNamedEntity(String name) {
		OpenSpaceWicketModel<T> entityModel = ctx.get(name);
		return entityModel != null ? entityModel.getObject() : null;
	}

	@Override
	public void detach() {
		super.detach();

		// detach models, but keep them for next request.

		for (OpenSpaceWicketModel model : ctx.values()) {
			if (model.isAttached())
				model.detach();
		}
	}

	@Override
	public void invalidate() {
		user = null;
		ctx.clear();
		super.invalidate();
	}

	public boolean isAdmin() {
		return "true".equals(getAttribute("isAdmin"));
	}
}
