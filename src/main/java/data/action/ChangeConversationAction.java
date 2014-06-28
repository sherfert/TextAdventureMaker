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
	private String newGreeting;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeConversationAction(Conversation)} instead.
	 */
	@Deprecated
	public ChangeConversationAction() {
	}

	/**
	 * @param conversation
	 *            the conversation to be changed
	 */
	public ChangeConversationAction(Conversation conversation) {
		super();
		this.conversation = conversation;
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	/**
	 * @param conversation
	 *            the conversation to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeConversationAction(Conversation conversation, boolean enabled) {
		super(enabled);
		this.conversation = conversation;
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	/**
	 * @return the enabling
	 */
	public Enabling getEnabling() {
		return enabling;
	}

	/**
	 * @param enabling the enabling to set
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
	 * @param newGreeting the newGreeting to set
	 */
	public void setNewGreeting(String newGreeting) {
		this.newGreeting = newGreeting;
	}

	/**
	 * @return the conversation
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * @param conversation the conversation to set
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
		if (enabling == Enabling.ENABLE) {
			conversation.setEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			conversation.setEnabled(false);
		}
	}
}
