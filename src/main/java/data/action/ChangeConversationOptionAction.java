package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.ConversationOption;

/**
 * Changes a conversation. It can be dis- or enabled and the greeting can be
 * changed.
 * 
 * @author Satia
 */
@Entity
public class ChangeConversationOptionAction extends AbstractAction {

	/**
	 * The option.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private ConversationOption option;

	/**
	 * Enabling or disabling the option.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enabling;

	/**
	 * The new text. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newText;

	/**
	 * The new answer. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newAnswer;

	/**
	 * The new event. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newEvent;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeConversationOptionAction(ConversationOption)}
	 *             instead.
	 */
	@Deprecated
	public ChangeConversationOptionAction() {
		init();
	}

	/**
	 * @param option
	 *            the option to be changed
	 */
	public ChangeConversationOptionAction(ConversationOption option) {
		super();
		init();
		this.option = option;
	}

	/**
	 * @param option
	 *            the option to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeConversationOptionAction(ConversationOption option,
			boolean enabled) {
		super(enabled);
		init();
		this.option = option;
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
	 * @return the option
	 */
	public ConversationOption getOption() {
		return option;
	}

	/**
	 * @param option
	 *            the option to set
	 */
	public void setOption(ConversationOption option) {
		this.option = option;
	}

	/**
	 * @return the newText
	 */
	public String getNewText() {
		return newText;
	}

	/**
	 * @param newText
	 *            the newText to set
	 */
	public void setNewText(String newText) {
		this.newText = newText;
	}

	/**
	 * @return the newAnswer
	 */
	public String getNewAnswer() {
		return newAnswer;
	}

	/**
	 * @param newAnswer
	 *            the newAnswer to set
	 */
	public void setNewAnswer(String newAnswer) {
		this.newAnswer = newAnswer;
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

	@Override
	protected void doAction() {
		// Change fields
		if (newText != null) {
			option.setText(newText);
		}
		if (newAnswer != null) {
			option.setAnswer(newAnswer);
		}
		if (newEvent != null) {
			option.setEvent(newEvent);
		}
		if (enabling == Enabling.ENABLE) {
			option.setEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			option.setEnabled(false);
		}
	}

	@Override
	public String toString() {
		return "ChangeConversationOptionAction{optionID=" + option.getId()
				+ ", enabling=" + enabling + ", newText=" + newText
				+ ", newAnswer=" + newAnswer + ", newEvent=" + newEvent + " "
				+ super.toString() + "}";
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing conversation option ").append(option.getId());
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" it.");
		}
		if (newText != null) {
			builder.append(" Setting text to '").append(newText).append("'.");
		}
		if (newAnswer != null) {
			builder.append(" Setting answer to '").append(newAnswer)
					.append("'.");
		}
		if (newEvent != null) {
			builder.append(" Setting event to '").append(newEvent).append("'.");
		}
		return builder.toString();
	}
}