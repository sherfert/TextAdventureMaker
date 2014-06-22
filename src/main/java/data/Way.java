package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import data.action.AbstractAction;
import data.action.MoveAction;
import data.interfaces.Travelable;

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
	 */
	private String moveForbiddenText;

	/**
	 * A personalized error message displayed if moving this way was successful.
	 */
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
		// Add way to the locations. The fields are being set by this.
		origin.addWayOut(this);
		destination.addWayIn(this);
	}

	@Override
	public void addAdditionalActionToMove(AbstractAction action) {
		additionalMoveActions.add(action);
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromMove() {
		return additionalMoveActions;
	}

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
		if(moveAction.getWay() != this) {
			moveAction.setWay(this);
		}
	}

	@Override
	public void setMoveForbiddenText(String forbiddenText) {
		moveForbiddenText = forbiddenText;
		//moveAction.setForbiddenText(forbiddenText);
	}

	@Override
	public void setMoveSuccessfulText(String successfulText) {
		moveSuccessfulText = successfulText;
		//moveAction.setSuccessfulText(successfulText);
	}

	@Override
	public void setMovingEnabled(boolean enabled) {
		moveAction.setEnabled(enabled);
	}

	@Override
	public void travel() {
		moveAction.triggerAction();
		for (AbstractAction abstractAction : additionalMoveActions) {
			abstractAction.triggerAction();
		}
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Location destination) {
		this.destination = destination;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Location origin) {
		this.origin = origin;
	}

	/**
	 * Initializes the fields
	 */
	private void init() {
		moveAction = new MoveAction(this);
		additionalMoveActions = new ArrayList<AbstractAction>();
	}
}