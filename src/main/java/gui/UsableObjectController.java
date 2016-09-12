package gui;

import data.UsableObject;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one usable object.
 * 
 * TODO Support to change additionalUseActions
 * 
 * @author Satia
 */
public class UsableObjectController extends GameDataController {

	/** The object */
	private UsableObject object;

	@FXML
	private CheckBox editUsingEnabledCB;

	@FXML
	private TextField editUseSuccessfulTextTF;

	@FXML
	private TextField editUseForbiddenTextTF;

	@FXML
	private TextArea editUseCommandsTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param object
	 *            the object to edit
	 */
	public UsableObjectController(CurrentGameManager currentGameManager, MainWindowController mwController,
			UsableObject object) {
		super(currentGameManager, mwController);
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		editUseSuccessfulTextTF.textProperty().bindBidirectional(object.useSuccessfulTextProperty());
		editUseForbiddenTextTF.textProperty().bindBidirectional(object.useForbiddenTextProperty());
		editUsingEnabledCB.selectedProperty().bindBidirectional(object.usingEnabledProperty());

		editUseCommandsTA.setText(getCommandString(object.getAdditionalUseCommands()));

		editUseCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, editUseCommandsTA, object::setAdditionalUseCommands));
	}
}
