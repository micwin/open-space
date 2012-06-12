package net.micwin.elysium.view.login;

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

import net.micwin.elysium.bpo.UserBPO;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.view.BasePanel;
import net.micwin.elysium.view.homepage.HomePage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthPanel extends BasePanel {

	private static final Logger L = LoggerFactory.getLogger(AuthPanel.class);

	private String login;
	private String pass;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6003819007820410997L;

	public AuthPanel() {
		super("authPanel");
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		L.info("user stored in session:" + getEylsiumSession().getUser());

		add(getLogoutLink());
		add(getAuthForm());
	}

	private Component getLogoutLink() {

		BookmarkablePageLink<LogoutPage> link = new BookmarkablePageLink<LogoutPage>("logoutLink", LogoutPage.class);
		link.setVisible(getUser() != null);
		
		if (link.isVisible()) {
			link.add(new Label("label", Model.of("Logout")));
		}
		return link;

	}

	private Component getAuthForm() {
		Form<User> form = new Form<User>("loginForm") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4334206218142646974L;

			@Override
			protected void onInitialize() {
				super.onInitialize();

				TextField<String> userTextField = new TextField<String>("login", new PropertyModel<String>(
								AuthPanel.this, "login"));

				add(userTextField);
				PasswordTextField passwordTextField = new PasswordTextField("password", new PropertyModel<String>(
								AuthPanel.this, "pass"));
				add(passwordTextField);
			};

			@Override
			protected void onSubmit() {
				L.info("submitted user '" + login + "' and pass '" + pass.charAt(0) + "..."
								+ pass.charAt(pass.length() - 1) + "'");

				User user = new UserBPO().login(login, pass);
				if (user == null) {
					error("user '" + login + "' unknown or pass mismatch");
				} else {
					getEylsiumSession().setUser(user);
					L.debug("user logged in");
					// issue a reload
					setResponsePage(HomePage.class);
				}
			}
		};

		form.setVisible(getUser() == null);

		return form;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPass() {
		return pass;
	}
}
