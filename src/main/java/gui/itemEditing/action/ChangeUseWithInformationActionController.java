package gui.itemEditing.action;

import data.action.ChangeUseWithInformationAction;
import gui.MainWindowController;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeUseWithInformationAction}.
 * 
 * @author Satia
 */
public class ChangeUseWithInformationActionController extends ActionController<ChangeUseWithInformationAction> {

	@FXML
	private Hyperlink linkItem1;

	@FXML
	private Hyperlink linkItem2;

	@FXML
	private RadioButton doNotChangeUseWithRB;

	@FXML
	private RadioButton enableUseWithRB;

	@FXML
	private RadioButton disableUseWithRB;

	@FXML
	private ToggleGroup enablingUseWithTG;

	@FXML
	private CheckBox newUseWithSuccessfulTextCB;

	@FXML
	private TextField newUseWithSuccessfulTextTF;

	@FXML
	private CheckBox newUseWithForbiddenTextCB;

	@FXML
	private TextField newUseWithForbiddenTextTF;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeUseWithInformationActionController(CurrentGameManager currentGameManager,
			MainWindowController mwController, ChangeUseWithInformationAction action) {
		super(currentGameManager, mwController, action);
	}

	@Override
	protected void initialize() {
		super.initialize();

		linkItem1.setText("Changing use of: " + action.getInventoryItem().toString());
		linkItem1.setOnAction((e) -> {
			objectSelected(action.getInventoryItem());
		});

		linkItem2.setText("with: " + action.getObject().toString());
		linkItem2.setOnAction((e) -> {
			objectSelected(action.getObject());
		});

		initRadioButtonEnablingGroup(enablingUseWithTG, doNotChangeUseWithRB, enableUseWithRB, disableUseWithRB,
				action::getEnabling, action::setEnabling);
		setNodeTooltip(enableUseWithRB, "Triggering this action will enable using the items together.");
		setNodeTooltip(disableUseWithRB, "Triggering this action will disable using the items together.");
		setNodeTooltip(doNotChangeUseWithRB,
				"Triggering this action will not change if the items can be used together.");

		initCheckBoxAndTextFieldSetter(newUseWithSuccessfulTextCB, newUseWithSuccessfulTextTF,
				action::getNewUseWithSuccessfulText, action::setNewUseWithSuccessfulText);
		newUseWithSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newUseWithSuccessfulTextTF, allPL, true));
		addPlaceholderTextTooltip(newUseWithSuccessfulTextTF,
				"This will be the new text when the player uses the two items together.", allPL);
		setNodeTooltip(newUseWithSuccessfulTextCB,
				"If ticked, the text displayed when the items are used together will change.");

		initCheckBoxAndTextFieldSetter(newUseWithForbiddenTextCB, newUseWithForbiddenTextTF,
				action::getNewUseWithForbiddenText, action::setNewUseWithForbiddenText);
		newUseWithForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newUseWithForbiddenTextTF, allPL, true));
		addPlaceholderTextTooltip(newUseWithForbiddenTextTF,
				"This will be the new text when the player tries to use the items together, unsuccessfully.", allPL);
		setNodeTooltip(newUseWithForbiddenTextCB,
				"If ticked, the text displayed when the items could not be used together will change.");
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, action);
		} else if (type == AbstractActionController.class) {
			return new AbstractActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
