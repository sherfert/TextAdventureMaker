package gui.itemEditing.action;

import data.action.AddInventoryItemsAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.InventoryItemListView;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import logic.CurrentGameManager;

/**
 * Controller for one {@link AddInventoryItemsAction}.
 * 
 * @author Satia
 */
public class AIIActionController extends GameDataController {

	/** The action */
	private AddInventoryItemsAction action;
	
	@FXML
	private InventoryItemListView pickUpItemsListView;

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
	public AIIActionController(CurrentGameManager currentGameManager, MainWindowController mwController, AddInventoryItemsAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		// Create new bindings

		pickUpItemsListView.initialize(action.getPickUpItems(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addPickUpItem(ii), (ii) -> action.removePickUpItem(ii));

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
