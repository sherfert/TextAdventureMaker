package gui.customui;

import data.Item;

/**
 * ListView to manage items.
 * 
 * @author Satia
 */
public class ItemListView extends NamedObjectListView<Item> {

	public ItemListView() {
		super(new ItemChooser());
	}

}
