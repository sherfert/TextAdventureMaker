package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.action.AbstractAction;
import data.interfaces.HasConversation;
import data.interfaces.HasLocation;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A person.
 * 
 * @author Satia
 */
@Entity
public class Person extends InspectableObject implements HasLocation,
		HasConversation {

	/**
	 * The current location of the person. May be {@code null}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	/**
	 * The persons conversation. If {@code null}, or the conversation is
	 * disabled, you cannot talk to the person. A conversation can be used by
	 * more than one person.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Conversation conversation;
	
	/**
	 * The text displayed when trying to talk to, but this is disabled.
	 * There is no such successful text, as then the conversation is
	 * started immediately.
	 */
	private String talkingToForbiddenText;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link Person#Person(String, String)} or
	 *             {@link Person#Person(Location, String, String)} instead.
	 */
	@Deprecated
	public Person() {
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
	}

	@Override
	public Location getLocation() {
		return location;
	}

	// final as called in constructor
	@Override
	public final void setLocation(Location location) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Setting location of {0} to {1}",
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
		return "Person{" + "locationID=" + location.getId() + " "
				+ super.toString() + '}';
	}

	@Override
	public Conversation getConversation() {
		return conversation;
	}

	@Override
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	@Override
	public void talkTo() {
		if (isTalkingEnabled()) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Talking to {0}", this);
		}
		if (conversation != null) {
			// Trigger additional actions
			for (AbstractAction abstractAction : conversation
					.getAdditionalActions()) {
				abstractAction.triggerAction();
			}
		}
	}

	@Override
	public boolean isTalkingEnabled() {
		return conversation != null && conversation.isEnabled();
	}

	@Override
	public String getTalkingToForbiddenText() {
		return talkingToForbiddenText;
	}

	@Override
	public void setTalkingToForbiddenText(String text) {
		this.talkingToForbiddenText = text;
	}

}
