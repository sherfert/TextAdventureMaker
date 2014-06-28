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
	private String newText;
	
	/**
	 * The new answer. If {@code null}, the old will not be changed.
	 */
	private String newAnswer;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeConversationOptionAction(ConversationOption)} instead.
	 */
	@Deprecated
	public ChangeConversationOptionAction() {
	}

	/**
	 * @param option
	 *            the option to be changed
	 */
	public ChangeConversationOptionAction(ConversationOption option) {
		super();
		this.option = option;
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	/**
	 * @param option
	 *            the option to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeConversationOptionAction(ConversationOption option, boolean enabled) {
		super(enabled);
		this.option = option;
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
	 * @return the option
	 */
	public ConversationOption getOption() {
		return option;
	}

	/**
	 * @param option the option to set
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
	 * @param newText the newText to set
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
	 * @param newAnswer the newAnswer to set
	 */
	public void setNewAnswer(String newAnswer) {
		this.newAnswer = newAnswer;
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
		if (enabling == Enabling.ENABLE) {
			option.setEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			option.setEnabled(false);
		}
	}
}
