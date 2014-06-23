package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.interfaces.HasLocation;

/**
 * A person.
 * 
 * @author Satia
 */
@Entity
public class Person extends InspectableObject implements HasLocation {

	/**
	 * The current location of the person. May be {@code null}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link Person#Person(String, String)} or
	 *             {@link Person#Person(Location, String, String)} instead.
	 */
	@Deprecated
	public Person() {
	}

	/**
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Person(Location location, String name, String description) {
		super(name, description);
		setLocation(location);
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Person(String name, String description) {
		super(name, description);
	}

	@Override
	public Location getLocation() {
		return location;
	}
	
	// TODO the duplicate Logic is too much. Just make the Location methods package
	// visible for Person/Item modification.
	@Override
	public void setLocation(Location location) {
		if (this.location != null) {
			this.location.removePerson(this);
		}
		if (location != null) {
			location.addPerson(this);
		}
		this.location = location;
	}
}