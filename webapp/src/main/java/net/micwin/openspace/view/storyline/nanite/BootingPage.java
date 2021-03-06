package net.micwin.openspace.view.storyline.nanite;

import net.micwin.openspace.view.storyline.StoryLineItem;
import net.micwin.openspace.view.storyline.StoryLineItemPage;

import org.apache.wicket.markup.html.basic.Label;

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
