package gui.customui;

import data.Item;

/**
 * Custom TextField for choosing items.
 * 
 * @author satia
 */
public class ItemChooser extends NamedObjectChooser<Item> {

	/**
	 * Create a new LocationChooser
	 */
	public ItemChooser() {
		super("(no item)");
	}
}
