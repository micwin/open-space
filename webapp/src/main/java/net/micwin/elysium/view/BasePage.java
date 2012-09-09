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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.bpo.GateBPO;
import net.micwin.elysium.bpo.MessageBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.bpo.ScannerBPO;
import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.view.avatar.CreateAvatarPage;
import net.micwin.elysium.view.avatar.ResurrectAvatarPage;
import net.micwin.elysium.view.border.ElysiumBorder;
import net.micwin.elysium.view.errors.EntityNotAccessiblePage;
import net.micwin.elysium.view.storyline.StoryLinePage;
import net.micwin.elysium.view.welcome.WelcomePage;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.support.PropertyComparator;

public abstract class BasePage extends WebPage {

	private static final String ATTR_SORT_ASCENDING = "sort.ascending";

	private static final String ATTR_SORT_PROPERTY = "sort.property";

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
	protected void addToContentBody(Component... components) {
		for (Component component : components) {
			getBorder().getBodyContainer().add(component);
		}
	}

	/**
	 * adds a list of components to the content body.
	 * 
	 * @param components
	 */
	protected void addToContentBody(Collection<Component> components) {
		addToContentBody(components.toArray(new Component[components.size()]));

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

	protected boolean isAdmin() {

		return getElysiumSession().isAdmin();
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

	/**
	 * Ensures that the logged in user has an avatar. If not, switch to avatar
	 * creation page.
	 * 
	 * @param checkAlive
	 *            wether or not the avatar is alive.
	 */
	protected void ensureAvatarPresent(boolean checkAlive) {

		ensureLoggedIn();
		if (getAvatar() == null)

		{
			throw new RestartResponseException(CreateAvatarPage.class);
		}

		if (checkAlive && !getAvatarBPO().isAlive(getAvatar())) {
			throw new RestartResponseException(ResurrectAvatarPage.class);
		}
	}

	/**
	 * Ensures that the story page has been shown this session. If not, switch
	 * to story page.
	 */
	protected void ensureStoryShown() {
		ensureAvatarPresent(true);
		if (getAvatar().getStoryLineItem() != null && !getElysiumSession().isStoryShown()) {
			throw new RestartResponseException(StoryLinePage.class);
		}
	}

	/**
	 * Ensures that the specified named entity has already been put into the
	 * session context. If not, switch to the {@link EntityNotAccessiblePage}.
	 * 
	 * @param <T>
	 * @param cls
	 *            the entities base class.
	 * @param name
	 *            the name (which normally is something human readable, not an
	 *            id)..
	 */
	protected <T extends ElysiumEntity> void ensureSessionEntityPresent(Class<T> cls, String name) {

		T entity = getElysiumSession().getNamedEntity(name);

		if (entity == null) {

			throw new RestartResponseException(EntityNotAccessiblePage.class);
		}

	}

	/**
	 * Returns an instance of the stateless gateBPO.
	 * 
	 * @return
	 */
	protected GateBPO getGateBPO() {
		return new GateBPO();
	}

	/**
	 * Returns an instance of the stateless NaniteBPO.
	 * 
	 * @return
	 */

	protected NaniteBPO getNanitesBPO() {
		return new NaniteBPO();
	}

	/**
	 * Returns an instance of the stateless scanner bpo.
	 * 
	 * @return
	 */
	protected ScannerBPO getScannerBPO() {
		return new ScannerBPO();

	}

	/**
	 * Returns the avatar bpo instance.
	 * 
	 * @return
	 */
	protected AvatarBPO getAvatarBPO() {
		return new AvatarBPO();
	}

	protected MessageBPO getMessageBPO() {
		return new MessageBPO();
	}

	protected Component createDummyLink(final String id, boolean enabled, boolean visible) {
		Link dummyLink = new Link(id) {

			@Override
			public void onClick() {
				L.debug("someone clicked on dumm link " + id);
			}
		};

		dummyLink.setEnabled(enabled);
		dummyLink.setVisible(visible);
		return dummyLink;
	}

	/**
	 * Returns a specific text key from the resource files (properties).
	 * 
	 * @param messageKey
	 * @return
	 */
	protected String getLocalizedMessage(String messageKey) {
		return getLocalizer().getString(messageKey, this);
	}

	protected Link createSortPropertyLink(String id, String caption, final String property, final boolean ascending) {

		Link link = new Link(id) {

			@Override
			public void onClick() {

				String oldSortProperty = (String) getElysiumSession().getAttribute(ATTR_SORT_PROPERTY);
				if (property.equals(oldSortProperty)) {
					getElysiumSession().setAttribute(ATTR_SORT_ASCENDING, !getSortAscending());
				} else {
					getElysiumSession().setAttribute(ATTR_SORT_PROPERTY, property);
					getElysiumSession().setAttribute(ATTR_SORT_ASCENDING, ascending);

				}
				setResponsePage(getPage().getClass());

			}
		};
		link.setBody(Model.of(caption));

		return link;
	}

	protected String getSortProperty() {
		return (String) getElysiumSession().getAttribute(ATTR_SORT_PROPERTY);
	}

	protected boolean getSortAscending() {
		Boolean attribute = (Boolean) getElysiumSession().getAttribute(ATTR_SORT_ASCENDING);
		return attribute == null ? true : attribute;
	}

	public void sort(List<? extends ElysiumEntity> nanites) {
		String sortProperty = getSortProperty();
		boolean sortPropertyAsc = getSortAscending();

		if (sortProperty != null) {

			PropertyComparator pc = new PropertyComparator(sortProperty, true, sortPropertyAsc);
			Collections.sort(nanites, pc);
		}
	}
}
