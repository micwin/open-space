package net.micwin.elysium.view.storyline.preserver;

import net.micwin.elysium.view.storyline.StoryLineItem;
import net.micwin.elysium.view.storyline.StoryLineItemPage;

public class NewHopePage extends StoryLineItemPage {

	public NewHopePage() {
		super(false);
	}

	@Override
	protected StoryLineItem getStoryLineItem() {
		return StoryLineItem.NEW_HOPE;
	}

}
