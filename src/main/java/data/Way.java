package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import data.action.AbstractAction;
import data.action.MoveAction;
import data.interfaces.Travelable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A one-way connection between two locations.
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
	 * All additional travel commands.
	 */
	@ElementCollection
	private List<String> additionalTravelCommands;

	/**
	 * The move action.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
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
		moveAction = new MoveAction(destination);
		setOrigin(origin);
		setDestination(destination);
	}

	@Override
	public void addAdditionalActionToMove(AbstractAction action) {
		additionalMoveActions.add(action);
	}
	
	@Override
	public void addAdditionalTravelCommand(String command) {
		additionalTravelCommands.add(command);
	}

	@Override
	public List<AbstractAction> getAdditionalMoveActions() {
		return additionalMoveActions;
	}
	
	@Override
	public List<String> getAdditionalTravelCommands() {
		return additionalTravelCommands;
	}

	@Override
	public Location getDestination() {
		return destination;
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
		return moveAction.getEnabled();
	}

	@Override
	public void removeAdditionalActionFromMove(AbstractAction action) {
		additionalMoveActions.remove(action);
	}
	
	@Override
	public void removeAdditionalTravelCommand(String command) {
		additionalTravelCommands.remove(command);
	}

	@Override
	public void setMoveForbiddenText(String forbiddenText) {
		moveForbiddenText = forbiddenText;
	}

	@Override
	public void setMoveSuccessfulText(String successfulText) {
		moveSuccessfulText = successfulText;
	}

	@Override
	public void setMovingEnabled(boolean enabled) {
		moveAction.setEnabled(enabled);
	}

	@Override
	public void travel(Game game) {
		// MoveAction is either enabled or not, no need to check here
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Travelling (if enabled) over {0}", this);

		moveAction.triggerAction(game);
		for (AbstractAction abstractAction : additionalMoveActions) {
			abstractAction.triggerAction(game);
		}
	}

	// final as called in constructor
	/**
	 * This also modifies the location and the moveAction.
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public final void setDestination(Location destination) {
		if (this.destination != null) {
			this.destination.removeWayIn(this);
		}
		destination.addWayIn(this);
		this.destination = destination;
		this.moveAction.setTarget(destination);
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
		origin.addWayOut(this);
		this.origin = origin;
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		this.additionalMoveActions = new ArrayList<>();
		this.additionalTravelCommands = new ArrayList<>();
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
