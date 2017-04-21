package gui.include;

import data.action.ChangeUsableObjectAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.itemEditing.action.ActionController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller to change properties of one usable object.
 * 
 * @author Satia
 */
public class ChangeUsableObjectActionController extends GameDataController {

	/** The action */
	private ChangeUsableObjectAction action;
	
	
	@FXML
	private RadioButton doNotChangeUseRB;

	@FXML
	private RadioButton enableUseRB;

	@FXML
	private RadioButton disableUseRB;

	@FXML
	private ToggleGroup enablingUseTG;

	@FXML
	private CheckBox newUseSuccessfulTextCB;

	@FXML
	private TextField newUseSuccessfulTextTF;

	@FXML
	private CheckBox newUseForbiddenTextCB;

	@FXML
	private TextField newUseForbiddenTextTF;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeUsableObjectActionController(CurrentGameManager currentGameManager,
			MainWindowController mwController, ChangeUsableObjectAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		ActionController.initRadioButtonEnablingGroup(enablingUseTG, doNotChangeUseRB, enableUseRB, disableUseRB,
				action::getEnabling, action::setEnabling);
		ActionController.initCheckBoxAndTextFieldSetter(newUseSuccessfulTextCB, newUseSuccessfulTextTF,
				action::getNewUseSuccessfulText, action::setNewUseSuccessfulText);
		ActionController.initCheckBoxAndTextFieldSetter(newUseForbiddenTextCB, newUseForbiddenTextTF,
				action::getNewUseForbiddenText, action::setNewUseForbiddenText);
	}
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(action);
	}
}
