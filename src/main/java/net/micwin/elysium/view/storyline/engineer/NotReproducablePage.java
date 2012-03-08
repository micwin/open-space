package net.micwin.elysium.view.storyline.engineer;

import net.micwin.elysium.view.storyline.StoryLineItem;
import net.micwin.elysium.view.storyline.StoryLineItemPage;

public class NotReproducablePage extends StoryLineItemPage {

	public NotReproducablePage() {
		super(false);
	}

	@Override
	protected StoryLineItem getStoryLineItem() {
		return StoryLineItem.NOT_REPRODUCABLE;
	}

}
