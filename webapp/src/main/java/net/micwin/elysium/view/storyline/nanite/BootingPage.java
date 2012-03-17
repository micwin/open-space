package net.micwin.elysium.view.storyline.nanite;

import org.apache.wicket.markup.html.basic.Label;

import net.micwin.elysium.view.storyline.StoryLineItem;
import net.micwin.elysium.view.storyline.StoryLineItemPage;

public class BootingPage extends StoryLineItemPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7162880964860760121L;

	public BootingPage() {
		super(false);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addToContentBody(new Label("avatarName", getAvatar().getName()));
	}

	@Override
	protected StoryLineItem getStoryLineItem() {
		return StoryLineItem.BOOTING;
	}
}
