package net.micwin.elysium.view.border;

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
		NavPanel navPanel = new NavPanel("navPanel");
		navPanel.setEnabled(((ElysiumSession) Session.get()).getUser() != null);
		return navPanel;
	}
}
