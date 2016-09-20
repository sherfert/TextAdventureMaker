package gui.include;

import data.action.ChangeNDObjectAction;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one named described object.
 * 
 * TODO link to the NDO under change
 * 
 * @author Satia
 */
public class ChangeNDObjectActionController extends GameDataController {

	/** The action */
	private ChangeNDObjectAction action;
	
	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeNDObjectActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeNDObjectAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		// TODO Create new bindings
		// Since null is a valid value, a Property is not a good idea.
		// Also, there should be checkboxes to decide if a value should be changed or not.
	}
}
