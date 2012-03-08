package net.micwin.elysium.view.nav;

import net.micwin.elysium.view.BasePanel;
import net.micwin.elysium.view.homepage.HomePage;
import net.micwin.elysium.view.replication.ConstructionPage;
import net.micwin.elysium.view.storyline.StoryLinePage;

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
		add(createReplicationLink());

	}

	protected BookmarkablePageLink<StoryLinePage> createStoryLineLink() {
		BookmarkablePageLink<StoryLinePage> link = new BookmarkablePageLink<StoryLinePage>("storyLineLink",
						StoryLinePage.class);
		link.setVisible(StoryLinePage.userCanShow(getUser()));
		return link;
	}

	protected BookmarkablePageLink<HomePage> createHomePageLink() {
		BookmarkablePageLink<HomePage> link = new BookmarkablePageLink<HomePage>("homepageLink", HomePage.class);
		link.setVisible(HomePage.userCanShow(getUser()));
		return link;
	}

	protected BookmarkablePageLink<ConstructionPage> createReplicationLink() {
		BookmarkablePageLink<ConstructionPage> link = new BookmarkablePageLink<ConstructionPage>("replicationLink",
						ConstructionPage.class);
		link.setVisible(ConstructionPage.userCanShow(getUser()));
		return link;
	}

}
