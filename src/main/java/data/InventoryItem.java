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
	 * @deprecated Use {@link InventoryItem#InventoryItem(String, String)}
	 *             instead.
	 */
	@Deprecated
	public InventoryItem() {
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public InventoryItem(String name, String description) {
		super(name, description);
	}
}