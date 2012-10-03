package net.micwin.elysium.view.nav;

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

import net.micwin.elysium.bpo.MessageBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.view.BasePanel;
import net.micwin.elysium.view.collective.NaniteGroupListPage;
import net.micwin.elysium.view.construction.ConstructionPage;
import net.micwin.elysium.view.fleet.FleetPage;
import net.micwin.elysium.view.homepage.HomePage;
import net.micwin.elysium.view.messages.MessagesListPage;
import net.micwin.elysium.view.storyline.StoryLinePage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class NavPanel extends BasePanel {

	public NavPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(createHomePageLink());
		add(createStoryLineLink());
		add(createGroupsLink());
		add(createReplicationLink());
		add(createMessagesLink());
		add(createMessageIndicator());
		add (createFleetLink()) ; 
	}

	private Component createMessageIndicator() {
		Label label = new Label("newMessages", "*");
		label.setVisible(getAvatar() != null && DaoManager.I.getMessageDao().hasNewMessages(getAvatar()));
		return label;
	}

	protected BookmarkablePageLink<StoryLinePage> createStoryLineLink() {
		BookmarkablePageLink<StoryLinePage> link = new BookmarkablePageLink<StoryLinePage>("storyLineLink",
						StoryLinePage.class);
		// link.setVisible(StoryLinePage.userCanShow(getUser()));

		link.setVisible(false);
		return link;
	}

	protected BookmarkablePageLink<MessagesListPage> createMessagesLink() {
		BookmarkablePageLink<MessagesListPage> link = new BookmarkablePageLink<MessagesListPage>("messagesListLink",
						MessagesListPage.class);
		// link.setVisible(StoryLinePage.userCanShow(getUser()));

		link.setVisible(getAvatar() != null);
		return link;
	}

	protected BookmarkablePageLink<NaniteGroupListPage> createGroupsLink() {
		BookmarkablePageLink<NaniteGroupListPage> link = new BookmarkablePageLink<NaniteGroupListPage>("groupsLink",
						NaniteGroupListPage.class);
		link.setVisible(NaniteGroupListPage.userCanShow(getUser()));
		return link;
	}

	protected BookmarkablePageLink<HomePage> createHomePageLink() {
		BookmarkablePageLink<HomePage> link = new BookmarkablePageLink<HomePage>("homepageLink", HomePage.class);
		link.setVisible(HomePage.userCanShow(getUser()));
		return link;
	}
	
	protected BookmarkablePageLink<FleetPage> createFleetLink() {
		BookmarkablePageLink<FleetPage> link = new BookmarkablePageLink<FleetPage>("fleetLink", FleetPage.class);
		return link;
	}

	protected BookmarkablePageLink<ConstructionPage> createReplicationLink() {
		BookmarkablePageLink<ConstructionPage> link = new BookmarkablePageLink<ConstructionPage>("replicationLink",
						ConstructionPage.class);
		link.setVisible(ConstructionPage.userCanShow(getUser()));
		return link;
	}

}
