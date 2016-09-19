package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Conversation;
import data.Game;

/**
 * Changes a conversation. It can be dis- or enabled and the greeting and event can be
 * changed.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class ChangeConversationAction extends AbstractAction {

	/**
	 * The conversation.
	 */
	private Conversation conversation;

	/**
	 * Enabling or disabling the conversation.
	 */
	private Enabling enabling;

	/**
	 * The new greeting. If {@code null}, the old will not be changed.
	 */
	private String newGreeting;

	/**
	 * The new event. If {@code null}, the old will not be changed.
	 */
	private String newEvent;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeConversationAction(String, Conversation)} instead.
	 */
	@Deprecated
	public ChangeConversationAction() {
		init();
	}

	/**
	 * @param conversation
	 *            the conversation to be changed
	 */
	public ChangeConversationAction(String name, Conversation conversation) {
		super(name);
		init();
		this.conversation = conversation;
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	/**
	 * @return the enabling
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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
	@Column(nullable = true)
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
	@Column(nullable = true)
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
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGECONVERSATIONACTION_CONVERSATION", //
	foreignKeyDefinition = "FOREIGN KEY (CONVERSATION_ID) REFERENCES CONVERSATION (ID) ON DELETE CASCADE") )
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
	protected void doAction(Game game) {
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

	@Override
	public String actionDescription() {
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
