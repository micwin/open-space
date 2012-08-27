package net.micwin.elysium.bpo;

import java.util.Date;
import java.util.List;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.messaging.Message;
import net.micwin.elysium.messaging.IMessageEndpoint;

public class MessageBPO extends BaseBPO {

	/**
	 * get messagesmin which specified end point either is sender or receiver.
	 * 
	 * @param endPoint
	 * @return
	 */
	public List<Message> getMessages(IMessageEndpoint endPoint) {
		return getMessageDao().findByEndPoint(endPoint);
	}

	public void send(IMessageEndpoint sender, IMessageEndpoint receiver, String messageText) {

		Message template = new Message();
		template.setDate(new Date());
		template.setSenderID(sender.getEndPointId());
		template.setReceiverID(receiver.getEndPointId());
		template.setText(messageText);

		if (sender.hasMailBox()) {
			Message senderCopy = (Message) template.copy(false);
			senderCopy.setViewedDate(new Date());
			senderCopy.setMailBox(sender.getEndPointId());
			DaoManager.I.getMessageDao().send(senderCopy);
		}

		if (receiver.hasMailBox()) {
			Message receiverCopy = template.copy(false);
			receiverCopy.setMailBox(receiver.getEndPointId());
			DaoManager.I.getMessageDao().send(receiverCopy);

		}

	}

	/**
	 * Wether or not specified endpoint can send another end point a message.
	 * 
	 * @param sender
	 * @param receiver
	 * @return
	 */
	public boolean canSendMessage(IMessageEndpoint sender, IMessageEndpoint receiver) {

		return receiver != null && receiver.hasMailBox();
	}

	public boolean canDelete(Avatar avatar, Message message) {
		return avatar.getEndPointId().equals(message.getMailBox());
	}
}
