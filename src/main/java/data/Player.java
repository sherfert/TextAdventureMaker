package data;

import data.interfaces.HasId;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * A player. There is exactly one player per game.
 *
 * @author Satia
 */
@Entity
public class Player implements HasId {

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * The inventory.
	 * 
	 * No ON CASCADE definitions, since the player cannot be deleted.
	 */
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn
	private final List<InventoryItem> inventory;

	/**
	 * The location. If {@code null}, the game supposedly has not started yet.
	 * 
	 * No ON CASCADE definitions, since when deleting locations, the game hasn't
	 * started and this will be {@code null}.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true)
	private Location location;

	/**
	 * Creates a player with no location yet. It will be transferred to the
	 * game's starting location if a new game is started.
	 */
	public Player() {
		inventory = new ArrayList<>();
	}

	/**
	 * Adds an item to the inventory.
	 *
	 * @param item
	 *            the item
	 */
	public void addInventoryItem(InventoryItem item) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Adding inventory item {0}", item);

		this.inventory.add(item);
	}

	@Override
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
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Removing inventory item {0}", item);
		this.inventory.remove(item);
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Moving player to {0}", location);
		this.location = location;
	}

	/**
	 * @return a string representation of this object
	 */
	@Override
	public String toString() {
		return "Player{" + "id=" + id + ", inventoryIDs=" + NamedObject.getIDList(inventory) + ", locationID="
				+ location.getId() + " " + super.toString() + '}';
	}

}
