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

@Entity
public class Player {

	public Player() {
		inventory = new ArrayList<InventoryItem>();
	}

	@Id
	@GeneratedValue
	private int id;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn
	private List<InventoryItem> inventory;

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public void addInventoryItem(InventoryItem item) {
		this.inventory.add(item);
	}
	
	public void removeInventoryItem(InventoryItem item) {
		this.inventory.remove(item);
	}

	/**
	 * @return the inventory
	 */
	public List<InventoryItem> getInventory() {
		return inventory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Player [location=" + location + "]";
	}
}