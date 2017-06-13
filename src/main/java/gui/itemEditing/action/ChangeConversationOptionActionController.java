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

		initRadioButtonEnablingGroup(enablingOptionTG, doNotChangeOptionRB, enableOptionRB, disableOptionRB,
				action::getEnabling, action::setEnabling);
		setNodeTooltip(enableOptionRB, "Triggering this action will enable the conversation option.");
		setNodeTooltip(disableOptionRB, "Triggering this action will disable the conversation option.");
		setNodeTooltip(doNotChangeOptionRB,
				"Triggering this action will not change if the conversation option is enabled.");

		initRadioButtonEnablingGroup(enablingRemoveOptionTG, doNotChangeRemoveOptionRB, enableRemoveOptionRB,
				disableOptionRB, action::getEnablingDisableOption, action::setEnablingDisableOption);
		setNodeTooltip(enableRemoveOptionRB,
				"Triggering this action will enable removing the conversation option after it has been chosen.");
		setNodeTooltip(disableOptionRB,
				"Triggering this action will disable removing the conversation option after it has been chosen.");
		setNodeTooltip(doNotChangeRemoveOptionRB,
				"Triggering this action will not change if the conversation option is removed after it has been chosen.");

		initCheckBoxAndTextFieldSetter(newTextCB, newTextTF, action::getNewText, action::setNewText);
		setNodeTooltip(newTextTF, "This will be the new text the player is saying.");
		setNodeTooltip(newTextCB, "If ticked, the text the player is saying will change.");
		
		initCheckBoxAndTextFieldSetter(newAnswerCB, newAnswerTA, action::getNewAnswer, action::setNewAnswer);
		setNodeTooltip(newAnswerTA, "This will be the new response from the person.");
		setNodeTooltip(newAnswerCB, "If ticked, the response from the person will change.");
		
		initCheckBoxAndTextFieldSetter(newEventCB, newEventTA, action::getNewEvent, action::setNewEvent);
		setNodeTooltip(newEventTA, "This will be the new description of events.");
		setNodeTooltip(newEventCB, "If ticked, the events description will change.");
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
