package gui.itemEditing.action;

import data.action.ChangeUsableObjectAction;
import gui.MainWindowController;
import gui.include.AbstractActionController;
import gui.include.ChangeInspectableObjectActionController;
import gui.include.ChangeNDObjectActionController;
import gui.include.ChangeUsableObjectActionController;
import gui.include.NamedObjectController;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeUsableObjectAction}, that is used to change
 * an inventory item.
 * 
 * @author Satia
 */
public class ChangeInventoryItemActionController extends ActionController<ChangeUsableObjectAction> {

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeInventoryItemActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeUsableObjectAction action) {
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
		} else if (type == ChangeNDObjectActionController.class) {
			return new ChangeNDObjectActionController(currentGameManager, mwController, action);
		} else if (type == ChangeInspectableObjectActionController.class) {
			return new ChangeInspectableObjectActionController(currentGameManager, mwController, action);
		} else if (type == ChangeUsableObjectActionController.class) {
			return new ChangeUsableObjectActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
