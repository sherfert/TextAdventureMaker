package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.action.MoveAction;

/**
 * A one-way connection between two locations.
 * 
 * @author Satia
 */
@Entity
public class Way extends HasActions<MoveAction> {

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
	 * {@link Way#Way(Location, Location)} instead.
	 */
	@Deprecated
	public Way() {
		primaryAction = new MoveAction(this);
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
		primaryAction = new MoveAction(this);
		// Add way to the locations. The fields are being set by this.
		origin.addWayOut(this);
		destination.addWayIn(this);
	}

	/**
	 * @return the destination
	 */
	public Location getDestination() {
		return destination;
	}

	/**
	 * @return the origin
	 */
	public Location getOrigin() {
		return origin;
	}
}