package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
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
public class Way extends NamedObject implements Travelable {

	/**
	 * All additional move actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalMoveActions;

	/**
	 * The move action.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private MoveAction moveAction;

	/**
	 * All names for the target.
	 */
	@ElementCollection
	private List<String> targetNames;

	/**
	 * The destination.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	Location destination;
	
	/**
	 * The origin.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	Location origin;

	/**
	 * No-arg constructor for the database. Use
	 * {@link Way#Way(String, String, String, Location, Location)} instead.
	 */
	@Deprecated
	public Way() {
		moveAction = new MoveAction(this);
		additionalMoveActions = new ArrayList<AbstractAction>();
		targetNames = new ArrayList<String>();
	}

	/**
	 * @param name
	 *            the name
	 * @param shortDescription
	 *            the shortDescription
	 * @param longDescription
	 *            the longDescription
	 * @param origin
	 *            the origin
	 * @param destination
	 *            the destination
	 */
	public Way(String name, String shortDescription, String longDescription,
			Location origin, Location destination) {
		super(name, shortDescription, longDescription);
		moveAction = new MoveAction(this);
		additionalMoveActions = new ArrayList<AbstractAction>();
		targetNames = new ArrayList<String>();
		// Add way to the locations. The fields are being set by this.
		origin.addWayOut(this);
		destination.addWayIn(this);
	}

	@Override
	public void addAdditionalActionToMove(AbstractAction action) {
		additionalMoveActions.add(action);
	}

	@Override
	public void addTargetName(String name) {
		targetNames.add(name);
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromMove() {
		return additionalMoveActions;
	}

	/**
	 * @return the destination
	 */
	public Location getDestination() {
		return destination;
	}

	@Override
	public MoveAction getMoveAction() {
		return moveAction;
	}

	@Override
	public String getMoveForbiddenText() {
		return moveAction.getForbiddenText();
	}

	@Override
	public String getMoveSuccessfulText() {
		return moveAction.getSuccessfulText();
	}

	/**
	 * @return the origin
	 */
	public Location getOrigin() {
		return origin;
	}

	@Override
	public List<String> getTargetNames() {
		return targetNames;
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
	public void removeTargetName(String name) {
		targetNames.remove(name);
	}

	@Override
	public void setMoveForbiddenText(String forbiddenText) {
		moveAction.setForbiddenText(forbiddenText);
	}

	@Override
	public void setMoveSuccessfulText(String successfulText) {
		moveAction.setSuccessfulText(successfulText);
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
}