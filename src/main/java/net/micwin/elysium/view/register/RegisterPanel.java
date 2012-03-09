package net.micwin.elysium.view.register;

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
