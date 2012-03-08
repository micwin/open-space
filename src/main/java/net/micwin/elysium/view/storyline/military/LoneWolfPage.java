package net.micwin.elysium.view.storyline.military;

import net.micwin.elysium.view.storyline.StoryLineItem;
import net.micwin.elysium.view.storyline.StoryLineItemPage;

public class LoneWolfPage extends StoryLineItemPage {

	public LoneWolfPage() {
		super(false);

	}

	@Override
	protected StoryLineItem getStoryLineItem() {
		return StoryLineItem.LONE_WOLF;
	}

}
