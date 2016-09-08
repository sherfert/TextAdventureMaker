package gui.custumui;

import java.util.function.Consumer;

import data.Item;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing items. Must be initialized with
 * {@link ItemChooser#initialize(Item, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
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
