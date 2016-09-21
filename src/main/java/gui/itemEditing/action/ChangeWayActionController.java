package gui.itemEditing.action;

import data.action.ChangeWayAction;
import data.action.AbstractAction.Enabling;
import gui.MainWindowController;
import gui.customui.LocationChooser;
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
	private LocationChooser newOriginChooser;

	@FXML
	private CheckBox newDestinationCB;

	@FXML
	private LocationChooser newDestinationChooser;

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

		switch(action.getEnabling()) {
		case DISABLE:
			enablingWayTG.selectToggle(disableWayRB);
			break;
		case DO_NOT_CHANGE:
			enablingWayTG.selectToggle(doNotChangeWayRB);
			break;
		case ENABLE:
			enablingWayTG.selectToggle(enableWayRB);
			break;
		}
		enablingWayTG.selectedToggleProperty().addListener((f, o, n) -> {
			if(n == doNotChangeWayRB) {
				action.setEnabling(Enabling.DO_NOT_CHANGE);
			} else if(n == enableWayRB) {
				action.setEnabling(Enabling.ENABLE);
			} else if(n == disableWayRB) {
				action.setEnabling(Enabling.DISABLE);
			}
		});

		newOriginChooser.initialize(action.getNewOrigin(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewOrigin);
		newOriginChooser.disableProperty().bind(newOriginCB.selectedProperty().not());
		newOriginCB.setSelected(action.getNewOrigin() != null);
		newOriginCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewOrigin(null);
			}
		});

		newDestinationChooser.initialize(action.getNewDestination(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewDestination);
		newDestinationChooser.disableProperty().bind(newDestinationCB.selectedProperty().not());
		newDestinationCB.setSelected(action.getNewDestination() != null);
		newDestinationCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewDestination(null);
			}
		});

		newMoveSuccessfulTextTF.setText(action.getNewMoveSuccessfulText());
		newMoveSuccessfulTextTF.disableProperty().bind(newMoveSuccessfulTextCB.selectedProperty().not());
		newMoveSuccessfulTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewMoveSuccessfulText(n);
		});
		newMoveSuccessfulTextCB.setSelected(action.getNewMoveSuccessfulText() != null);
		newMoveSuccessfulTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewMoveSuccessfulText(null);
			}
		});

		newMoveForbiddenTextTF.setText(action.getNewMoveForbiddenText());
		newMoveForbiddenTextTF.disableProperty().bind(newMoveForbiddenTextCB.selectedProperty().not());
		newMoveForbiddenTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewMoveForbiddenText(n);
		});
		newMoveForbiddenTextCB.setSelected(action.getNewMoveForbiddenText() != null);
		newMoveForbiddenTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewMoveForbiddenText(null);
			}
		});
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
