package gui.customui;

import data.InventoryItem;

/**
 * Custom TextField for choosing inventory items.
 * 
 * @author satia
 */
public class InventoryItemChooser extends NamedObjectChooser<InventoryItem> {

	/**
	 * Create a new LocationChooser
	 */
	public InventoryItemChooser() {
		super("(no inventory item)");
	}
}
