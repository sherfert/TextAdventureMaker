package gui.itemEditing.action;

import data.InventoryItem;
import data.action.AddInventoryItemsAction;
import gui.MainWindowController;
import gui.customui.NamedObjectListView;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for one {@link AddInventoryItemsAction}.
 * 
 * @author Satia
 */
public class AIIActionController extends ActionController<AddInventoryItemsAction> {
	
	@FXML
	private NamedObjectListView<InventoryItem> pickUpItemsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public AIIActionController(CurrentGameManager currentGameManager, MainWindowController mwController, AddInventoryItemsAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();

		pickUpItemsListView.initialize(action.getPickUpItems(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addPickUpItem(ii), (ii) -> action.removePickUpItem(ii));
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
