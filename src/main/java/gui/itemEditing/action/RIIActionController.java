package gui.itemEditing.action;

import data.action.RemoveInventoryItemAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.InventoryItemChooser;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import logic.CurrentGameManager;

/**
 * Controller for one {@link RemoveInventoryItemAction}.
 * 
 * @author Satia
 */
public class RIIActionController extends GameDataController {

	/** The action */
	private RemoveInventoryItemAction action;

	@FXML
	private InventoryItemChooser inventoryItemChooser;

	@FXML
	private Button removeButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public RIIActionController(CurrentGameManager currentGameManager, MainWindowController mwController, RemoveInventoryItemAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		inventoryItemChooser.initialize(action.getItem(), false, false,
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems,
				action::setItem);

		removeButton.setOnMouseClicked(
				(e) -> removeObject(action, "Deleting an action", "Do you really want to delete this action?",
						"No other entities will be deleted."));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, action);
		} else if (type == AbstractActionController.class) {
			return new AbstractActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
