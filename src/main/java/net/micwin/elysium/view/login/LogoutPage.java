package net.micwin.elysium.view.login;

import net.micwin.elysium.bpo.UserBPO;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.welcome.WelcomePage;

import org.apache.wicket.RestartResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A page for only one action: logging out.
 * 
 * @author MicWin
 * 
 */
public class LogoutPage extends BasePage {

	private static final Logger L = LoggerFactory.getLogger(LogoutPage.class);

	public LogoutPage() {
		super(false);

		User user = getUser();

		if (L.isDebugEnabled()) {
			L.debug("logged out user '" + user + "" + "'");
		}

		new UserBPO().logout(user);

		getElysiumSession().setUser(null);
		throw new RestartResponseException(WelcomePage.class);
	}

}
