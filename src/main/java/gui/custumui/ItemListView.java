package gui.custumui;

import data.Item;

/**
 * TODO
 * 
 * @author Satia
 */
public class ItemListView extends NamedObjectListView<Item> {

	public ItemListView() {
		super(new ItemChooser());
	}

}
