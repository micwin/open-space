package net.micwin.elysium.view.register;

import net.micwin.elysium.bpo.UserBPO;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.view.BasePanel;
import net.micwin.elysium.view.ElysiumSession;
import net.micwin.elysium.view.homepage.HomePage;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterPanel extends BasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6890646025594806822L;

	private static final Logger L = LoggerFactory.getLogger(RegisterPanel.class);

	private String login;
	private String pass;
	private String pass2;

	public RegisterPanel() {
		super("registerPanel");
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(getRegisterForm());
	}

	private Component getRegisterForm() {
		Form form = new Form("registerForm") {

			protected void onInitialize() {
				super.onInitialize();

				TextField<String> userTextField = new TextField<String>("regLogin", new PropertyModel<String>(
								RegisterPanel.this, "login"));

				add(userTextField);
				PasswordTextField passwordTextField = new PasswordTextField("regPass", new PropertyModel<String>(
								RegisterPanel.this, "pass"));
				add(passwordTextField);
				PasswordTextField password2TextField = new PasswordTextField("regPass2", new PropertyModel<String>(
								RegisterPanel.this, "pass2"));
				add(password2TextField);

				L.debug("RegisterForm initialized");
			};

			@Override
			protected void onSubmit() {
				L.error("login / pass / pass2 : " + login + " / " + pass + " / " + pass2);

				UserBPO userBPO = new UserBPO();

				String error = userBPO.register(login, pass, pass2);
				if (error != null) {
					error(error);
				} else {
					User user = userBPO.login(login, pass);
					((ElysiumSession) Session.get()).setUser(user);
					RequestCycle.get().setResponsePage(HomePage.class);
				}
				return;
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

	public void setPass2(String pass2) {
		this.pass2 = pass2;
	}

	public String getPass2() {
		return pass2;
	}
}
