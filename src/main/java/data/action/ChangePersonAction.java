package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Conversation;
import data.Game;
import data.Location;
import data.Person;

/**
 * An action changing properties of a {@link Person}.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
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
	private Conversation newConversation;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangePersonAction(String, Person)} instead.
	 */
	@Deprecated
	public ChangePersonAction() {
	}

	/**
	 * @param name
	 *            the name
	 * @param object
	 *            the object to be changed
	 */
	public ChangePersonAction(String name, Person object) {
		super(name, object);
	}

	@Override
	public Person getObject() {
		return (Person) super.getObject();
	}

	/**
	 * @return the newLocation
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_CHANGEPERSONACTION_NEWLOCATION", //
	foreignKeyDefinition = "FOREIGN KEY (NEWLOCATION_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE SET NULL") )
	public Location getNewLocation() {
		return newLocation;
	}

	/**
	 * Remember to also set changeLocation to true.
	 * 
	 * @param newLocation
	 *            the newLocation to set
	 */
	public void setNewLocation(Location newLocation) {
		this.newLocation = newLocation;
	}

	/**
	 * @return the changeLocation
	 */
	@Column(nullable = false)
	public boolean getChangeLocation() {
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
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_CHANGEPERSONACTION_NEWCONVERSATION", //
	foreignKeyDefinition = "FOREIGN KEY (NEWCONVERSATION_ID) REFERENCES CONVERSATION (ID) ON DELETE SET NULL") )
	public Conversation getNewConversation() {
		return newConversation;
	}

	/**
	 * Remember to also set changeConversation to true.
	 * 
	 * @param newLocation
	 *            the newLocation to set
	 */
	public void setNewConversation(Conversation newConversation) {
		this.newConversation = newConversation;
	}

	/**
	 * @return the changeConversation
	 */
	@Column(nullable = false)
	public boolean getChangeConversation() {
		return changeConversation;
	}

	/**
	 * @param changeConversation
	 *            the changeConversation to set
	 */
	public void setChangeConversation(boolean changeConversation) {
		this.changeConversation = changeConversation;
	}
	
	/**
	 * @return the newTalkingToForbiddenText
	 */
	@Column(nullable = true)
	public String getNewTalkingToForbiddenText() {
		return newTalkingToForbiddenText;
	}

	/**
	 * @param newTalkingToForbiddenText the newTalkingToForbiddenText to set
	 */
	public void setNewTalkingToForbiddenText(String newTalkingToForbiddenText) {
		this.newTalkingToForbiddenText = newTalkingToForbiddenText;
	}

	@Override
	protected void doAction(Game game) {
		// Call the super method
		super.doAction(game);

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
	public String actionDescription() {
		StringBuilder builder = new StringBuilder(super.actionDescription());
		if (changeConversation) {
			builder.append(" Setting conversation to '")
					.append(newConversation != null ? newConversation.getId() : "null").append("'.");
		}
		if (changeLocation) {
			builder.append(" Setting location to '").append(newLocation != null ? newLocation.getName() : "null")
					.append("'.");
		}
		if (newTalkingToForbiddenText != null) {
			builder.append(" Setting talking forbidden text to '").append(newTalkingToForbiddenText).append("'.");
		}
		return builder.toString();
	}

}
