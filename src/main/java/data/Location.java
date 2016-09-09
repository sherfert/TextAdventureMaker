package data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
			// Give the item the highest locationOrder, so it appears last in
			// the list
			int newOrder = this.items.stream().map(Item::getLocationOrder).max(Comparator.<Integer> naturalOrder())
					.orElse(-1) + 1;
			item.setLocationOrder(newOrder);
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
			// Give the item the highest locationOrder, so it appears last in
			// the list
			int newOrder = this.persons.stream().map(Person::getLocationOrder).max(Comparator.<Integer> naturalOrder())
					.orElse(-1) + 1;
			person.setLocationOrder(newOrder);
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
			// Give the item the highest destinationOrder, so it appears last in
			// the list
			int newOrder = this.waysIn.stream().map(Way::getDestinationOrder).max(Comparator.<Integer> naturalOrder())
					.orElse(-1) + 1;
			wayIn.setDestinationOrder(newOrder);
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
			// Give the item the highest originOrder, so it appears last in
			// the list
			int newOrder = this.waysOut.stream().map(Way::getOriginOrder).max(Comparator.<Integer> naturalOrder())
					.orElse(-1) + 1;
			wayOut.setOriginOrder(newOrder);
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
	 * 
	 *         FIXME why get items deleted
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	@OrderBy("locationOrder ASC, getId ASC")
	public List<Item> getItems() {
		return items;
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
		// Set items list, so that the order of the passed list is preserved
		items.clear();
		items.addAll(newItems);
		// Update the location order on all items
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setLocationOrder(i);
		}
	}

	/**
	 * @return the persons
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	@OrderBy("locationOrder ASC, getId ASC")
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
		// Set persons list, so that the order of the passed list is preserved
		persons.clear();
		persons.addAll(newPersons);
		// Update the location order on all persons
		for (int i = 0; i < persons.size(); i++) {
			persons.get(i).setLocationOrder(i);
		}
	}

	/**
	 * @return the waysOut
	 */
	@OneToMany(mappedBy = "origin", cascade = { CascadeType.PERSIST })
	@OrderBy("originOrder ASC, getId ASC")
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
		// Set ways list, so that the order of the passed list is preserved
		waysOut.clear();
		waysOut.addAll(newWays);
		// Update the origin order on all ways
		for (int i = 0; i < waysOut.size(); i++) {
			waysOut.get(i).setOriginOrder(i);
		}
	}

	/**
	 * @return the waysIn
	 */
	@OneToMany(mappedBy = "destination", cascade = { CascadeType.PERSIST })
	@OrderBy("destinationOrder ASC, getId ASC")
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
		// Set ways list, so that the order of the passed list is preserved
		waysIn.clear();
		waysIn.addAll(newWays);
		// Update the destination order on all items
		for (int i = 0; i < waysIn.size(); i++) {
			waysIn.get(i).setDestinationOrder(i);
		}
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

}
