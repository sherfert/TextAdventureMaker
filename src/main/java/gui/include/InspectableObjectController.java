package gui.include;

import data.InspectableObject;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.ActionListView;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for one inspectable object.
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

	@FXML
	private ActionListView inspectActionsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
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

		inspectActionsListView.initialize(object.getAdditionalInspectActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions, null,
				this::objectSelected, (a) -> object.addAdditionalInspectAction(a),
				(a) -> object.removeAdditionalInspectAction(a));
	}
}
