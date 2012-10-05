package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import data.interfaces.Inspectable;

/**
 * Any location in the game. The player can move from one location to another.
 * 
 * @author Satia
 */
@Entity
public class Location extends NamedObject {

	/**
	 * The items located here.
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	private List<Item> items;

	/**
	 * The ways leading inside this location. They get deleted when the location
	 * is deleted.
	 */
	@OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
	private List<Way> waysIn;

	/**
	 * The ways leading outside this location. They get deleted when the
	 * location is deleted.
	 */
	@OneToMany(mappedBy = "origin", cascade = CascadeType.ALL)
	private List<Way> waysOut;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link Location#Location(String, String, String)}
	 *             instead.
	 */
	@Deprecated
	public Location() {
		waysOut = new ArrayList<Way>();
		waysIn = new ArrayList<Way>();
		items = new ArrayList<Item>();
	}

	/**
	 * @param name
	 *            the name
	 * @param shortDescription
	 *            the shortDescription
	 * @param longDescription
	 *            the longDescription
	 */
	public Location(String name, String shortDescription, String longDescription) {
		super(name, shortDescription, longDescription);
		waysOut = new ArrayList<Way>();
		waysIn = new ArrayList<Way>();
		items = new ArrayList<Item>();
	}

	/**
	 * Adds an item to this location. The item's location and its old location's
	 * list are modified, too.
	 * 
	 * @param item
	 *            the item
	 */
	public void addItem(Item item) {
		if (!this.items.contains(item)) {
			this.items.add(item);
			item.setLocation(this);
		}
	}

	/**
	 * Adds a way in.
	 * 
	 * @param wayIn
	 *            the way
	 */
	public void addWayIn(Way wayIn) {
		this.waysIn.add(wayIn);
		wayIn.destination = this;
	}

	/**
	 * Adds a way out.
	 * 
	 * @param wayOut
	 *            the way
	 */
	public void addWayOut(Way wayOut) {
		this.waysOut.add(wayOut);
		wayOut.origin = this;
	}

	/**
	 * This String is being displayed when the player enters the location
	 * 
	 * @return the short description plus the short descriptions of everything
	 *         here.
	 */
	public String getEnteredText() {
		StringBuilder sb = new StringBuilder(getShortDescription());
		// Items
		for (Item item : items) {
			sb.append(' ').append(item.getShortDescription());
		}
		// Ways out
		for (Way way : waysOut) {
			sb.append(' ').append(way.getShortDescription());
		}
		return sb.toString();
	}

	/**
	 * @return a list containing everything that can be inspected in this
	 *         location.
	 */
	public List<Inspectable> getInspectables() {
		List<Inspectable> result = new ArrayList<Inspectable>(1 + items.size()
				+ waysOut.size());
		// The location itself
		result.add(this);
		// The items
		result.addAll(items);
		// The ways leading out
		result.addAll(waysOut);
		return result;
	}

	/**
	 * @return the items
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * @return the waysOut
	 */
	public List<Way> getWaysOut() {
		return waysOut;
	}

	/**
	 * Removes an item from this location. The item's location is modified, too.
	 * 
	 * @param item
	 *            the item
	 */
	public void removeItem(Item item) {
		if (this.items.contains(item)) {
			this.items.remove(item);
			item.setLocation(null);
		}
	}

	/**
	 * Removes a way in.
	 * 
	 * @param wayIn
	 *            the way
	 */
	public void removeWayIn(Way wayIn) {
		this.waysIn.remove(wayIn);
		wayIn.destination = null;
	}

	/**
	 * Removes a way out.
	 * 
	 * @param wayOut
	 *            the way
	 */
	public void removeWayOut(Way wayOut) {
		this.waysOut.remove(wayOut);
		wayOut.origin = null;
	}
}