package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import data.interfaces.HasLocation;
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
	 * The persons located here.
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	private List<Person> persons;

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
	 * Adds a person to this location. The person's location and its old
	 * location's list are modified, too.
	 * 
	 * @param person
	 *            the person
	 */
	public void addPerson(Person person) {
		if (!this.persons.contains(person)) {
			this.persons.add(person);
			person.setLocation(this);
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
		StringBuilder sb = new StringBuilder(getDescription());
		// Persons
		for (Person person : persons) {
			sb.append(' ').append(person.getDescription());
		}
		// Items
		for (Item item : items) {
			sb.append(' ').append(item.getDescription());
		}
		// Ways out
		for (Way way : waysOut) {
			sb.append(' ').append(way.getDescription());
		}
		return sb.toString();
	}

	/**
	 * @return a list containing the {@link HasLocation}s: Persons and Items.
	 */
	public List<HasLocation> getHasLocations() {
		List<HasLocation> result = new ArrayList<HasLocation>(persons.size()
				+ items.size());
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
	public List<Inspectable> getInspectables() {
		List<Inspectable> result = new ArrayList<Inspectable>(1
				+ persons.size() + items.size() + waysOut.size());
		// The location itself
		result.add(this);
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
	public List<Item> getItems() {
		return items;
	}

	/**
	 * @return the persons
	 */
	public List<Person> getPersons() {
		return persons;
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
	 * Removes a person from this location. The person's location is modified,
	 * too.
	 * 
	 * @param person
	 *            the person
	 */
	public void removePerson(Person person) {
		if (this.persons.contains(person)) {
			this.persons.remove(person);
			person.setLocation(null);
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

	/**
	 * Initializes the fields
	 */
	private void init() {
		waysOut = new ArrayList<Way>();
		waysIn = new ArrayList<Way>();
		items = new ArrayList<Item>();
		persons = new ArrayList<Person>();
	}
}