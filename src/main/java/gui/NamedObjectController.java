package gui;

import data.NamedObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one named object
 * 
 * @author Satia
 */
public class NamedObjectController extends GameDataController {

	/** The named object */
	private NamedObject object;

	@FXML
	private TextField editNameTF;

	@FXML
	private TextArea editDescriptionTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param object
	 *            the object to edit
	 */
	public NamedObjectController(CurrentGameManager currentGameManager, MainWindowController mwController, NamedObject object) {
		super(currentGameManager, mwController);
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		editNameTF.textProperty().bindBidirectional(object.nameProperty());
		editDescriptionTA.textProperty().bindBidirectional(object.descriptionProperty());
	}
}
