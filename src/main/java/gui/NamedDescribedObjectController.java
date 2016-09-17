package gui;

import data.NamedDescribedObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for one named described object.
 * 
 * @author Satia
 */
public class NamedDescribedObjectController extends GameDataController {

	/** The object */
	private NamedDescribedObject object;

	@FXML
	private TextArea editDescriptionTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param object
	 *            the object to edit
	 */
	public NamedDescribedObjectController(CurrentGameManager currentGameManager, MainWindowController mwController,
			NamedDescribedObject object) {
		super(currentGameManager, mwController);
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		editDescriptionTA.textProperty().bindBidirectional(object.descriptionProperty());
	}
}
