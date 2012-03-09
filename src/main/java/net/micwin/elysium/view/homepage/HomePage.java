package net.micwin.elysium.view.homepage;

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
