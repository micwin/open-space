package net.micwin.openspace.view.messages;

import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.messaging.IMessageEndpoint;
import net.micwin.openspace.view.BasePageView;
import net.micwin.openspace.view.collective.NaniteGroupListPage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

public class MessageCreatePage extends BasePageView {

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
