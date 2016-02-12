package data.action;

import javax.persistence.Entity;

import data.Game;
import data.Location;
import data.Way;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * An action changing properties of a {@link Way}.
 * 
 * @author Satia
 */
@Entity
public class ChangeWayAction extends ChangeInspectableObjectAction {

	/**
	 * The new moveForbiddenText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newMoveForbiddenText;

	/**
	 * The new moveSuccessfulText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newMoveSuccessfulText;

	/**
	 * The new origin. If {@code null}, the old will not be changed.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true)
	private Location newOrigin;
	/**
	 * The new destination. If {@code null}, the old will not be changed.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true)
	private Location newDestination;

	/**
	 * Enabling or disabling if the way is travelable.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enabling;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link ChangeWayAction#ChangeWayAction(Way)} instead.
	 */
	@Deprecated
	public ChangeWayAction() {
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeWayAction(Way object) {
		super(object);
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeWayAction(Way object, boolean enabled) {
		super(object, enabled);
		init();
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

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
	 * @param newMoveForbiddenText
	 *            the newMoveForbiddenText to set
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
	 * @param newMoveSuccessfulText
	 *            the newMoveSuccessfulText to set
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
	 * @param newOrigin
	 *            the newOrigin to set
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
	 * @param newDestination
	 *            the newDestination to set
	 */
	public void setNewDestination(Location newDestination) {
		this.newDestination = newDestination;
	}

	/**
	 * @return the enabling
	 */
	public Enabling getEnabling() {
		return enabling;
	}

	/**
	 * @param enabling
	 *            the enabling to set
	 */
	public void setEnabling(Enabling enabling) {
		this.enabling = enabling;
	}

	@Override
	protected void doAction(Game game) {
		// Call the super method
		super.doAction(game);

		// Change fields
		if (enabling == Enabling.ENABLE) {
			getObject().setMovingEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			getObject().setMovingEnabled(false);
		}
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
				+ ", enabling=" + enabling + ", newDestinationID="
				+ newDestination.getId() + " " + super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder(super.getActionDescription());
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" moving.");
		}
		if (newOrigin != null) {
			builder.append(" Setting origin to '").append(newOrigin.getName())
					.append("'.");
		}
		if (newDestination != null) {
			builder.append(" Setting destination to '")
					.append(newDestination.getName()).append("'.");
		}

		if (newMoveSuccessfulText != null) {
			builder.append(" Setting move successful text to '")
					.append(newMoveSuccessfulText).append("'.");
		}
		if (newMoveForbiddenText != null) {
			builder.append(" Setting move forbidden text to '")
					.append(newMoveSuccessfulText).append("'.");
		}
		return builder.toString();
	}

}
