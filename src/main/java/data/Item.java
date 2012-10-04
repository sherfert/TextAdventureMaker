package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.action.TakeAction;

/**
 * Any item in the game world. This items are stored in locations but cannot be
 * in your inventory.
 * 
 * @author Satia
 */
@Entity
public class Item extends HasActions<TakeAction> {

	/**
	 * The current location of the item.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link Item#Item(String, String)} instead.
	 */
	@Deprecated
	public Item() {
		this.primaryAction = new TakeAction(this);
	}

	/**
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(Location location, String name, String description) {
		super(name, description);
		setLocation(location);
		this.primaryAction = new TakeAction(this);
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(String name, String description) {
		super(name, description);
		this.primaryAction = new TakeAction(this);
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets a new location for this item. The item is removed from the old
	 * location's list and added to the new one.
	 * 
	 * @param location
	 *            the location to be set
	 */
	public void setLocation(Location location) {
		if (this.location != null) {
			this.location.removeItem(this);
		}
		if (location != null) {
			location.addItem(this);
		}
		this.location = location;
	}
}