package persistence;

import data.Item;
import data.Location;

/**
 * Managing access to the items in the database.
 * 
 * @author Satia
 */
public class ItemManager {
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
	public static Item getItemFromLocation(Location location, String identifier) {
		return NamedObjectManager.getIdentifiableWithIdentifier(
				location.getItems(), identifier);
	}
}