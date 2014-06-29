package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Conversation;

/**
 * Changes a conversation. It can be dis- or enabled and the greeting can be
 * changed.
 * 
 * @author Satia
 */
@Entity
public class ChangeConversationAction extends AbstractAction {

	/**
	 * The conversation.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private Conversation conversation;

	/**
	 * Enabling or disabling the conversation.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enabling;

	/**
	 * The new greeting. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newGreeting;

	/**
	 * The new event. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newEvent;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeConversationAction(Conversation)} instead.
	 */
	@Deprecated
	public ChangeConversationAction() {
		init();
	}

	/**
	 * @param conversation
	 *            the conversation to be changed
	 */
	public ChangeConversationAction(Conversation conversation) {
		super();
		init();
		this.conversation = conversation;
	}

	/**
	 * @param conversation
	 *            the conversation to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeConversationAction(Conversation conversation, boolean enabled) {
		super(enabled);
		init();
		this.conversation = conversation;
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	/**
	 * @return the enabling
	 */
	public Enabling getEnabling() {
		return enabling;
	}

	/**
	 * @param enabling
	 *            the enabling to set
	 */
	public void setEnabling(Enabling enabling) {
		this.enabling = enabling;
	}

	/**
	 * @return the newGreeting
	 */
	public String getNewGreeting() {
		return newGreeting;
	}

	/**
	 * @param newGreeting
	 *            the newGreeting to set
	 */
	public void setNewGreeting(String newGreeting) {
		this.newGreeting = newGreeting;
	}

	/**
	 * @return the newEvent
	 */
	public String getNewEvent() {
		return newEvent;
	}

	/**
	 * @param newEvent
	 *            the newEvent to set
	 */
	public void setNewEvent(String newEvent) {
		this.newEvent = newEvent;
	}

	/**
	 * @return the conversation
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * @param conversation
	 *            the conversation to set
	 */
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	@Override
	protected void doAction() {
		// Change fields
		if (newGreeting != null) {
			conversation.setGreeting(newGreeting);
		}
		if (newEvent != null) {
			conversation.setEvent(newEvent);
		}
		if (enabling == Enabling.ENABLE) {
			conversation.setEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			conversation.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChangeConversationAction{conversationID="
				+ conversation.getId() + ", enabling=" + enabling
				+ ", newGreeting=" + newGreeting + ", newEvent=" + newEvent
				+ " " + super.toString() 
				+ "}";
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing conversation ").append(conversation.getId());
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" it.");
		}
		if (newGreeting != null) {
			builder.append(" Setting greeting to '").append(newGreeting).append("'.");
		}
		if (newEvent != null) {
			builder.append(" Setting event to '").append(newEvent).append("'.");
		}
		return builder.toString();
	}
}
