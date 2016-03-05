package data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
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
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class ConversationOption implements HasId {

	/**
	 * The id.
	 */
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
	 * A text describing what is going on additionally. If empty, nothing is
	 * printed.
	 */
	private String event;

	/**
	 * All additional actions.
	 */
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
	private ConversationLayer target;

	/**
	 * The {@link ChangeConversationOptionAction} which would disable this
	 * option.
	 * 
	 * Note: This is NOT the Inverse connection of
	 * {@link ChangeConversationOptionAction#option}.
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 */
	// XXX why not unnullable?
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn
	@Access(AccessType.FIELD)
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
	public ConversationOption(String text, String answer, ConversationLayer target) {
		this();
		this.text = text;
		this.answer = answer;
		this.event = "";
		this.target = target;
		this.enabled = true;
	}

	/**
	 * Create an enabled ConversationOption, with given text, answer, event and
	 * target. By default, the option will not disappear after being chosen
	 * once.
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
	public ConversationOption(String text, String answer, String event, ConversationLayer target) {
		this();
		this.text = text;
		this.answer = answer;
		this.event = event;
		this.target = target;
		this.enabled = true;
	}

	@Override
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the text
	 */
	@Column(nullable = false)
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
	@Column(nullable = false)
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
	@Column(nullable = false)
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
	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable(name = "CONVOP_AA", foreignKey = @ForeignKey(name = "FK_CONVERSATIONOPTION_ADDITIONALACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (ConversationOption_ID) REFERENCES CONVERSATIONOPTION (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_CONVERSATIONOPTION_ADDITIONALACTIONS_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
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
	@Column(nullable = false)
	public boolean getEnabled() {
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
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_CONVERSATIONOPTION_TARGET", //
	foreignKeyDefinition = "FOREIGN KEY (TARGET_ID) REFERENCES CONVERSATIONLAYER (ID) ON DELETE SET NULL") )
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
		return disableAction.getEnabled();
	}

	/**
	 * Choose this option. Simply triggers all additional actions. Also disable
	 * this option if specified.
	 * 
	 * @param game
	 *            the game
	 */
	public void choose(Game game) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Choosing {0}", this);
		disableAction.triggerAction(game);
		// Trigger additional actions
		for (AbstractAction abstractAction : additionalActions) {
			abstractAction.triggerAction(game);
		}
	}

	@Override
	public String toString() {
		return "ConversationOption{id=" + id + ", text=" + text + ", answer=" + answer + ", event=" + event
				+ ", additionalActionsIDs=" + NamedObject.getIDList(additionalActions) + ", enabled=" + enabled
				+ ", targetID=" + target.getId() + ", disableActionID=" + disableAction.getId() + "}";
	}

}
