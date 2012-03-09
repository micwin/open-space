package net.micwin.elysium.view;

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

import java.util.Date;

import net.micwin.elysium.model.characters.User;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElysiumSession extends WebSession {

	private static final Logger L = LoggerFactory.getLogger(ElysiumSession.class);

	private static final long serialVersionUID = 3923640078723752554L;

	private Date creationDate = new Date();

	private User user;

	private boolean storyShown = false;

	public ElysiumSession(Request request) {
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

	}

	public boolean isStoryShown() {
		return storyShown;
	}

	public void setStoryShown(boolean newState) {
		storyShown = newState;
	}
}
