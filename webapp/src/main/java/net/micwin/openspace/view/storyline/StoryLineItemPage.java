package net.micwin.openspace.view.storyline;

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

import net.micwin.openspace.bpo.StoryLineBPO;
import net.micwin.openspace.view.BasePage;

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
		ensureAvatarPresent(true);

		Boolean storyItemResolved = new StoryLineBPO().checkSolved(getStoryLineItem(), getAvatar());
		if (storyItemResolved != null && storyItemResolved && getStoryLineItem().getNextItem() != null) {
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

		Boolean resolved = new StoryLineBPO().checkSolved(getStoryLineItem(), getAvatar());

		boolean visible = resolved != null;

		if (visible) {
			Label label = new Label("status", resolved ? State.RESOLVED.name() : State.OPEN.name());
			return label;
		} else {
			return new Label("status", "");
		}
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
