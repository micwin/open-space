package net.micwin.elysium.view;

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
