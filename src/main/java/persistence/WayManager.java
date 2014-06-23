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
	 * Gets the way out from the given location with the given identifier or
	 * {@code null} , if there is none.
	 * 
	 * @param location
	 *            the location
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Way getWayOutFromLocation(Location location, String identifier) {
		return InspectableObjectManager.getIdentifiableWithIdentifier(
				location.getWaysOut(), identifier);
	}
}