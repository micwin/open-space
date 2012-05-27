package net.micwin.elysium.view;

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
		String skin = null;

		if (getUser() != null) {
			skin = getUser().getSkin();
		}

		if (skin == null) {
			skin = "default";
		}
		response.renderCSSReference("skins/" + skin + "/style.css");

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
		return new AvatarBPO().findByUser(getUser());
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
