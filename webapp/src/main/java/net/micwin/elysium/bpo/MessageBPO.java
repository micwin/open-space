package net.micwin.elysium.bpo;

import java.util.List;

import net.micwin.elysium.entities.GalaxyTimer;
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
		Message message = new Message();
		message.setDate(GalaxyTimer.get().getGalaxyDate());
		message.setSenderID(sender.getEndPointId());
		message.setReceiverID(receiver.getEndPointId());
		message.setText(messageText);
		getMessageDao().send(message);
	}

	/**
	 * Wether or not one avatar can send another avatar a message.
	 * 
	 * @param sender
	 * @param receiver
	 * @return
	 */
	public boolean canSendMessage(Avatar sender, Avatar receiver) {
		// until now, everything is always ok :)
		return true;
	}
}
