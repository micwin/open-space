package net.micwin.elysium.entities.messaging;

import java.util.Date;

import javax.persistence.Entity;

import net.micwin.elysium.entities.ElysiumEntity;

@Entity
public class Message extends ElysiumEntity {

	private Date viewedDate = null;

	private String senderID;
	private String receiverID;

	@Override
	public Class getBaseClass() {
		return Message.class;
	}

	public void setViewedDate(Date viewDate) {
		this.viewedDate = viewDate;
	}

	public Date getViewedDate() {
		return viewedDate;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getSenderID() {
		return senderID;
	}

	public void setReceiverID(String receiverID) {
		this.receiverID = receiverID;
	}

	public String getReceiverID() {
		return receiverID;
	}

}
