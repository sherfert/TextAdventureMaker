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
	public ChangeUsableObjectActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeUsableObjectAction action) {
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

		newUseForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newUseForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newUseForbiddenTextTF,
				"This will be the new text when the player tries to use an item, unsuccessfully.", noSecondPL);
		setNodeTooltip(newUseForbiddenTextCB,
				"If ticked, the text displayed when the item could not be used will change.");

		newUseSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newUseSuccessfulTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newUseSuccessfulTextTF,
				"This is the new text when the item is successfully used.", noSecondPL);
		setNodeTooltip(newUseSuccessfulTextCB,
				"If ticked, the text displayed when the item was used will change.");
	}

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(action);
	}
}
