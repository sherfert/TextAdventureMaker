package data.action;

import javax.persistence.Entity;

import data.Location;
import data.Way;

/**
 * An action changing properties of a {@link Way}.
 *
 * @author Satia
 */
@Entity
public class ChangeWayAction extends ChangeInspectableObjectAction {

	/**
	 * The new moveForbiddenText. If {@code null}, the old will not be
	 * changed.
	 */
	private String newMoveForbiddenText;

	/**
	 * The new moveSuccessfulText. If {@code null}, the old will not be
	 * changed.
	 */
	private String newMoveSuccessfulText;

	/**
	 * The new origin. If {@code null}, the old will not be changed.
	 */
	private Location newOrigin;
	/**
	 * The new destination. If {@code null}, the old will not be changed.
	 */
	private Location newDestination;

	/**
	 * No-arg constructor for the database.
	 *
	 * @deprecated Use {@link ChangeWayAction#ChangeWayAction(Way)} instead.
	 */
	@Deprecated
	public ChangeWayAction() {
	}

	/**
	 * @param object the object to be changed
	 */
	public ChangeWayAction(Way object) {
		super(object);
	}

	/**
	 * @param object the object to be changed
	 * @param enabled if the action should be enabled
	 */
	public ChangeWayAction(Way object, boolean enabled) {
		super(object, enabled);
	}

	/**
	 * @return the object
	 */
	@Override
	public Way getObject() {
		return (Way) super.getObject();
	}

	/**
	 * @return the newMoveForbiddenText
	 */
	public String getNewMoveForbiddenText() {
		return newMoveForbiddenText;
	}

	/**
	 * @param newMoveForbiddenText the newMoveForbiddenText to set
	 */
	public void setNewMoveForbiddenText(String newMoveForbiddenText) {
		this.newMoveForbiddenText = newMoveForbiddenText;
	}

	/**
	 * @return the newMoveSuccessfulText
	 */
	public String getNewMoveSuccessfulText() {
		return newMoveSuccessfulText;
	}

	/**
	 * @param newMoveSuccessfulText the newMoveSuccessfulText to set
	 */
	public void setNewMoveSuccessfulText(String newMoveSuccessfulText) {
		this.newMoveSuccessfulText = newMoveSuccessfulText;
	}

	/**
	 * @return the newOrigin
	 */
	public Location getNewOrigin() {
		return newOrigin;
	}

	/**
	 * @param newOrigin the newOrigin to set
	 */
	public void setNewOrigin(Location newOrigin) {
		this.newOrigin = newOrigin;
	}

	/**
	 * @return the newDestination
	 */
	public Location getNewDestination() {
		return newDestination;
	}

	/**
	 * @param newDestination the newDestination to set
	 */
	public void setNewDestination(Location newDestination) {
		this.newDestination = newDestination;
	}

	@Override
	public void doAction() {
		// Call the super method
		super.doAction();
		
		// Change fields
			if (newMoveForbiddenText != null) {
				getObject().setMoveForbiddenText(newMoveForbiddenText);
			}
			if (newMoveSuccessfulText != null) {
				getObject().setMoveSuccessfulText(newMoveSuccessfulText);
			}
			if (newOrigin != null) {
				getObject().setOrigin(newOrigin);
			}
			if (newDestination != null) {
				getObject().setDestination(newDestination);
			}
	}

	@Override
	public String toString() {
		return "ChangeWayAction{" + "newMoveForbiddenText="
			+ newMoveForbiddenText + ", newMoveSuccessfulText="
			+ newMoveSuccessfulText + ", newOriginID=" + newOrigin.getId()
			+ ", newDestinationID=" + newDestination.getId() + " " + super.toString() + '}';
	}

}
