package net.micwin.elysium.view.login;

import net.micwin.elysium.bpo.UserBPO;
import net.micwin.elysium.model.characters.User;
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
			link.add(new Label("label", Model.of("Hello, " + getUser())));
		}
		return link;

	}

	private Component getAuthForm() {
		Form<User> form = new Form<User>("loginForm") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4334206218142646974L;

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
