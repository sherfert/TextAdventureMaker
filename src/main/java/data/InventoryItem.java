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
	 *             {@link InventoryItem#InventoryItem(String, String)}
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

	/**
	 * Copies name, description, identifiers and inspectionText from the given
	 * inventory item.
	 * 
	 * @param item
	 *            the item to use name, description, identifiers and
	 *            inspectionText from
	 */
	public InventoryItem(Item item) {
		super(item.getName(), item.getDescription());
		setIdentifiers(item.getIdentifiers());
		setInspectionText(item.getInspectionText());
	}
}