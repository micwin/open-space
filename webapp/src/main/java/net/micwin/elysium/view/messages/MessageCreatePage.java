package net.micwin.elysium.view.messages;

import javax.security.auth.callback.TextInputCallback;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.messaging.IMessageEndpoint;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.collective.NaniteGroupListPage;
import net.micwin.elysium.view.collective.NaniteGroupShowPage;

public class MessageCreatePage extends BasePage {

	private TextArea<String> textArea;

	public MessageCreatePage() {
		super(false);
		ensureAvatarPresent(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Avatar receiver = getElysiumSession().getNamedEntity("receiver");
		if (receiver == null) {
			error("need a receiver for this operation");
			setResponsePage(NaniteGroupListPage.class);
			return;
		} else if (!getMessageBPO().canSendMessage(getAvatar(), receiver)) {
			setResponsePage(NaniteGroupListPage.class);
			return;
		}

		addToContentBody(new Label("receiver", receiver.getName()));
		addToContentBody(getMessageInputForm());

	}

	private Component getMessageInputForm() {
		Form form = new Form("messageInputForm") {

			@Override
			protected void onInitialize() {
				super.onInitialize();

				add(getTextInput());
		
			}

			@Override
			protected void onSubmit() {
				getMessageBPO().send(getAvatar(), (IMessageEndpoint) getElysiumSession().getNamedEntity("receiver"),
								getTextInput().getDefaultModelObject().toString());
				setResponsePage(NaniteGroupListPage.class);

			}
		};

		return form;
	}

	private Component getTextInput() {

		if (textArea == null) {
			textArea = new TextArea<String>("text", Model.of(""));
		}
		return textArea;
	}

}
