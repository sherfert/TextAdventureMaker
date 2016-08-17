package gui;

import data.NamedDescribedObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one named object.
 * 
 * TODO also a NamedObjectController?
 * 
 * @author Satia
 */
public class NamedDescribedObjectController extends GameDataController {

	/** The object */
	private NamedDescribedObject object;

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
	public NamedDescribedObjectController(CurrentGameManager currentGameManager, MainWindowController mwController, NamedDescribedObject object) {
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
