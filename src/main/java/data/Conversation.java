package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import data.action.AbstractAction;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A conversation, consisting of different layers and a greeting. Can be enabled
 * and disabled. There are actions than can be triggered if the conversation is
 * started.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class Conversation extends NamedObject {

	/**
	 * The greeting from the Person.
	 */
	private final StringProperty greeting;

	/**
	 * A text describing what is going on additionally. If empty, nothing is
	 * printed.
	 */
	private final StringProperty event;

	/**
	 * If this conversation is enabled.
	 */
	private final BooleanProperty enabled;

	/**
	 * All additional actions.
	 */
	private List<AbstractAction> additionalActions;

	/**
	 * All layers belonging to this conversation. A layer can only belong to one
	 * conversation.
	 */
	private List<ConversationLayer> layers;

	/**
	 * The starting layer. The options of this layer will be presented after the
	 * greeting. If this is {@code null}, the conversation will end after the
	 * greeting.
	 */
	private ConversationLayer startLayer;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #Conversation(String, String)} instead.
	 */
	@Deprecated
	public Conversation() {
		this.event = new SimpleStringProperty();
		this.greeting = new SimpleStringProperty();
		this.enabled = new SimpleBooleanProperty();
		init();
	}

	/**
	 * An enabled conversation with no layers yet.
	 * 
	 * @param name
	 *            the name
	 * @param greeting
	 *            the greeting
	 */
	public Conversation(String name, String greeting) {
		super(name);
		this.event = new SimpleStringProperty("");
		this.greeting = new SimpleStringProperty(greeting);
		this.enabled = new SimpleBooleanProperty(true);
		init();
	}

	/**
	 * An enabled conversation with no layers yet.
	 * 
	 * @param name
	 *            the name
	 * @param greeting
	 *            the greeting
	 * @param event
	 *            the event
	 */
	public Conversation(String name, String greeting, String event) {
		super(name);
		this.event = new SimpleStringProperty(event);
		this.greeting = new SimpleStringProperty(greeting);
		this.enabled = new SimpleBooleanProperty(true);
		init();
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		this.layers = new ArrayList<>();
		this.additionalActions = new ArrayList<>();
	}

	/**
	 * @return the greeting
	 */
	@Column(nullable = false)
	public String getGreeting() {
		return greeting.get();
	}

	/**
	 * @return the greeting property
	 */
	public StringProperty greetingProperty() {
		return greeting;
	}

	/**
	 * @param greeting
	 *            the greeting to set
	 */
	public void setGreeting(String greeting) {
		this.greeting.set(greeting);
	}

	/**
	 * @return the event
	 */
	@Column(nullable = false)
	public String getEvent() {
		return event.get();
	}

	/**
	 * @return the event property
	 */
	public StringProperty eventProperty() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event.set(event);
	}

	/**
	 * If this conversation is enabled.
	 * 
	 * @return the enabled
	 */
	@Column(nullable = false)
	public boolean getEnabled() {
		return enabled.get();
	}

	/**
	 * @return the enabled property
	 */
	public BooleanProperty enabledProperty() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	/**
	 * @return the layers
	 */
	@OneToMany(mappedBy = "getConversation", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	public List<ConversationLayer> getLayers() {
		return layers;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setLayers(List<ConversationLayer> layers) {
		this.layers = layers;
	}

	/**
	 * @return the startLayer
	 */
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_CONVERSATION_STARTLAYER", //
	foreignKeyDefinition = "FOREIGN KEY (startLayer_ID) REFERENCES CONVERSATIONLAYER (ID) ON DELETE SET NULL") )
	public ConversationLayer getStartLayer() {
		return startLayer;
	}

	/**
	 * @param startLayer
	 *            the startLayer to set
	 */
	public void setStartLayer(ConversationLayer startLayer) {
		this.startLayer = startLayer;
	}

	/**
	 * @return the additionalActions
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "CONV_AA", foreignKey = @ForeignKey(name = "FK_CONVERSATION_ADDITIONALACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (Conversation_ID) REFERENCES CONVERSATION (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_CONVERSATION_ADDITIONALACTIONS_D", //
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
	 * Adds a layer.
	 * 
	 * @param layer
	 *            the layer to add
	 */
	public void addLayer(ConversationLayer layer) {
		layers.add(layer);
		// Set the conversation in the layer
		layer.setConversation(this);
	}

	/**
	 * Removes a layer. Can only be called by the layer when the layer is deleted.
	 * 
	 * @param layer
	 *            the layer to remove
	 */
	void removeLayer(ConversationLayer layer) {
		layers.remove(layer);
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

}
