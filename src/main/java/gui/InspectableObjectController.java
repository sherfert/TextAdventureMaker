package gui;

import data.InspectableObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for one inspectable object.
 * 
 * TODO Support to change additionalInspectActions
 * 
 * @author Satia
 */
public class InspectableObjectController extends GameDataController {

	/** The object */
	private InspectableObject object;

	@FXML
	private TextArea editInspectionTextTA;

	@FXML
	private TextArea editIdentifiersTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param object
	 *            the object to edit
	 */
	public InspectableObjectController(CurrentGameManager currentGameManager, MainWindowController mwController,
			InspectableObject object) {
		super(currentGameManager, mwController);
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		editInspectionTextTA.textProperty().bindBidirectional(object.inspectionTextProperty());
		editIdentifiersTA.setText(getListString(object.getIdentifiers()));
		editIdentifiersTA.textProperty().addListener((f, o, n) -> updateList(n, object::setIdentifiers));
	}
}
