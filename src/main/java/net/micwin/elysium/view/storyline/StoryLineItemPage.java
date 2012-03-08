package net.micwin.elysium.view.storyline;

import net.micwin.elysium.bpo.StoryLineBPO;
import net.micwin.elysium.view.BasePage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;

public abstract class StoryLineItemPage extends BasePage {

	private static enum State {

		OPEN, RESOLVED;
	}

	public StoryLineItemPage() {
		super(false);
	}

	protected StoryLineItemPage(boolean addFeedbackPanel) {
		super(addFeedbackPanel);
		ensureAvatarPresent();

		if (new StoryLineBPO().checkSolved(getStoryLineItem(), getAvatar()) && getStoryLineItem().getNextItem() != null) {
			throw new RestartResponseException(getStoryLineItem().getNextItem().getItemPage());
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		// check wether we should directly redirect to next story item

		addToContentBody(getStoryStatus());
	}

	private Label getStoryStatus() {

		boolean resolved = new StoryLineBPO().checkSolved(getStoryLineItem(), getAvatar());
		return new Label("status", resolved ? State.RESOLVED.name() : State.OPEN.name());
	}

	@Override
	protected void onDetach() {
		getElysiumSession().setStoryShown(true);
		super.onDetach();
	}

	/**
	 * The story line item this page is showing.
	 * 
	 * @return
	 */
	protected abstract StoryLineItem getStoryLineItem();
}
