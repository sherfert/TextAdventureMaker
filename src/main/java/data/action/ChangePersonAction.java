package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Conversation;
import data.Location;
import data.Person;

/**
 * An action changing properties of a {@link Person}.
 * 
 * @author Satia
 */
@Entity
public class ChangePersonAction extends ChangeInspectableObjectAction {

	/**
	 * The new talkingToForbidden text. If {@code null}, the old will not be
	 * changed.
	 */
	private String newTalkingToForbiddenText;

	/**
	 * Only if this is {@code true}, the location will be changed. This is
	 * necessary, as {@code null} is a valid location and cannot be used to
	 * identify no changes to be applied.
	 */
	private boolean changeLocation;

	/**
	 * The new location of the person. Can be {@code null}, which means the
	 * Person will be removed. To apply these changes, {@link #changeLocation}
	 * has to be set to {@code true}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location newLocation;

	/**
	 * Only if this is {@code true}, the conversation will be changed. This is
	 * necessary, as {@code null} is a valid conversation and cannot be used to
	 * identify no changes to be applied.
	 */
	private boolean changeConversation;

	/**
	 * The new conversation of the item. Can be {@code null}, which means the
	 * Conversation will be removed. To apply these changes,
	 * {@link #changeConversation} has to be set to {@code true}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Conversation newConversation;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangePersonAction(Person)} instead.
	 */
	@Deprecated
	public ChangePersonAction() {
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangePersonAction(Person object) {
		super(object);
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangePersonAction(Person object, boolean enabled) {
		super(object, enabled);
	}

	@Override
	public Person getObject() {
		return (Person) super.getObject();
	}

	/**
	 * @return the newLocation
	 */
	public Location getNewLocation() {
		return newLocation;
	}

	/**
	 * This also sets changeLocation to true. If you want to undo this,
	 * explicitly call {@code setChangeLocation(false)}.
	 * 
	 * @param newLocation
	 *            the newLocation to set
	 */
	public void setNewLocation(Location newLocation) {
		this.newLocation = newLocation;
		this.changeLocation = true;
	}

	/**
	 * @return the changeLocation
	 */
	public boolean isChangeLocation() {
		return changeLocation;
	}

	/**
	 * @param changeLocation
	 *            the changeLocation to set
	 */
	public void setChangeLocation(boolean changeLocation) {
		this.changeLocation = changeLocation;
	}

	/**
	 * @return the newConversation
	 */
	public Conversation getNewConversation() {
		return newConversation;
	}

	/**
	 * This also sets changeConversation to true. If you want to undo this,
	 * explicitly call {@code setChangeConversation(false)}.
	 * 
	 * @param newLocation
	 *            the newLocation to set
	 */
	public void setNewConversation(Conversation newConversation) {
		this.newConversation = newConversation;
		this.changeConversation = true;
	}

	/**
	 * @return the changeConversation
	 */
	public boolean isChangeConversation() {
		return changeConversation;
	}

	/**
	 * @param changeConversation
	 *            the changeConversation to set
	 */
	public void setChangeConversation(boolean changeConversation) {
		this.changeConversation = changeConversation;
	}

	@Override
	protected void doAction() {
		// Call the super method
		super.doAction();

		if (changeLocation) {
			getObject().setLocation(newLocation);
		}
		if (changeConversation) {
			getObject().setConversation(newConversation);
		}

		if (newTalkingToForbiddenText != null) {
			getObject().setTalkingToForbiddenText(newTalkingToForbiddenText);
		}
	}

	@Override
	public String toString() {
		return "ChangePersonAction{newTalkingToForbiddenText="
				+ newTalkingToForbiddenText + ", changeLocation="
				+ changeLocation + ", newLocationID="
				+ (newLocation != null ? newLocation.getId() : "null")
				+ ", changeConversation=" + changeConversation
				+ ", newConversationID="
				+ (newConversation != null ? newConversation.getId() : "null")
				+ " " + super.toString() + "}";
	}

}
