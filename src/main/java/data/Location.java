package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

import data.interfaces.HasLocation;
import data.interfaces.Inspectable;

/**
 * Any location in the game. The player can move from one location to another. A
 * location is not Inspectable, but with a lookaround command, the entered text
 * is displayed.
 *
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class Location extends NamedDescribedObject {

	/**
	 * The items located here.
	 */
	private List<Item> items;

	/**
	 * The persons located here.
	 */
	private List<Person> persons;

	/**
	 * The ways leading inside this location. They get deleted when the location
	 * is deleted.
	 */
	private List<Way> waysIn;

	/**
	 * The ways leading outside this location. They get deleted when the
	 * location is deleted.
	 */
	private List<Way> waysOut;

	/**
	 * The x coordinate when displaying this location in the map view. Not
	 * relevant for the game.
	 */
	private double xCoordinate;

	/**
	 * The x coordinate when displaying this location in the map view. Not
	 * relevant for the game.
	 */
	private double yCoordinate;

	/**
	 * No-arg constructor for the database.
	 *
	 * @deprecated Use {@link Location#Location(String, String)} instead.
	 */
	@Deprecated
	public Location() {
		init();
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Location(String name, String description) {
		super(name, description);
		init();
	}

	/**
	 * Adds an item to this location. Package visibility, so that from outside
	 * the Item methods are used instead.
	 *
	 * @param item
	 *            the item
	 */
	void addItem(Item item) {
		if (!this.items.contains(item)) {
			this.items.add(item);
		}
	}

	/**
	 * Adds a person to this location. Package visibility, so that from outside
	 * the Person methods are used instead.
	 *
	 * @param person
	 *            the person
	 */
	void addPerson(Person person) {
		if (!this.persons.contains(person)) {
			this.persons.add(person);
		}
	}

	/**
	 * Adds a way in. Package visibility, so that from outside the Way methods
	 * are used instead.
	 *
	 * @param wayIn
	 *            the way
	 */
	void addWayIn(Way wayIn) {
		if (!this.waysIn.contains(wayIn)) {
			this.waysIn.add(wayIn);
		}
	}

	/**
	 * Adds a way out. Package visibility, so that from outside the Way methods
	 * are used instead.
	 *
	 * @param wayOut
	 *            the way
	 */
	void addWayOut(Way wayOut) {
		if (!this.waysOut.contains(wayOut)) {
			this.waysOut.add(wayOut);
		}
	}

	/**
	 * This String is being displayed when the player enters the location
	 *
	 * @return the short description plus the short descriptions of everything
	 *         here.
	 */
	@Transient
	public String getEnteredText() {
		StringBuilder sb = new StringBuilder(getDescription());
		// Persons
		for (Person person : persons) {
			String d = person.getDescription();
			if (d != null && !d.isEmpty()) {
				sb.append(' ').append(d);
			}
		}
		// Items
		for (Item item : items) {
			String d = item.getDescription();
			if (d != null && !d.isEmpty()) {
				sb.append(' ').append(d);
			}
		}
		// Ways out
		for (Way way : waysOut) {
			String d = way.getDescription();
			if (d != null && !d.isEmpty()) {
				sb.append(' ').append(d);
			}
		}
		return sb.toString();
	}

	/**
	 * @return a list containing the {@link HasLocation}s: Persons and Items.
	 *         This does not include the Player.
	 */
	@Transient
	public List<HasLocation> getHasLocations() {
		List<HasLocation> result = new ArrayList<>(persons.size() + items.size());
		// The persons
		result.addAll(persons);
		// The items
		result.addAll(items);
		return result;
	}

	/**
	 * @return a list containing everything that can be inspected in this
	 *         location.
	 */
	@Transient
	public List<Inspectable> getInspectables() {
		List<Inspectable> result = new ArrayList<>(persons.size() + items.size() + waysOut.size());
		// The persons
		result.addAll(persons);
		// The items
		result.addAll(items);
		// The ways leading out
		result.addAll(waysOut);
		return result;
	}

	/**
	 * @return the items
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	@OrderColumn
	public List<Item> getItems() {
		return items;
	}

	/**
	 * @return the xCoordinate
	 */
	@Column(nullable = false)
	public double getxCoordinate() {
		return xCoordinate;
	}

	/**
	 * @param xCoordinate
	 *            the xCoordinate to set
	 */
	public void setxCoordinate(double xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	/**
	 * @return the yCoordinate
	 */
	@Column(nullable = false)
	public double getyCoordinate() {
		return yCoordinate;
	}

	/**
	 * @param yCoordinate
	 *            the yCoordinate to set
	 */
	public void setyCoordinate(double yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	/**
	 * Updates the list of items of this location. This method must only be
	 * called if the elements of the passed list and the current list are the
	 * same, except for the order.
	 * 
	 * @param newItems
	 *            the permutated list of items
	 */
	public void updateItems(List<Item> newItems) {
		items.clear();
		items.addAll(newItems);
	}

	/**
	 * @return the persons
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	@OrderColumn
	public List<Person> getPersons() {
		return persons;
	}

	/**
	 * Updates the list of persons of this location. This method must only be
	 * called if the elements of the passed list and the current list are the
	 * same, except for the order.
	 * 
	 * @param newPersons
	 *            the permutated list of persons
	 */
	public void updatePersons(List<Person> newPersons) {
		persons.clear();
		persons.addAll(newPersons);
	}

	/**
	 * @return the waysOut
	 */
	@OneToMany(mappedBy = "origin", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderColumn
	public List<Way> getWaysOut() {
		return waysOut;
	}

	/**
	 * Updates the list of waysOut of this location. This method must only be
	 * called if the elements of the passed list and the current list are the
	 * same, except for the order.
	 * 
	 * @param newWays
	 *            the permutated list of ways
	 */
	public void updateWaysOut(List<Way> newWays) {
		waysOut.clear();
		waysOut.addAll(newWays);
	}

	/**
	 * @return the waysIn
	 */
	@OneToMany(mappedBy = "destination", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@OrderColumn
	public List<Way> getWaysIn() {
		return waysIn;
	}

	/**
	 * Updates the list of waysIn of this location. This method must only be
	 * called if the elements of the passed list and the current list are the
	 * same, except for the order.
	 * 
	 * @param newWays
	 *            the permutated list of ways
	 */
	public void updateWaysIn(List<Way> newWays) {
		waysIn.clear();
		waysIn.addAll(newWays);
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setItems(List<Item> items) {
		this.items = items;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setWaysIn(List<Way> waysIn) {
		this.waysIn = waysIn;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setWaysOut(List<Way> waysOut) {
		this.waysOut = waysOut;
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		waysOut = new ArrayList<>();
		waysIn = new ArrayList<>();
		items = new ArrayList<>();
		persons = new ArrayList<>();
	}

	/**
	 * Removes an item from this location. Package visibility, so that from
	 * outside the Item methods are used instead.
	 *
	 * @param item
	 *            the item
	 */
	void removeItem(Item item) {
		this.items.remove(item);
	}

	/**
	 * Removes a person from this location. Package visibility, so that from
	 * outside the Person methods are used instead.
	 *
	 * @param person
	 *            the person
	 */
	void removePerson(Person person) {
		this.persons.remove(person);
	}

	/**
	 * Removes a way in. Package visibility, so that from outside the Way
	 * methods are used instead.
	 *
	 * @param wayIn
	 *            the way
	 */
	void removeWayIn(Way wayIn) {
		this.waysIn.remove(wayIn);
	}

	/**
	 * Removes a way out. Package visibility, so that from outside the Way
	 * methods are used instead.
	 *
	 * @param wayOut
	 *            the way
	 */
	void removeWayOut(Way wayOut) {
		this.waysOut.remove(wayOut);
	}

	/**
	 * Called to set the location of all items/persons to null.
	 * Marks location as deleted in connected ways.
	 */
	@PreRemove
	private void preRemove() {
		for (int i = persons.size() - 1; i >= 0; i--) {
			persons.get(i).setLocation(null);
		}
		for (int i = items.size() - 1; i >= 0; i--) {
			items.get(i).setLocation(null);
		}
		for(Way w : waysIn) {
			w.setDestinationDeleted();
		}
		for(Way w : waysOut) {
			w.setOriginDeleted();
		}
	}

}
