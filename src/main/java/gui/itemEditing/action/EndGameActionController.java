package gui.itemEditing.action;

import data.action.EndGameAction;
import gui.MainWindowController;
import gui.customui.InventoryItemChooser;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for one {@link EndGameAction}.
 * 
 * @author Satia
 */
public class EndGameActionController extends ActionController<EndGameAction> {

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
	public EndGameActionController(CurrentGameManager currentGameManager, MainWindowController mwController, EndGameAction action) {
		super(currentGameManager, mwController, action);
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
