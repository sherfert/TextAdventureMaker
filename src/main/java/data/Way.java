package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.action.MoveAction;

@Entity
public class Way extends HasActions<MoveAction> {

	public Way() {
		primaryAction = new MoveAction(this);
	}

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	Location origin;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location destination;

	/**
	 * @return the origin
	 */
	public Location getOrigin() {
		return origin;
	}

	/**
	 * @return the destination
	 */
	public Location getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Location destination) {
		this.destination = destination;
	}
}