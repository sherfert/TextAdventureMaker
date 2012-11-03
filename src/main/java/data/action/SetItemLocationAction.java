package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import persistence.Main;
import data.Item;
import data.Location;

/**
 * An action setting the location of an {@link Item}. It can also be used to
 * ADD items, if the former location was {@code null} or to REMOVE items, if the
 * new location is {@code null}.
 * 
 * @author Satia
 */
@Entity
public class SetItemLocationAction extends AbstractAction {
	/**
	 * The item to have the location changed.
	 */
	// TODO why not unnullable?
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn//(nullable = false)
	private Item item;

	/**
	 * The new location of the item. Can be {@code null}, which means the Item
	 * will be removed.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location newLocation;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link SetItemLocationAction#SetItemLocationAction(Item, Location)}
	 *             instead.
	 */
	@Deprecated
	public SetItemLocationAction() {
	}

	/**
	 * 
	 * @param item
	 *            the item to have the location changed
	 * @param location
	 *            the new location of the item. Can be {@code null}, which means
	 *            the Item will be removed.
	 */
	public SetItemLocationAction(Item item, Location location) {
		this.item = item;
		this.newLocation = location;
	}

	/**
	 * @return the newLocation
	 */
	public Location getNewLocation() {
		return newLocation;
	}

	/**
	 * @param newLocation
	 *            the newLocation to set
	 */
	public void setNewLocation(Location newLocation) {
		this.newLocation = newLocation;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			item.setLocation(newLocation);
		}
		Main.updateChanges();
	}
}