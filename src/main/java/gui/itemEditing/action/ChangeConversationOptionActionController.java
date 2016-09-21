package gui.itemEditing.action;

import data.action.ChangeConversationOptionAction;
import gui.MainWindowController;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeConversationOptionAction}.
 * 
 * @author Satia
 */
public class ChangeConversationOptionActionController extends ActionController<ChangeConversationOptionAction> {

	@FXML
	private Hyperlink link;

	@FXML
	private RadioButton doNotChangeOptionRB;

	@FXML
	private RadioButton enableOptionRB;

	@FXML
	private RadioButton disableOptionRB;

	@FXML
	private ToggleGroup enablingOptionTG;

	@FXML
	private RadioButton doNotChangeRemoveOptionRB;

	@FXML
	private RadioButton enableRemoveOptionRB;

	@FXML
	private RadioButton disableRemoveOptionRB;

	@FXML
	private ToggleGroup enablingRemoveOptionTG;

	@FXML
	private CheckBox newTextCB;

	@FXML
	private TextField newTextTF;

	@FXML
	private CheckBox newAnswerCB;

	@FXML
	private TextArea newAnswerTA;

	@FXML
	private CheckBox newEventCB;

	@FXML
	private TextArea newEventTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeConversationOptionActionController(CurrentGameManager currentGameManager,
			MainWindowController mwController, ChangeConversationOptionAction action) {
		super(currentGameManager, mwController, action);
	}

	@Override
	protected void initialize() {
		super.initialize();

		link.setText("Changing: " + action.getOption().toString());
		link.setOnAction((e) -> {
			objectSelected(action.getOption());
		});

		initRadioButtonEnablingGroup(enablingOptionTG, doNotChangeOptionRB, enableOptionRB,
				disableOptionRB, action::getEnabling, action::setEnabling);
		initRadioButtonEnablingGroup(enablingRemoveOptionTG, doNotChangeRemoveOptionRB, enableRemoveOptionRB,
				disableOptionRB, action::getEnablingDisableOption, action::setEnablingDisableOption);
		initCheckBoxAndTextFieldSetter(newTextCB, newTextTF, action::getNewText, action::setNewText);
		initCheckBoxAndTextFieldSetter(newAnswerCB, newAnswerTA, action::getNewAnswer, action::setNewAnswer);
		initCheckBoxAndTextFieldSetter(newEventCB, newEventTA, action::getNewEvent, action::setNewEvent);
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
