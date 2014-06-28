package data;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import data.action.AbstractAction;
import data.interfaces.HasId;

/**
 * An option in a conversation. The player can choose between options to say in
 * a certain {@link ConversationLayer}
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
	private String text;

	/**
	 * The answer the player gets when choosing that option.
	 */
	private String answer;

	/**
	 * All additional actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalActions;

	/**
	 * Only enabled options from a layer are listed.
	 */
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
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ConversationOption(String, String, ConversationLayer)}
	 *             instead.
	 */
	@Deprecated
	public ConversationOption() {
	}

	/**
	 * Create an enabled ConversationOption, with given text, answer and target.
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
		this.text = text;
		this.answer = answer;
		this.target = target;
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

	@Override
	public String toString() {
		return "ConversationOption{id=" + id + ", text=" + text + ", answer="
				+ answer + ", additionalActionsIDs="
				+ NamedObject.getIDList(additionalActions) + ", enabled="
				+ enabled + ", target=" + target + "}";
	}

}
