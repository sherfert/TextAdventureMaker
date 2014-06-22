package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * A player. There should be only one player per database.
 * 
 * @author Satia
 */
@Entity
public class Player {
	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * The inventory.
	 */
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn
	private List<InventoryItem> inventory;

	/**
	 * The location. If {@code null}, the game supposedly has not started yet.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true)
	private Location location;

	/**
	 * Creates a player with no location yet. It will be transferred to the
	 * game's starting location when a new game is started.
	 */
	public Player() {
		inventory = new ArrayList<InventoryItem>();
	}

	/**
	 * @param location
	 *            the location
	 */
	public Player(Location location) {
		this();
		this.location = location;
	}

	/**
	 * Adds an item to the inventory.
	 * 
	 * @param item
	 *            the item
	 */
	public void addInventoryItem(InventoryItem item) {
		this.inventory.add(item);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the inventory
	 */
	public List<InventoryItem> getInventory() {
		return inventory;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Removes an item from the inventory.
	 * 
	 * @param item
	 *            the item
	 */
	public void removeInventoryItem(InventoryItem item) {
		this.inventory.remove(item);
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
}