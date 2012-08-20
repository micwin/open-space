package net.micwin.elysium.view.messages;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.messaging.Message;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import edu.emory.mathcs.backport.java.util.Collections;

public class MessagesListPage extends BasePage {

	public MessagesListPage() {
		super(false);
		ensureAvatarPresent(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		List<Message> messages = new LinkedList<Message>(getMessageBPO().getMessages(getAvatar()));
		addToContentBody(composeMessagesComponent(messages));
		addToContentBody(new Label("messageCount", Model.of(messages.size())));
	}

	private Component composeMessagesComponent(List<Message> messages) {

		Collections.sort(messages, new Comparator<Message>() {

			@Override
			public int compare(Message o1, Message o2) {
				return o2.getDate().compareTo(o1.getDate());
			}
		});

		final List<IModel<Message>> models = ElysiumWicketModel.asModelList(messages);

		Component talentsComponent = new RefreshingView<Message>("messages") {
			protected Iterator<IModel<Message>> getItemModels() {
				return models.iterator();
			}

			protected void populateItem(Item<Message> item) {
				final IModel<Message> messageModel = item.getModel();
				final Message message = (Message) messageModel.getObject();
				item.add(new Label("time", Model.of(message.getDate())));
				item.add(new Label("sender", Model.of(message.getSenderID())));
				item.add(new Label("receiver", Model.of(message.getReceiverID())));
				item.add(new Label("message", Model.of(message.getText())));
				item.add(new Label("read", Model.of(message.getViewedDate() == null ? "*" : "")));
				item.add((getDeleteLink(message, isAdmin())));
			}

			private Component getDeleteLink(Message message, boolean admin) {
				if (!admin)
					return createDummyLink("delete", false, false);
				Link<Message> deleteLink = new Link<Message>("delete", ElysiumWicketModel.of(message)) {

					@Override
					public void onClick() {
						DaoManager.I.getMessageDao().delete(getModelObject());
						setResponsePage(MessagesListPage.class) ; 
					}
				};

				return deleteLink;
			}
		};

		return talentsComponent;
	}
}
