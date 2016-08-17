package gui;

import data.Way;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one way.
 * 
 * TODO Support to change origin, destination, additionalMoveActions
 * 
 * @author Satia
 */
public class WayController extends GameDataController {

	/** The way */
	private Way way;

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

	/**
	 * @param currentGameManager
	 *            the game manager
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
		// alert.setTitle("Deleting a way");
		removeButton
				.setOnMouseClicked((e) -> removeObject(way, "Deleting a way", "Do you really want to delete this way?",
						"This will delete the way, and actions associated with any of the deleted entities."));
		editMoveSuccessfulTextTF.textProperty().bindBidirectional(way.moveSuccessfulTextProperty());
		editMoveForbiddenTextTF.textProperty().bindBidirectional(way.moveForbiddenTextProperty());

		editMovingEnabledCB.setSelected(way.isMovingEnabled());
		editMovingEnabledCB.selectedProperty().addListener((f, o, n) -> way.setMovingEnabled(n));

		editMoveCommandsTA.setText(getCommandString(way.getAdditionalTravelCommands()));
		editMoveCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, editMoveCommandsTA, way::setAdditionalTravelCommands));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, way);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, way);
		} else {
			return super.controllerFactory(type);
		}
	}
}
