package net.micwin.elysium.bpo;

import java.util.List;

import net.micwin.elysium.entities.messaging.Message;
import net.micwin.elysium.messaging.IMessageEndpoint;

public class MessageBPO extends BaseBPO {


	/**
	 * Returns messages denoted by specified parameters.
	 * 
	 * @param sender
	 *            optional.
	 * @param receiver
	 *            optional.
	 * @param filterViewed
	 *            Wether or not already viewed messages are filtered (removed)
	 *            and hence not part of result.
	 * @return
	 */
	public List<Message> getMessages(IMessageEndpoint sender, IMessageEndpoint receiver, boolean filterViewed) {

		return getMessageDao().find(sender, receiver, filterViewed);
	}
}
