package gui.itemEditing.action;

import data.Location;
import data.action.ChangeWayAction;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.include.AbstractActionController;
import gui.include.ChangeInspectableObjectActionController;
import gui.include.ChangeNDObjectActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeWayAction}.
 * 
 * @author Satia
 */
public class ChangeWayActionController extends ActionController<ChangeWayAction> {

	@FXML
	private RadioButton doNotChangeWayRB;

	@FXML
	private RadioButton enableWayRB;

	@FXML
	private RadioButton disableWayRB;

	@FXML
	private ToggleGroup enablingWayTG;

	@FXML
	private CheckBox newOriginCB;

	@FXML
	private NamedObjectChooser<Location> newOriginChooser;

	@FXML
	private CheckBox newDestinationCB;

	@FXML
	private NamedObjectChooser<Location> newDestinationChooser;

	@FXML
	private CheckBox newMoveSuccessfulTextCB;

	@FXML
	private TextField newMoveSuccessfulTextTF;

	@FXML
	private CheckBox newMoveForbiddenTextCB;

	@FXML
	private TextField newMoveForbiddenTextTF;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeWayActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeWayAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();

		initRadioButtonEnablingGroup(enablingWayTG, doNotChangeWayRB, enableWayRB, disableWayRB,
				action::getEnabling, action::setEnabling);

		newOriginChooser.initialize(action.getNewOrigin(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewOrigin);
		initCheckBoxAndChooserNoNull(newOriginCB, newOriginChooser, action::getNewOrigin, action::setNewOrigin);

		newDestinationChooser.initialize(action.getNewDestination(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewDestination);
		initCheckBoxAndChooserNoNull(newDestinationCB, newDestinationChooser, action::getNewDestination, action::setNewDestination);
		
		initCheckBoxAndTextFieldSetter(newMoveSuccessfulTextCB, newMoveSuccessfulTextTF,
				action::getNewMoveSuccessfulText, action::setNewMoveSuccessfulText);
		initCheckBoxAndTextFieldSetter(newMoveForbiddenTextCB, newMoveForbiddenTextTF,
				action::getNewMoveForbiddenText, action::setNewMoveForbiddenText);
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
		} else {
			return super.controllerFactory(type);
		}
	}
}
