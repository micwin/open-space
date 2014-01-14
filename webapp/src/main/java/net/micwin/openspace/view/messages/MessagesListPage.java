package net.micwin.openspace.view.messages;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.messaging.Message;
import net.micwin.openspace.view.BasePage;
import net.micwin.openspace.view.OpenSpaceWicketModel;

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

		final List<IModel<Message>> models = OpenSpaceWicketModel.asModelList(messages);

		Component talentsComponent = new RefreshingView<Message>("messages") {
			protected Iterator<IModel<Message>> getItemModels() {
				return models.iterator();
			}

			protected void populateItem(Item<Message> item) {
				final IModel<Message> messageModel = item.getModel();
				final Message message = (Message) messageModel.getObject();
				item.add(new Label("time", Model.of(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
								Locale.GERMANY).format(message.getDate()))));
				item.add(new Label("sender", Model.of(message.getSenderID())));
				item.add(new Label("receiver", Model.of(message.getReceiverID())));
				item.add(new Label("message", Model.of(message.getText())));
				item.add(new Label("read", Model.of(message.getViewedDate() == null ? "*" : "")));
				item.add((getDeleteLink(message)));

				if (message.getViewedDate() == null) {
					message.setViewedDate(new Date());
					DaoManager.I.getMessageDao().update(message);
				}

			}

			private Component getDeleteLink(Message message) {

				boolean canDelete = getMessageBPO().canDelete(getAvatar(), message);
				if (!canDelete)
					return createDummyLink("delete", false, false);
				Link<Message> deleteLink = new Link<Message>("delete", OpenSpaceWicketModel.of(message)) {

					@Override
					public void onClick() {
						DaoManager.I.getMessageDao().delete(getModelObject());
						setResponsePage(MessagesListPage.class);
					}
				};

				return deleteLink;
			}
		};

		return talentsComponent;
	}
}
