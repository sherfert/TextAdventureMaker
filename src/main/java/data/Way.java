package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import data.action.AbstractAction;
import data.action.MoveAction;
import data.interfaces.Travelable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A one-way connection between two locations.
 * 
 * TODO hide access to move action, adapt changeWayAction
 * Have MoveAction only know the destination, not the way, and
 * such these is not a OneToOne mapping any more and users can use
 * MoveActions without ways.
 * 
 * @author Satia
 */
@Entity
public class Way extends InspectableObject implements Travelable {

	/**
	 * All additional move actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalMoveActions;

	/**
	 * The move action.
	 */
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "way")
	private MoveAction moveAction;

	/**
	 * The destination.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private Location destination;

	/**
	 * The origin.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private Location origin;

	/**
	 * A personalized error message displayed if moving this way was forbidden.
	 * The default message is used if this is {@code null}.
	 */
	@Column(nullable = true)
	private String moveForbiddenText;

	/**
	 * A personalized error message displayed if moving this way was successful.
	 * The default message is used if this is {@code null}.
	 */
	@Column(nullable = true)
	private String moveSuccessfulText;

	/**
	 * No-arg constructor for the database. Use
	 * {@link Way#Way(String, String, String, Location, Location)} instead.
	 */
	@Deprecated
	public Way() {
		init();
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param origin
	 *            the origin
	 * @param destination
	 *            the destination
	 */
	public Way(String name, String description, Location origin,
			Location destination) {
		super(name, description);
		init();
		setOrigin(origin);
		setDestination(destination);
	}

	@Override
	public void addAdditionalActionToMove(AbstractAction action) {
		additionalMoveActions.add(action);
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromMove() {
		return additionalMoveActions;
	}

	@Override
	public Location getDestination() {
		return destination;
	}

	@Override
	public MoveAction getMoveAction() {
		return moveAction;
	}

	@Override
	public String getMoveForbiddenText() {
		return moveForbiddenText;
	}

	@Override
	public String getMoveSuccessfulText() {
		return moveSuccessfulText;
	}

	/**
	 * @return the origin
	 */
	public Location getOrigin() {
		return origin;
	}

	@Override
	public boolean isMovingEnabled() {
		return moveAction.isEnabled();
	}

	@Override
	public void removeAdditionalActionFromMove(AbstractAction action) {
		additionalMoveActions.remove(action);
	}

	@Override
	public void setMoveAction(MoveAction moveAction) {
		this.moveAction = moveAction;
		if (moveAction.getWay() != this) {
			moveAction.setWay(this);
		}
	}

	@Override
	public void setMoveForbiddenText(String forbiddenText) {
		moveForbiddenText = forbiddenText;
		// moveAction.setForbiddenText(forbiddenText);
	}

	@Override
	public void setMoveSuccessfulText(String successfulText) {
		moveSuccessfulText = successfulText;
		// moveAction.setSuccessfulText(successfulText);
	}

	@Override
	public void setMovingEnabled(boolean enabled) {
		moveAction.setEnabled(enabled);
	}

	@Override
	public void travel() {
		// MoveAction is either enabled or not, no need to check here
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Travelling (if enabled) over {0}", this);

		moveAction.triggerAction();
		for (AbstractAction abstractAction : additionalMoveActions) {
			abstractAction.triggerAction();
		}
	}

	// final as called in constructor
	/**
	 * This also modifies the location.
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public final void setDestination(Location destination) {
		if (this.destination != null) {
			this.destination.removeWayIn(this);
		}
		if (destination != null) {
			destination.addWayIn(this);
		}
		this.destination = destination;
	}

	// final as called in constructor
	/**
	 * This also modifies the location.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public final void setOrigin(Location origin) {
		if (this.origin != null) {
			this.origin.removeWayOut(this);
		}
		if (origin != null) {
			origin.addWayOut(this);
		}
		this.origin = origin;
	}

	/**
	 * Initializes the fields
	 */
	private void init() {
		moveAction = new MoveAction(this);
		additionalMoveActions = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "Way{" + "additionalMoveActionsIDs="
				+ NamedObject.getIDList(additionalMoveActions)
				+ ", moveActionID=" + moveAction.getId() + ", destinationID="
				+ destination.getId() + ", originID=" + origin.getId()
				+ ", moveForbiddenText=" + moveForbiddenText
				+ ", moveSuccessfulText=" + moveSuccessfulText + " "
				+ super.toString() + '}';
	}
}
