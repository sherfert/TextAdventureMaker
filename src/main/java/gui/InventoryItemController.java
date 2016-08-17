package gui;

import data.InventoryItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import logic.CurrentGameManager;

/**
 * Controller for one inventory item.
 * 
 * TODO Support to change additionalCombineCommands, combinableInvItems, usableItems, usablePersons
 * 
 * @author Satia
 */
public class InventoryItemController extends GameDataController {

	/** The inventory item */
	private InventoryItem invitem;

	@FXML
	private Button removeButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param item
	 *            the inventory item to edit
	 */
	public InventoryItemController(CurrentGameManager currentGameManager, MainWindowController mwController, InventoryItem item) {
		super(currentGameManager, mwController);
		this.invitem = item;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		removeButton.setOnMouseClicked(
				(e) -> removeObject(invitem, "Deleting an invenory item", "Do you really want to delete this inventory item?",
						"This will delete the inventory item, usage information with other inventory items, items and persons, "
								+ "and actions associated with any of the deleted entities."));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, invitem);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, invitem);
		} else if (type == UsableObjectController.class) {
			return new UsableObjectController(currentGameManager, mwController, invitem);
		} else {
			return super.controllerFactory(type);
		}
	}
}
