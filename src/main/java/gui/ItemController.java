package gui;

import data.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import logic.CurrentGameManager;

/**
 * Controller for one item.
 * 
 * TODO Support to change fields of InspectableObject, UsableObject, and Item
 * 
 * @author Satia
 */
public class ItemController extends GameDataController {

	/** The item */
	private Item item;

	@FXML
	private Button removeButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param item
	 *            the item to edit
	 */
	public ItemController(CurrentGameManager currentGameManager, MainWindowController mwController, Item item) {
		super(currentGameManager, mwController);
		this.item = item;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		removeButton.setOnMouseClicked((e) -> removeItem());
	}

	/**
	 * Controller factory that initializes named object controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, item);
		} else {
			return super.controllerFactory(type);
		}
	}

	/**
	 * Removes an item from the DB.
	 */
	private void removeItem() {
		// Show a confirmation dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Deleting an item");
		alert.setHeaderText("Do you really want to delete this item?");
		alert.setContentText(
				"This will delete the item, usage information of inventory items with this item, "
				+ "and actions associated with any of the deleted entities.");
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Remove item from DB
				currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(item);
				currentGameManager.getPersistenceManager().updateChanges();

				// Switch back to previous view
				mwController.popCenterContent();
			}
		});
	}
}
