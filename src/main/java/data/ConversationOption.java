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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import data.action.AbstractAction;
import data.action.AbstractAction.Enabling;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import data.action.ChangeConversationOptionAction;

/**
 * An option in a conversation. The player can choose between options to say in
 * a certain {@link ConversationLayer}
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class ConversationOption extends NamedObject {
	
	/**
	 * All additional actions.
	 */
	private List<AbstractAction> additionalActions;
	
	/**
	 * The answer the player gets when choosing that option.
	 */
	private final StringProperty answer;
	
	/**
	 * The {@link ChangeConversationOptionAction} which would disable this
	 * option.
	 * 
	 * Note: This is NOT the Inverse connection of
	 * {@link ChangeConversationOptionAction#option}.
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 * 
	 * XXX This should be nullable = false, which is not possible.
	 */
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn
	@Access(AccessType.FIELD)
	private ChangeConversationOptionAction disableAction;

	/**
	 * Only enabled options from a layer are listed.
	 */
	private final BooleanProperty enabled;

	/**
	 * A text describing what is going on additionally. If empty, nothing is
	 * printed.
	 */
	private final StringProperty event;

	/**
	 * The layer this option belongs to. This is fixed and cannot be
	 * changed. 
	 */
	private ConversationLayer layer;

	/**
	 * This defines the order in which options appear in a layer. It is managed
	 * by the methods of the layer automatically and should not be set
	 * anywhere else. The getters and setters have package visibility, so that it cannot be accessed from the outside.
	 */
	private int layerOrder;

	/**
	 * The target layer, which the player gets to after choosing that option. If
	 * this is {@code null}, the option will end the conversation. By default,
	 * this should be the owning layer.
	 * 
	 * This should only point to layers of the same conversation.
	 */
	private ConversationLayer target;

	/**
	 * The text the player says when choosing that option.
	 */
	private final StringProperty text;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ConversationOption(String, String, String, ConversationLayer)}
	 *             instead.
	 */
	@Deprecated
	public ConversationOption() {
		this.text = new SimpleStringProperty();
		this.answer = new SimpleStringProperty();
		this.event = new SimpleStringProperty();
		this.enabled = new SimpleBooleanProperty();
		init();
	}

	/**
	 * Create an enabled ConversationOption, with given text, answer and target.
	 * No event text. By default, the option will not disappear after being
	 * chosen once.
	 * 
	 * @param name
	 *            the name
	 * @param text
	 *            the text
	 * @param answer
	 *            the answer
	 * @param target
	 *            the target
	 */
	public ConversationOption(String name, String text, String answer, ConversationLayer target) {
		super(name);
		this.text = new SimpleStringProperty(text);
		this.answer = new SimpleStringProperty(answer);
		this.event = new SimpleStringProperty("");
		this.enabled = new SimpleBooleanProperty(true);
		this.target = target;
		init();
	}

	/**
	 * Create an enabled ConversationOption, with given text, answer, event and
	 * target. By default, the option will not disappear after being chosen
	 * once.
	 * 
	 * @param name
	 *            the name
	 * @param text
	 *            the text
	 * @param answer
	 *            the answer
	 * @param event
	 *            the event
	 * @param target
	 *            the target
	 */
	public ConversationOption(String name, String text, String answer, String event, ConversationLayer target) {
		super(name);
		this.text = new SimpleStringProperty(text);
		this.answer = new SimpleStringProperty(answer);
		this.event = new SimpleStringProperty(event);
		this.enabled = new SimpleBooleanProperty(true);
		this.target = target;
		init();
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
	 * @return the answer property
	 */
	public StringProperty answerProperty() {
		return answer;
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

	/**
	 * @return the enabled property
	 */
	public BooleanProperty enabledProperty() {
		return enabled;
	}

	/**
	 * @return the event property
	 */
	public StringProperty eventProperty() {
		return event;
	}

	/**
	 * @return the additionalActions
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "CONVOP_AA", foreignKey = @ForeignKey(name = "FK_CONVERSATIONOPTION_ADDITIONALACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (ConversationOption_ID) REFERENCES CONVERSATIONOPTION (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_CONVERSATIONOPTION_ADDITIONALACTIONS_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	public List<AbstractAction> getAdditionalActions() {
		return additionalActions;
	}
	
	/**
	 * @return the answer
	 */
	@Column(nullable = false)
	public String getAnswer() {
		return answer.get();
	}

	/**
	 * @return the enabled
	 */
	@Column(nullable = false)
	public boolean getEnabled() {
		return enabled.get();
	}

	/**
	 * @return the event
	 */
	@Column(nullable = false)
	public String getEvent() {
		return event.get();
	}
	
	/**
	 * @return the layer
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_OPTIONS_LAYER", //
	foreignKeyDefinition = "FOREIGN KEY (OPTIONS_ID) REFERENCES CONVERSATIONLAYER (ID) ON DELETE CASCADE") )
	public ConversationLayer getLayer() {
		return layer;
	}

	/**
	 * @return the layerOrder
	 */
	@Column(nullable = false)
	int getLayerOrder() {
		return layerOrder;
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
	 * @return the text
	 */
	@Column(nullable = false)
	public String getText() {
		return text.get();
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		this.additionalActions = new ArrayList<>();
		this.disableAction = new ChangeConversationOptionAction("", this);
		this.disableAction.setHidden(true);
		this.disableAction.setEnabled(false);
		this.disableAction.setEnabling(Enabling.DISABLE);
	}

	/**
	 * @return whether this action should be disabled (permanently) after being
	 *         chosen once.
	 */
	@Transient
	public boolean isDisablingOptionAfterChosen() {
		return disableAction.getEnabled();
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
	 * @param additionalActions
	 *            the additionalActions to set
	 */
	public void setAdditionalActions(List<AbstractAction> additionalActions) {
		this.additionalActions = additionalActions;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer.set(answer);
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
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event.set(event);
	}

	/**
	 * @param layer the layer to set
	 */
	void setLayer(ConversationLayer layer) {
		this.layer = layer;
	}

	/**
	 * @param layerOrder the layerOrder to set
	 */
	void setLayerOrder(int layerOrder) {
		this.layerOrder = layerOrder;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(ConversationLayer target) {
		this.target = target;
	}
	
	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text.set(text);
	}

	/**
	 * @return the text property
	 */
	public StringProperty textProperty() {
		return text;
	}

}
