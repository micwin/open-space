package net.micwin.elysium.bpo;

import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.view.storyline.StoryLineItem;

public class StoryLineBPO extends BaseBPO {

	/**
	 * Checks wether this story line item is solved. If yes, checks wether this
	 * story line item is the active story ine item for the avatar and if,
	 * rewards the avatar.
	 * 
	 * @param storyLineItem
	 * @param avatar
	 * @return
	 */
	public boolean checkSolved(StoryLineItem storyLineItem, Avatar avatar) {
		return false;
	}

}
