package net.micwin.elysium.view.storyline;

import org.apache.wicket.RestartResponseException;

import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.view.BasePage;

public class StoryLinePage extends BasePage {

	public StoryLinePage() {
		super(false);

		ensureAvatarPresent();

		/**
		 * Redirect to item page
		 */
		Avatar avatar = getAvatar();
		getElysiumSession().setStoryShown(true);
		throw new RestartResponseException(avatar.getStoryLineItem().getItemPage());
	}

	public static boolean userCanShow(User user) {
		return user != null;
	}
}
