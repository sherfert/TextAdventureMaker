package gui.itemEditing.action;

import data.Location;
import data.action.MoveAction;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for one {@link MoveAction}.
 * 
 * @author Satia
 */
public class MoveActionController extends ActionController<MoveAction> {

	@FXML
	private NamedObjectChooser<Location> targetChooser;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public MoveActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			MoveAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		targetChooser.initialize(action.getTarget(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setTarget);
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
