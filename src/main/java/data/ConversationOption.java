package data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import data.action.AbstractAction;
import data.action.AbstractAction.Enabling;
import data.action.ChangeConversationOptionAction;
import data.interfaces.HasId;

/**
 * An option in a conversation. The player can choose between options to say in
 * a certain {@link ConversationLayer}
 * 
 * XXX A possible improvement would be to include removeOption /
 * removeOptionPermanently options
 * 
 * @author Satia
 */
@Entity
public class ConversationOption implements HasId {

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * The text the player says when choosing that option.
	 */
	@Column(nullable = false)
	private String text;

	/**
	 * The answer the player gets when choosing that option.
	 */
	@Column(nullable = false)
	private String answer;

	/**
	 * A text describing what is going on additionally. If empty, nothing is
	 * printed.
	 */
	@Column(nullable = false)
	private String event;

	/**
	 * All additional actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalActions;

	/**
	 * Only enabled options from a layer are listed.
	 */
	@Column(nullable = false)
	private boolean enabled;

	/**
	 * The target layer, which the player gets to after choosing that option. If
	 * this is {@code null}, the option will end the conversation. By default,
	 * this should be the owning layer.
	 * 
	 * This should only point to layers of the same conversation.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private ConversationLayer target;

	/**
	 * The {@link ChangeConversationOptionAction} which would disable this
	 * option.
	 * 
	 * Note: This is NOT the Inverse connection of
	 * {@link ChangeConversationOptionAction#option}.
	 */
	// XXX why not unnullable?
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	//(nullable = false)
	private ChangeConversationOptionAction disableAction;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ConversationOption(String, String, ConversationLayer)}
	 *             instead.
	 */
	@Deprecated
	public ConversationOption() {
		this.additionalActions = new ArrayList<>();
		this.disableAction = new ChangeConversationOptionAction(this, false);
		this.disableAction.setEnabling(Enabling.DISABLE);
	}

	/**
	 * Create an enabled ConversationOption, with given text, answer and target.
	 * No event text. By default, the option will not disappear after being
	 * chosen once.
	 * 
	 * @param text
	 *            the text
	 * @param answer
	 *            the answer
	 * @param target
	 *            the target
	 */
	public ConversationOption(String text, String answer,
			ConversationLayer target) {
		this();
		this.text = text;
		this.answer = answer;
		this.event = "";
		this.target = target;
		this.enabled = true;
	}

	/**
	 * Create an enabled ConversationOption, with given text, answer, event and
	 * target. By default, the option will not disappear after being
	 * chosen once.
	 * 
	 * @param text
	 *            the text
	 * @param answer
	 *            the answer
	 * @param event
	 *            the event
	 * @param target
	 *            the target
	 */
	public ConversationOption(String text, String answer, String event,
			ConversationLayer target) {
		this();
		this.text = text;
		this.answer = answer;
		this.event = event;
		this.target = target;
		this.enabled = true;
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the additionalActions
	 */
	public List<AbstractAction> getAdditionalActions() {
		return additionalActions;
	}

	/**
	 * @param additionalActions
	 *            the additionalActions to set
	 */
	public void setAdditionalActions(List<AbstractAction> additionalActions) {
		this.additionalActions = additionalActions;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the target
	 */
	public ConversationLayer getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(ConversationLayer target) {
		this.target = target;
	}

	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action to add.
	 */
	public void addAdditionalAction(AbstractAction action) {
		additionalActions.add(action);
	}

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action to remove.
	 */
	public void removeAdditionalAction(AbstractAction action) {
		additionalActions.remove(action);
	}

	/**
	 * Sets whether this action should be disabled (permanently) after being
	 * chosen once.
	 * 
	 * @param disabling
	 */
	public void setDisablingOptionAfterChosen(boolean disabling) {
		disableAction.setEnabled(disabling);
	}

	/**
	 * @return whether this action should be disabled (permanently) after being
	 *         chosen once.
	 */
	public boolean isDisablingOptionAfterChosen() {
		return disableAction.isEnabled();
	}

	/**
	 * Choose this option. Simply triggers all additional actions. Also disable
	 * this option if specified.
	 */
	public void choose() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Choosing {0}", this);
		disableAction.triggerAction();
		// Trigger additional actions
		for (AbstractAction abstractAction : additionalActions) {
			abstractAction.triggerAction();
		}
	}

	@Override
	public String toString() {
		return "ConversationOption{id=" + id + ", text=" + text + ", answer="
				+ answer + ", event=" + event + ", additionalActionsIDs="
				+ NamedObject.getIDList(additionalActions) + ", enabled="
				+ enabled + ", targetID=" + target.getId() + ", disableActionID="
						+ disableAction.getId() + "}";
	}

}
