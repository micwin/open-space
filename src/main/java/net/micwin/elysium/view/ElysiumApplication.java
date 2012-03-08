package net.micwin.elysium.view;

import net.micwin.elysium.bpo.AdminBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.model.GalaxyTimer;
import net.micwin.elysium.view.welcome.WelcomePage;

import org.apache.wicket.IPageFactory;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElysiumApplication extends WebApplication {

	private static final Logger L = LoggerFactory.getLogger(ElysiumApplication.class);

	private DaoManager daoManager;

	private GalaxyTimer galaxyTimer;

	public ElysiumApplication() {
	}

	public void init() {
		AdminBPO adminBpo = new AdminBPO();
		adminBpo.ensureInitialDbSetup();
		galaxyTimer = adminBpo.restoreGalaxyTimer();
	}

	public GalaxyTimer getGalaxyTimer() {
		return galaxyTimer;
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return WelcomePage.class;
	}

	@Override
	public Session newSession(Request request, Response response) {

		return new ElysiumSession(request);
	}

	@Override
	protected void onDestroy() {
		new AdminBPO().saveGalaxyTimer(galaxyTimer);
	}

	public void setDaoManager(DaoManager daoManager) {
		this.daoManager = daoManager;
	}

	public DaoManager getDaoManager() {
		return daoManager;
	}

}
