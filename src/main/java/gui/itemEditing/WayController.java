package gui.itemEditing;

import data.Way;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.ActionListView;
import gui.customui.LocationChooser;
import gui.include.InspectableObjectController;
import gui.include.NamedDescribedObjectController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one way.
 * 
 * @author Satia
 */
public class WayController extends GameDataController {

	/** The way */
	private Way way;
	
	@FXML
	private TabPane tabPane;

	@FXML
	private LocationChooser originChooser;

	@FXML
	private LocationChooser destinationChooser;

	@FXML
	private Button removeButton;

	@FXML
	private CheckBox editMovingEnabledCB;

	@FXML
	private TextField editMoveSuccessfulTextTF;

	@FXML
	private TextField editMoveForbiddenTextTF;

	@FXML
	private TextArea editMoveCommandsTA;

	@FXML
	private Label moveCommandsLabel;

	@FXML
	private ActionListView moveActionsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param way
	 *            the way to edit
	 */
	public WayController(CurrentGameManager currentGameManager, MainWindowController mwController, Way way) {
		super(currentGameManager, mwController);
		this.way = way;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		originChooser.initialize(way.getOrigin(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, way::setOrigin);
		destinationChooser.initialize(way.getDestination(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				way::setDestination);

		removeButton
				.setOnMouseClicked((e) -> removeObject(way, "Deleting a way", "Do you really want to delete this way?",
						"This will delete the way, and actions associated with any of the deleted entities."));
		editMoveSuccessfulTextTF.textProperty().bindBidirectional(way.moveSuccessfulTextProperty());
		editMoveForbiddenTextTF.textProperty().bindBidirectional(way.moveForbiddenTextProperty());

		editMovingEnabledCB.setSelected(way.isMovingEnabled());
		editMovingEnabledCB.selectedProperty().addListener((f, o, n) -> way.setMovingEnabled(n));

		editMoveCommandsTA.setText(getCommandString(way.getAdditionalMoveCommands()));
		editMoveCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, true, editMoveCommandsTA, way::setAdditionalMoveCommands));
		
		moveCommandsLabel.setText("Additional commands for using " + way.getName());

		moveActionsListView.initialize(way.getAdditionalMoveActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions, null,
				this::objectSelected, (a) -> way.addAdditionalMoveAction(a),
				(a) -> way.removeAdditionalMoveAction(a));
		
		saveTabIndex(tabPane);
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, way);
		} else if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, way);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, way);
		} else {
			return super.controllerFactory(type);
		}
	}
}
