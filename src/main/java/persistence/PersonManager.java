package persistence;

import data.Location;
import data.Person;

/**
 * Managing access to the persons in the database.
 * 
 * @author Satia
 */
public class PersonManager {
	/**
	 * Gets the person located in the location with the given identifier or
	 * {@code null} , if there is none.
	 * 
	 * @param location
	 *            the location
	 * @param identifier
	 *            an identifier of the person
	 * @return the corresponding item or {@code null}.
	 */
	public static Person getPersonFromLocation(Location location, String identifier) {
		return InspectableObjectManager.getIdentifiableWithIdentifier(
				location.getPersons(), identifier);
	}
}