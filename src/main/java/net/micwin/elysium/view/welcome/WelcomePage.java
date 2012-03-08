package net.micwin.elysium.view.welcome;

import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.homepage.HomePage;
import net.micwin.elysium.view.register.RegisterPanel;

public class WelcomePage extends BasePage {

	public WelcomePage() {
		super(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (getUser() != null) {
			setResponsePage(HomePage.class);
		}
		addToContentBody(new RegisterPanel());
	}

}
