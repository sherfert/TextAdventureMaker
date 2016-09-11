package gui.custumui;

import java.util.function.Consumer;

import data.InventoryItem;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing inventory items. Must be initialized with
 * {@link InventoryItemChooser#initialize(InventoryItem, boolean, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
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
