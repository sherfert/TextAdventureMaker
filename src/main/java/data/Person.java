package data;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import data.action.AbstractAction;
import data.interfaces.HasConversation;
import data.interfaces.HasLocation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A person.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class Person extends InspectableObject implements HasLocation, HasConversation {

	/**
	 * The current location of the person. May be {@code null}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_PERSON_LOCATION", foreignKeyDefinition = //
	"FOREIGN KEY (LOCATION_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE SET NULL") )
	@Access(AccessType.FIELD)
	private Location location;

	/**
	 * The persons conversation. If {@code null}, or the conversation is
	 * disabled, you cannot talk to the person. A conversation can be used by
	 * more than one person.
	 */
	private Conversation conversation;

	/**
	 * The text displayed when trying to talk to, but this is disabled. There is
	 * no such successful text, as then the conversation is started immediately.
	 * Suggests the default text to be displayed if {@code null}.
	 */
	private final StringProperty talkingToForbiddenText;

	/**
	 * All additional talk to commands.
	 */
	private List<String> additionalTalkToCommands;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link Person#Person(String, String)} or
	 *             {@link Person#Person(Location, String, String)} instead.
	 */
	@Deprecated
	public Person() {
		talkingToForbiddenText = new SimpleStringProperty();
		init();
	}

	/**
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Person(Location location, String name, String description) {
		super(name, description);
		talkingToForbiddenText = new SimpleStringProperty();
		init();
		setLocation(location);
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Person(String name, String description) {
		super(name, description);
		talkingToForbiddenText = new SimpleStringProperty();
		init();
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.additionalTalkToCommands = new ArrayList<>();
	}

	@Override
	public void addAdditionalTalkToCommand(String command) {
		additionalTalkToCommands.add(command);
	}

	@Override
	@ElementCollection
	public List<String> getAdditionalTalkToCommands() {
		return additionalTalkToCommands;
	}

	@Override
	public void setAdditionalTalkToCommands(List<String> additionalTalkToCommands) {
		this.additionalTalkToCommands = additionalTalkToCommands;
	}

	@Override
	public void removeAdditionalTalkToCommand(String command) {
		additionalTalkToCommands.remove(command);
	}

	@Override
	@Transient
	public Location getLocation() {
		return location;
	}

	// final as called in constructor
	@Override
	public final void setLocation(Location location) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Setting location of {0} to {1}",
				new Object[] { this, location });

		if (this.location != null) {
			this.location.removePerson(this);
		}
		if (location != null) {
			location.addPerson(this);
		}
		this.location = location;
	}

	@Override
	public String toString() {
		return "Person{" + "locationID=" + location.getId() + " " + super.toString() + '}';
	}

	@Override
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_PERSON_CONVERSATION", foreignKeyDefinition = //
	"FOREIGN KEY (CONVERSATION_ID) REFERENCES CONVERSATION (ID) ON DELETE SET NULL") )
	public Conversation getConversation() {
		return conversation;
	}

	@Override
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	@Override
	public void talkTo(Game game) {
		if (isTalkingEnabled()) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Talking to {0}", this);
		}
		if (conversation != null) {
			// Trigger additional actions
			for (AbstractAction abstractAction : conversation.getAdditionalActions()) {
				abstractAction.triggerAction(game);
			}
		}
	}

	@Override
	@Transient
	public boolean isTalkingEnabled() {
		return conversation != null && conversation.getEnabled();
	}
	
	/**
	 * @return the talk to forbidden text property
	 */
	public StringProperty talkingToForbiddenTextProperty() {
		return talkingToForbiddenText;
	}

	@Override
	@Column(nullable = true)
	public String getTalkingToForbiddenText() {
		return talkingToForbiddenText.get();
	}

	@Override
	public void setTalkingToForbiddenText(String text) {
		talkingToForbiddenText.set(text);
	}

}
