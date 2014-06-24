package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

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
	 * The ways leading inside this location. They get deleted when the
	 * location is deleted.
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
	 * @param name the name
	 * @param description the description
	 */
	public Location(String name, String description) {
		super(name, description);
		init();
	}

	/**
	 * Adds an item to this location. Package visibility, so that from
	 * outside the Item methods are used instead.
	 *
	 * @param item the item
	 */
	void addItem(Item item) {
		if (!this.items.contains(item)) {
			this.items.add(item);
		}
	}

	/**
	 * Adds a person to this location. Package visibility, so that from
	 * outside the Person methods are used instead.
	 *
	 * @param person the person
	 */
	void addPerson(Person person) {
		if (!this.persons.contains(person)) {
			this.persons.add(person);
		}
	}

	/**
	 * Adds a way in. Package visibility, so that from outside the Way
	 * methods are used instead.
	 *
	 * @param wayIn the way
	 */
	void addWayIn(Way wayIn) {
		if (!this.waysIn.contains(wayIn)) {
			this.waysIn.add(wayIn);
		}
	}

	/**
	 * Adds a way out. Package visibility, so that from outside the Way
	 * methods are used instead.
	 *
	 * @param wayOut the way
	 */
	void addWayOut(Way wayOut) {
		if (!this.waysOut.contains(wayOut)) {
			this.waysOut.add(wayOut);
		}
	}

	/**
	 * This String is being displayed when the player enters the location
	 *
	 * @return the short description plus the short descriptions of
	 * everything here.
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
	 * @return a list containing the {@link HasLocation}s: Persons and
	 * Items.
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
	 * location.
	 */
	public List<Inspectable> getInspectables() {
		List<Inspectable> result = new ArrayList<Inspectable>(1
			+ persons.size() + items.size() + waysOut.size());
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
	 * Initializes the fields
	 */
	private void init() {
		waysOut = new ArrayList<Way>();
		waysIn = new ArrayList<Way>();
		items = new ArrayList<Item>();
		persons = new ArrayList<Person>();
	}

	/**
	 * Removes an item from this location. Package visibility, so that from
	 * outside the Item methods are used instead.
	 *
	 * @param item the item
	 */
	void removeItem(Item item) {
		this.items.remove(item);
	}

	/**
	 * Removes a person from this location. Package visibility, so that from
	 * outside the Person methods are used instead.
	 *
	 * @param person the person
	 */
	void removePerson(Person person) {
		this.persons.remove(person);
	}

	/**
	 * Removes a way in. Package visibility, so that from outside the Way
	 * methods are used instead.
	 *
	 * @param wayIn the way
	 */
	void removeWayIn(Way wayIn) {
		this.waysIn.remove(wayIn);
	}

	/**
	 * Removes a way out. Package visibility, so that from outside the Way
	 * methods are used instead.
	 *
	 * @param wayOut the way
	 */
	void removeWayOut(Way wayOut) {
		this.waysOut.remove(wayOut);
	}

	@Override
	public String toString() {
		return "Location{" + "itemsIDs=" + NamedObject.getIDList(items)
			+ ", personsIDs=" + NamedObject.getIDList(persons)
			+ ", waysInIDs=" + NamedObject.getIDList(waysIn)
			+ ", waysOutIDs=" + NamedObject.getIDList(waysOut) + " "
			+ super.toString() + '}';
	}

}
