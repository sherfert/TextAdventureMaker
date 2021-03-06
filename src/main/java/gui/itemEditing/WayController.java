package gui.itemEditing;

import data.Location;
import data.Way;
import data.action.AbstractAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.customui.NamedObjectListView;
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
	private NamedObjectChooser<Location> originChooser;

	@FXML
	private NamedObjectChooser<Location> destinationChooser;

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
	private NamedObjectListView<AbstractAction> moveActionsListView;

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
		setNodeTooltip(originChooser, "Where does the way start?");

		destinationChooser.initialize(way.getDestination(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				way::setDestination);
		setNodeTooltip(destinationChooser, "Where does the way lead to?");

		removeButton
				.setOnMouseClicked((e) -> removeObject(way, "Deleting a way", "Do you really want to delete this way?",
						"This will delete the way, and actions associated with any of the deleted entities."));

		editMoveSuccessfulTextTF.textProperty().bindBidirectional(way.moveSuccessfulTextProperty());
		editMoveSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editMoveSuccessfulTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(editMoveSuccessfulTextTF,
				"This is the text when the player uses the way, before displaying information about the new location. "
						+ "If empty, only information about the new location is displayed.",
				noSecondPL);

		editMoveForbiddenTextTF.textProperty().bindBidirectional(way.moveForbiddenTextProperty());
		editMoveForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editMoveForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(editMoveForbiddenTextTF,
				"This text is displayed when the player tries to use this way, unsuccessfully. If empty, the default will be used.",
				noSecondPL);

		editMovingEnabledCB.setSelected(way.isMovingEnabled());
		editMovingEnabledCB.selectedProperty().addListener((f, o, n) -> way.setMovingEnabled(n));
		setNodeTooltip(editMovingEnabledCB, "If ticked, the way can be used.");

		editMoveCommandsTA.setText(getCommandString(way.getAdditionalMoveCommands()));
		editMoveCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, true, editMoveCommandsTA, way::setAdditionalMoveCommands));
		addCommandTooltip(editMoveCommandsTA,
				"Additional commands to use the way. These will only be valid for this way.");

		moveCommandsLabel.setText("Additional commands for using " + way.getName());

		moveActionsListView.initialize(way.getAdditionalMoveActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions,
				way::setAdditionalMoveActions, this::objectSelected, way::addAdditionalMoveAction,
				way::removeAdditionalMoveAction);

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

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(way);
	}
}
