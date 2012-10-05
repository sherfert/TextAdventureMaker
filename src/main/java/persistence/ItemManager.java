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
	 * Removes an item from the database.
	 * 
	 * TODO general remove method?
	 * 
	 * @param item
	 *            the item
	 */
	public static void removeItem(Item item) {
		Main.getEntityManager().remove(item);
	}

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
		return NamedObjectManager.getInspectableWithIdentifier(
				location.getItems(), identifier);
	}
}