package gui.customui;

import data.InventoryItem;

/**
 * ListView to manage inventory items.
 * 
 * @author Satia
 */
public class InventoryItemListView extends NamedObjectListView<InventoryItem> {

	public InventoryItemListView() {
		super(new InventoryItemChooser());
	}

}
