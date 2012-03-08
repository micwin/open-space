package net.micwin.elysium.view.avatar;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.model.characters.Avatar.Personality;
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

	RadioChoice<Personality> personalityChoice;
	TextField<String> nameInput;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Form form = createForm();
		addToContentBody(form);
	}

	protected Form createForm() {
		Form form = new Form("createAvatarForm") {

			protected void onInitialize() {
				super.onInitialize();
				personalityChoice = new RadioChoice<Personality>("personality", new Model<Personality>(
								Personality.ENGINEER), new ListModel<Personality>(Personality.asList()));
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
