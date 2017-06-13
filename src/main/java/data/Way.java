package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

import data.action.AbstractAction;
import data.action.MoveAction;
import data.interfaces.PassivelyUsable;
import data.interfaces.Travelable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A one-way connection between two locations.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class Way extends InspectableObject implements Travelable, PassivelyUsable {

	/**
	 * All additional move actions.
	 */
	private List<AbstractAction> additionalMoveActions;

	/**
	 * All additional move commands.
	 */
	private List<String> additionalMoveCommands;

	/**
	 * The destination.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_WAY_DESTINATION"))
	@Access(AccessType.FIELD)
	private Location destination;
	
	/**
	 * The move action.
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 */
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(nullable = false)
	@Access(AccessType.FIELD)
	private MoveAction moveAction;

	/**
	 * A personalized error message displayed if moving this way was forbidden.
	 * The default message is used if this is {@code null}.
	 */
	private final StringProperty moveForbiddenText;

	/**
	 * A personalized error message displayed if moving this way was successful.
	 * The default message is used if this is {@code null}.
	 */
	private final StringProperty moveSuccessfulText;

	/**
	 * The origin.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_WAY_ORIGIN") )
	@Access(AccessType.FIELD)
	private Location origin;
	
	/**
	 * These values indicate that either the origin or destination are already deleted.
	 * In that case, their lists need not be modified as part of {@link Way#removeFromLocation()
	 */
	private boolean originDeleted;
	private boolean destinationDeleted;

	/**
	 * No-arg constructor for the database. Use
	 * {@link Way#Way(String, String, Location, Location)} instead.
	 */
	@Deprecated
	public Way() {
		init();
		moveForbiddenText = new SimpleStringProperty();
		moveSuccessfulText = new SimpleStringProperty();
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
	public Way(String name, String description, Location origin, Location destination) {
		super(name, description);
		init();
		moveForbiddenText = new SimpleStringProperty();
		moveSuccessfulText = new SimpleStringProperty();
		moveAction = new MoveAction("", destination);
		moveAction.setHidden(true);
		setOrigin(origin);
		setDestination(destination);
	}

	@Override
	public void addAdditionalMoveAction(AbstractAction action) {
		additionalMoveActions.add(action);
	}

	@Override
	public void addAdditionalMoveCommand(String command) {
		additionalMoveCommands.add(command);
	}

	@Override
	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable(name = "WAY_AMA",foreignKey = @ForeignKey(name = "FK_WAY_ADDITIONALMOVEACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (Way_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_WAY_ADDITIONALMOVEACTIONS_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalMoveActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	@OrderColumn
	public List<AbstractAction> getAdditionalMoveActions() {
		return additionalMoveActions;
	}

	@Override
	@ElementCollection
	public List<String> getAdditionalMoveCommands() {
		return additionalMoveCommands;
	}

	@Override
	@Transient
	public Location getDestination() {
		return destination;
	}

	@Override
	@Column(nullable = true)
	public String getMoveForbiddenText() {
		return moveForbiddenText.get();
	}

	@Override
	@Column(nullable = true)
	public String getMoveSuccessfulText() {
		return moveSuccessfulText.get();
	}

	/**
	 * @return the origin
	 */
	@Transient
	public Location getOrigin() {
		return origin;
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		this.additionalMoveActions = new ArrayList<>();
		this.additionalMoveCommands = new ArrayList<>();
	}

	@Override
	@Transient
	public boolean isMovingEnabled() {
		return moveAction.getEnabled();
	}
	
	/**
	 * @return the move forbidden text property
	 */
	public StringProperty moveForbiddenTextProperty() {
		return moveForbiddenText;
	}

	/**
	 * @return the move successful text property
	 */
	public StringProperty moveSuccessfulTextProperty() {
		return moveSuccessfulText;
	}
	
	@Override
	public void removeAdditionalMoveAction(AbstractAction action) {
		additionalMoveActions.remove(action);
	}

	@Override
	public void removeAdditionalMoveCommand(String command) {
		additionalMoveCommands.remove(command);
	}

	/**
	 * Called to remove a way from its origin and destination prior to deletion.
	 */
	@PreRemove
	private void removeFromLocation() {
		if (this.origin != null && !this.originDeleted) {
			this.origin.removeWayOut(this);
		}
		if (this.destination != null && !this.destinationDeleted) {
			this.destination.removeWayIn(this);
		}
	}

	@Override
	public void setAdditionalMoveActions(List<AbstractAction> additionalMoveActions) {
		this.additionalMoveActions = additionalMoveActions;
	}

	@Override
	public void setAdditionalMoveCommands(List<String> additionalMoveCommands) {
		this.additionalMoveCommands = additionalMoveCommands;
	}

	// final as called in constructor
	@Override
	public final void setDestination(Location destination) {
		// This also modifies the location and the moveAction.
		if (this.destination != null) {
			this.destination.removeWayIn(this);
		}
		destination.addWayIn(this);
		this.destination = destination;
		this.moveAction.setTarget(destination);
	}
	
	/**
	 * Marks the destination as deleted.
	 */
	void setDestinationDeleted() {
		this.destinationDeleted = true;
	}
	
	/**
	 * Marks the origin as deleted.
	 */
	void setOriginDeleted() {
		this.originDeleted = true;
	}

	@Override
	public void setMoveForbiddenText(String forbiddenText) {
		moveForbiddenText.set(forbiddenText);
	}

	@Override
	public void setMoveSuccessfulText(String successfulText) {
		moveSuccessfulText.set(successfulText);
	}
	
	@Override
	public void setMovingEnabled(boolean enabled) {
		moveAction.setEnabled(enabled);
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

	@Override
	public void travel(Game game) {
		// MoveAction is either enabled or not, no need to check here
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Travelling (if enabled) over {0}", this);

		moveAction.triggerAction(game);
		for (AbstractAction abstractAction : additionalMoveActions) {
			abstractAction.triggerAction(game);
		}
	}
}
