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

import data.ConversationOption;
import data.Game;

/**
 * Changes a conversation option. It can be en- or disabled temporarily or
 * permanently, and the text, answer and event text can be changed.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class ChangeConversationOptionAction extends AbstractAction {

	/**
	 * The option.
	 */
	private ConversationOption option;

	/**
	 * Enabling or disabling the option.
	 */
	private Enabling enabling;

	/**
	 * Enabling or disabling whether this action should be disabled
	 * (permanently) after being chosen once.
	 */
	private Enabling enablingDisableOption;

	/**
	 * The new text. If {@code null}, the old will not be changed.
	 */
	private String newText;

	/**
	 * The new answer. If {@code null}, the old will not be changed.
	 */
	private String newAnswer;

	/**
	 * The new event. If {@code null}, the old will not be changed.
	 */
	private String newEvent;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeConversationOptionAction(String, ConversationOption)}
	 *             instead.
	 */
	@Deprecated
	public ChangeConversationOptionAction() {
		init();
	}

	/**@param name
	 *            the name
	 * @param option
	 *            the option to be changed
	 */
	public ChangeConversationOptionAction(String name, ConversationOption option) {
		super(name);
		init();
		this.option = option;
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.enabling = Enabling.DO_NOT_CHANGE;
		this.enablingDisableOption = Enabling.DO_NOT_CHANGE;
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
	 * @return the enablingDisableOption
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Enabling getEnablingDisableOption() {
		return enablingDisableOption;
	}

	/**
	 * @param enablingDisableOption
	 *            the enablingDisableOption to set
	 */
	public void setEnablingDisableOption(Enabling enablingDisableOption) {
		this.enablingDisableOption = enablingDisableOption;
	}

	/**
	 * @return the option
	 * 
	 * XXX This should be nullable = false. This is impossible due to circular
	 * dependencies.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_CHANGECONVERSATIONOPTIONACTION_OPTION", //
	foreignKeyDefinition = "FOREIGN KEY (OPTION_ID) REFERENCES CONVERSATIONOPTION (ID) ON DELETE CASCADE") )
	public ConversationOption getOption() {
		return option;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setOption(ConversationOption option) {
		this.option = option;
	}

	/**
	 * @return the newText
	 */
	@Column(nullable = true)
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
	@Column(nullable = true)
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

	@Override
	protected void doAction(Game game) {
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
		if (enablingDisableOption == Enabling.ENABLE) {
			option.setDisablingOptionAfterChosen(true);
		} else if (enablingDisableOption == Enabling.DISABLE) {
			option.setDisablingOptionAfterChosen(false);
		}
	}

	@Override
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing conversation option ").append(option.getId());
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" it.");
		}
		if (enablingDisableOption != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enablingDisableOption.description)
					.append(" that this action should be disabled (permanently) after being chosen once.");
		}
		if (newText != null) {
			builder.append(" Setting text to '").append(newText).append("'.");
		}
		if (newAnswer != null) {
			builder.append(" Setting answer to '").append(newAnswer).append("'.");
		}
		if (newEvent != null) {
			builder.append(" Setting event to '").append(newEvent).append("'.");
		}
		return builder.toString();
	}
}
