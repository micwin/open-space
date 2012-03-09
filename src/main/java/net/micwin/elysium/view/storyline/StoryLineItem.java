package net.micwin.elysium.view.storyline;

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
 OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

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
