package net.micwin.elysium.view.avatar;

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

import java.text.NumberFormat;

import net.micwin.elysium.entities.characters.Race;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.homepage.HomePage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResurrectAvatarPage extends BasePage {

	private static final Logger L = LoggerFactory.getLogger(ResurrectAvatarPage.class);

	public ResurrectAvatarPage() {
		super(true);
		ensureLoggedIn();
		ensureAvatarPresent(false);
	}

	RadioChoice<Race> personalityChoice;
	TextField<String> nameInput;

	@Override
	protected void onInitialize() {
		super.onInitialize();

		addToContentBody(new Label("deathCount", Model.of(getAvatar().getDeathCount())));
		int levelCost = getAvatarBPO().computeResurrectionLevelCost(getAvatar());

		addToContentBody(new Label("levelCost", Model.of(NumberFormat.getIntegerInstance().format(levelCost))));
		addToContentBody(getResurrectLink());
		addToContentBody(getStartOverLink());
	}

	private Component getStartOverLink() {
		return new Link("startOverLink") {
			@Override
			public void onClick() {
				getAvatarBPO().remove(getAvatar());
				setResponsePage(CreateAvatarPage.class);
			}
		};
	}

	private Component getResurrectLink() {

		return new Link("resurrectLink") {
			@Override
			public void onClick() {
				getAvatarBPO().resurrect(getAvatar());
				setResponsePage(HomePage.class);
			}
		};
	}

}
