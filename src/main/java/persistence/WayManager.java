package persistence;

import data.Location;
import data.Way;

/**
 * Managing access to the ways in the database.
 * 
 * @author Satia
 */
public class WayManager {

	/**
	 * Gets the item located in the location with the given identifier or
	 * {@code null} , if there is none.
	 * 
	 * @param location
	 *            the location
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Way getWayOutFromLocation(Location location, String identifier) {
		return NamedObjectManager.getInspectableWithIdentifier(
				location.getWaysOut(), identifier);
	}
}