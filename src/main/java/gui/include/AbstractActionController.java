package gui.include;

import data.action.AbstractAction;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import logic.CurrentGameManager;

/**
 * Controller for one AbstractAction
 * 
 * @author Satia
 */
public class AbstractActionController extends GameDataController {

	/** The action */
	private AbstractAction action;

	@FXML
	private CheckBox editEnabledCB;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public AbstractActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			AbstractAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		editEnabledCB.selectedProperty().bindBidirectional(action.enabledProperty());
		setNodeTooltip(editEnabledCB, "If ticked, the action can be triggered and will have an effect.");
	}

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(action);
	}
}
