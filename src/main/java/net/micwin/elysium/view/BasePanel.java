package net.micwin.elysium.view;

import net.micwin.elysium.model.characters.User;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class BasePanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1340374644079100324L;

	public BasePanel(String id) {
		super(id);
	}

	protected User getUser() {
		return getEylsiumSession().getUser();
	}

	protected ElysiumSession getEylsiumSession() {
		return (ElysiumSession) Session.get();
	}

}
