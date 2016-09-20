package gui.itemEditing.action;

import data.action.ChangeNDObjectAction;
import gui.MainWindowController;
import gui.include.AbstractActionController;
import gui.include.ChangeNDObjectActionController;
import gui.include.NamedObjectController;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeNDObjectAction}, that is used to change a
 * Location.
 * 
 * @author Satia
 */
public class ChangeLocationActionController extends ActionController<ChangeNDObjectAction> {

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeLocationActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeNDObjectAction action) {
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
		} else {
			return super.controllerFactory(type);
		}
	}
}
