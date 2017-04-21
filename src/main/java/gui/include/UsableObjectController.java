package gui.include;

import data.UsableObject;
import data.action.AbstractAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectListView;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one usable object.
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

	@FXML
	private Label useCommandsLabel;

	@FXML
	private NamedObjectListView<AbstractAction> useActionsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
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
				(f, o, n) -> updateGameCommands(n, 1, true, editUseCommandsTA, object::setAdditionalUseCommands));

		useCommandsLabel.setText("Additional commands for using " + object.getName());

		useActionsListView.initialize(object.getAdditionalUseActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions, null,
				this::objectSelected, (a) -> object.addAdditionalUseAction(a),
				(a) -> object.removeAdditionalUseAction(a));
	}
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(object);
	}
}
