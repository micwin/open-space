package net.micwin.elysium.view.storyline;

import net.micwin.elysium.view.storyline.engineer.NotReproducablePage;
import net.micwin.elysium.view.storyline.military.LoneWolfPage;
import net.micwin.elysium.view.storyline.preserver.NewHopePage;

/**
 * Holds the different story line items with their followers and the pages to
 * display.
 * 
 * @author MicWin
 * 
 */
public enum StoryLineItem {

	/**
	 * military story line
	 */
	LONE_WOLF(null, LoneWolfPage.class),

	/**
	 * engineer story line
	 */
	NOT_REPRODUCABLE(null, NotReproducablePage.class),

	/**
	 * preserver story line
	 */
	NEW_HOPE(null, NewHopePage.class);

	public StoryLineItem getNextItem() {
		return nextItem;
	}

	public Class<? extends StoryLineItemPage> getItemPage() {
		return itemPage;
	}

	private final StoryLineItem nextItem;
	private final Class<? extends StoryLineItemPage> itemPage;

	private StoryLineItem(StoryLineItem nextItem, Class<? extends StoryLineItemPage> itemPage) {
		this.nextItem = nextItem;
		this.itemPage = itemPage;
	}
}
