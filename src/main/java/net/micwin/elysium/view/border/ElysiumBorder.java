package net.micwin.elysium.view.border;

import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.view.ElysiumSession;
import net.micwin.elysium.view.login.AuthPanel;
import net.micwin.elysium.view.nav.NavPanel;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElysiumBorder extends Border {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1650615903715041577L;
	private static final Logger L = LoggerFactory.getLogger(ElysiumBorder.class);

	public ElysiumBorder(boolean addFeedbackPanel) {
		super("layout");

		// add(new BoxBorder("bodyBorder"));

		if (addFeedbackPanel) {
			add(new FeedbackPanel("feedback"));
		}

		addToBorder(getNavPanel());
		addToBorder(getAuthPanel());

		User user = getUser();
		if (user == null) {
			addToBorder(new Label("roles", new ResourceModel("label.notYetLoggedIn")));
		} else {
			String rolesString = user.getRole().name() + "/" + user.getState().name();
			addToBorder(new Label("roles", rolesString));
		}

	}

	private User getUser() {
		return ((ElysiumSession) Session.get()).getUser();
	}

	private Component getAuthPanel() {
		return new AuthPanel();
	}

	private Component getNavPanel() {
		return new NavPanel("navPanel");
	}
}
