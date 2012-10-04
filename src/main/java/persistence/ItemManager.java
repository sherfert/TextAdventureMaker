package persistence;

import data.Item;
import data.Location;

/**
 * Managing access to the items in the database
 * 
 * @author Satia
 */
public class ItemManager {
	/**
	 * Removes an item from the database.
	 * 
	 * @param item
	 *            the item
	 */
	public static void removeItem(Item item) {
		Main.getEntityManager().remove(item);
	}

	/**
	 * Gets the item located in the location with the given name or {@code null}
	 * , if there is none.
	 * 
	 * @param location
	 *            the location
	 * @param itemName
	 *            the name of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Item getItemFromLocation(Location location, String itemName) {
		for (Item item : location.getItems()) {
			if (item.getName().equalsIgnoreCase(itemName)) {
				return item;
			}
		}
		return null;
	}
}