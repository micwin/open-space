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

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.model.characters.Avatar.Race;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.homepage.HomePage;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAvatarPage extends BasePage {

	private static final Logger L = LoggerFactory.getLogger(CreateAvatarPage.class);

	public CreateAvatarPage() {
		super(true);
		ensureLoggedIn();
	}

	RadioChoice<Race> personalityChoice;
	TextField<String> nameInput;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Form form = createForm();
		addToContentBody(form);
	}

	protected Form createForm() {
		Form form = new Form("createAvatarForm") {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				personalityChoice = new RadioChoice<Race>("personality", new Model<Race>(Race.NANITE),
								new ListModel<Race>(Race.asList()));
				add(personalityChoice);

				nameInput = new TextField<String>("name", new Model<String>(""));
				add(nameInput);
			}

			@Override
			protected void onSubmit() {

				String result = new AvatarBPO().create(getUser(), nameInput.getModel().getObject(),
								personalityChoice.getModelObject());
				if (result == null) {
					setResponsePage(HomePage.class);
				} else {
					error(result);
				}
			}
		};
		return form;
	}
}
