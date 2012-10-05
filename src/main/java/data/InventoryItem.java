package data;

import javax.persistence.Entity;

/**
 * Any item that can appear in your inventory. These items are not in locations.
 * 
 * @author Satia
 */
@Entity
public class InventoryItem extends NamedObject {

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link InventoryItem#InventoryItem(String, String, String)}
	 *             instead.
	 */
	@Deprecated
	public InventoryItem() {
	}

	/**
	 * @param name
	 *            the name
	 * @param shortDescription
	 *            the shortDescription
	 * @param longDescription
	 *            the longDescription
	 */
	public InventoryItem(String name, String shortDescription,
			String longDescription) {
		super(name, shortDescription, longDescription);
	}

	/**
	 * Copies name, identifiers, short and long description from the given
	 * inventory item.
	 * 
	 * @param item
	 *            the item to use name, short and long description from
	 */
	public InventoryItem(Item item) {
		super(item.getName(), item.getShortDescription(), item
				.getLongDescription());
		setIdentifiers(item.getIdentifiers());
	}
}