package net.micwin.elysium.view.collective;

public class ShowGroupPage extends net.micwin.elysium.view.BasePage {

	public ShowGroupPage() {
		super(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ensureAvatarPresent();
		ensureLoggedIn();
		ensureStoryShown();

	}
}
