package net.micwin.elysium.view;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.model.GalaxyTimer;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.view.avatar.CreateAvatarPage;
import net.micwin.elysium.view.border.ElysiumBorder;
import net.micwin.elysium.view.storyline.StoryLinePage;
import net.micwin.elysium.view.welcome.WelcomePage;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.border.Border;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage extends WebPage {

	public static final String[] NUMBERS_IN_ENGLISH = { "Zero", "One", "Two", "Three", "Four", "Five" };

	private static final Logger L = LoggerFactory.getLogger(BasePage.class);

	private Border border;

	private final boolean addFeedbackPanel;

	public BasePage(boolean addFeedbackPanel) {
		super();
		this.addFeedbackPanel = addFeedbackPanel;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		this.border = new ElysiumBorder(addFeedbackPanel);
		addChild(border);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderCSSReference("skins/default/style.css");

	}

	public Border getBorder() {
		return border;
	}

	/**
	 * Dont use this! use #addToBody, #addToBorder or #addChild instead.
	 * 
	 * @see #addChild(Component)
	 * @see #addToContentBody(Component)
	 * @see #addToBorder(Component)
	 */
	@Deprecated
	@Override
	public MarkupContainer add(Component... component) {
		return super.add(component);
	}

	/**
	 * Use this method if the passed component shopuld part of the layout border
	 * instead of the content body
	 * 
	 * @param component
	 */
	protected void addToBorder(Component component) {
		getBorder().add(component);
	}

	/**
	 * adds a component to the content body.
	 * 
	 * @param component
	 */
	protected void addToContentBody(Component component) {
		getBorder().getBodyContainer().add(component);
	}

	/**
	 * Adds a component as a direct child of this page.
	 * 
	 * @param c
	 */
	protected void addChild(Component c) {
		super.add(c);
	}

	protected ElysiumSession getElysiumSession() {
		return (ElysiumSession) Session.get();
	}

	protected User getUser() {
		return getElysiumSession().getUser();
	}

	protected Avatar getAvatar() {
		return new AvatarBPO().findByController(getUser());
	}

	/**
	 * Ensures that the user is logged in. If not, does a redirect to the
	 * welcome page.
	 */
	protected void ensureLoggedIn() {
		if (getUser() == null) {
			throw new RestartResponseException(WelcomePage.class);
		}
	}

	protected void ensureAvatarPresent() {

		if (getAvatar() == null)

		{
			throw new RestartResponseException(CreateAvatarPage.class);
		}
	}

	protected void ensureStoryShown() {
		if (!getElysiumSession().isStoryShown()) {
			throw new RestartResponseException(StoryLinePage.class);
		}
	}

	protected GalaxyTimer getGalaxyTimer() {
		return ((ElysiumApplication) Application.get()).getGalaxyTimer();
	}

}
