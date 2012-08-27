package net.micwin.elysium.entities.messaging;

import java.util.Date;

import javax.persistence.Entity;

import net.micwin.elysium.entities.ElysiumEntity;

@Entity
public class Message extends ElysiumEntity {

	private Date viewedDate = null;

	private String senderID;
	private String receiverID;

	private Date date;

	private String text;

	/**
	 * The local mailbox this message is in.
	 */
	private String mailBox;

	@Override
	public Class getBaseClass() {
		return Message.class;
	}

	public Message copy(boolean alsoCopyId) {

		Message copy = new Message();

		if (alsoCopyId)
			copy.setId(getId());
		copy.senderID = senderID;
		copy.receiverID = receiverID;
		copy.text = text;
		copy.viewedDate = viewedDate;
		copy.date = date;

		return copy;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setMailBox(String endPointId) {
		this.mailBox = endPointId;
	}

	public String getMailBox() {
		return this.mailBox;
	}

}
