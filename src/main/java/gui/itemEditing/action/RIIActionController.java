package gui.itemEditing.action;

import data.action.RemoveInventoryItemAction;
import gui.MainWindowController;
import gui.customui.InventoryItemChooser;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for one {@link RemoveInventoryItemAction}.
 * 
 * @author Satia
 */
public class RIIActionController extends ActionController<RemoveInventoryItemAction> {


	@FXML
	private InventoryItemChooser inventoryItemChooser;


	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public RIIActionController(CurrentGameManager currentGameManager, MainWindowController mwController, RemoveInventoryItemAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		inventoryItemChooser.initialize(action.getItem(), false, false,
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems,
				action::setItem);
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