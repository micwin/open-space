package net.micwin.elysium.view.homepage;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Collection;

import net.micwin.elysium.bpo.XpBPO;
import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.view.BasePage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The landing page.
 * 
 * @author MicWin
 * 
 */
public class HomePage extends BasePage {

	private static final Logger L = LoggerFactory.getLogger(HomePage.class);

	public HomePage() {
		super(true);
		ensureAvatarPresent();
		ensureStoryShown();

	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		addToContentBody(new Label("name", "" + getAvatar().getName()));
		addToContentBody(new Label("controller", getAvatar().getController().getLogin()));
		addToContentBody(composeXpEntry(getAvatar()));
		addToContentBody(new Label("level", NumberFormat.getIntegerInstance(getLocale()).format(getAvatar().getLevel())));
		addToContentBody(new Label("creationDate", DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale()).format(
						getAvatar().getCreationDate())));
		addToContentBody(new Label("position", "\"" + getAvatar().getPosition().toString() + "\""));
		addToContentBody(getTalents());

	}

	private Component getTalents() {

		StringBuilder builder = new StringBuilder();

		Collection<Utilization> talents = getAvatar().getTalents();

		if (L.isDebugEnabled()) {
			L.debug("avatar " + getAvatar().getName() + " has " + talents.size() + " talents:" + talents.toString());
		}
		for (Utilization talent : talents) {
			if (builder.length() > 1) {
				builder.append(", ");
			}
			builder.append(talent.getAppliance().name()).append(" (").append(talent.getLevel()).append(")");
		}
		return new Label("talents", builder.toString());
	}

	protected Component composeXpEntry(Avatar avatar) {

		long nextXp = new XpBPO().computeXpForLevel(avatar.getLevel() + 1, avatar.getPersonality());
		NumberFormat nf = NumberFormat.getIntegerInstance(getLocale());
		String labelString = nf.format(avatar.getXp()) + " / +" + nf.format(nextXp - avatar.getXp()) + " / "
						+ avatar.getTalentPoints();
		Label xpLabel = new Label("xp", labelString);
		return xpLabel;

	}

	/**
	 * Determines wether or not the specified user may show this page.
	 * 
	 * @param user
	 * @return
	 */
	public static boolean userCanShow(User user) {
		return user != null;
	}
}
