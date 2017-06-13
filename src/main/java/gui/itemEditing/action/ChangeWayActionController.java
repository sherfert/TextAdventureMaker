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
import javafx.scene.control.TabPane;
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
	private TabPane tabPane;
	
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

		initRadioButtonEnablingGroup(enablingWayTG, doNotChangeWayRB, enableWayRB, disableWayRB, action::getEnabling,
				action::setEnabling);
		setNodeTooltip(enableWayRB, "Triggering this action will enable using the way.");
		setNodeTooltip(disableWayRB, "Triggering this action will disable using the way.");
		setNodeTooltip(doNotChangeWayRB, "Triggering this action will not change if the way can be used.");

		newOriginChooser.initialize(action.getNewOrigin(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewOrigin);
		initCheckBoxAndChooserNoNull(newOriginCB, newOriginChooser, action::getNewOrigin, action::setNewOrigin);
		setNodeTooltip(newOriginChooser, "This will be the new starting point of the way.");
		setNodeTooltip(newOriginCB, "If ticked, the starting point of the way will change.");

		newDestinationChooser.initialize(action.getNewDestination(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewDestination);
		initCheckBoxAndChooserNoNull(newDestinationCB, newDestinationChooser, action::getNewDestination,
				action::setNewDestination);
		setNodeTooltip(newDestinationChooser, "This will be the new ending point of the way.");
		setNodeTooltip(newDestinationCB, "If ticked, the ending point of the way will change.");

		initCheckBoxAndTextFieldSetter(newMoveSuccessfulTextCB, newMoveSuccessfulTextTF,
				action::getNewMoveSuccessfulText, action::setNewMoveSuccessfulText);
		newMoveSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newMoveSuccessfulTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newMoveSuccessfulTextTF, "This will be the new text when the player uses the way.",
				noSecondPL);
		setNodeTooltip(newMoveSuccessfulTextCB,
				"If ticked, the text displayed when the player uses the way will change.");

		initCheckBoxAndTextFieldSetter(newMoveForbiddenTextCB, newMoveForbiddenTextTF, action::getNewMoveForbiddenText,
				action::setNewMoveForbiddenText);
		newMoveForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newMoveForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newMoveForbiddenTextTF,
				"This will be the new text when the player tries to use the way, unsuccessfully.", noSecondPL);
		setNodeTooltip(newMoveForbiddenTextCB,
				"If ticked, the text displayed when the way could not be used will change.");
		
		saveTabIndex(tabPane);
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
