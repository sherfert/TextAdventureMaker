package gui.itemEditing.action;

import data.action.ChangeConversationAction;
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
 * Controller for one {@link ChangeConversationAction}.
 * 
 * @author Satia
 */
public class ChangeConversationActionController extends ActionController<ChangeConversationAction> {

	@FXML
	private Hyperlink link;

	@FXML
	private RadioButton doNotChangeConversationRB;

	@FXML
	private RadioButton enableConversationRB;

	@FXML
	private RadioButton disableConversationRB;

	@FXML
	private ToggleGroup enablingConversationTG;

	@FXML
	private CheckBox newGreetingCB;

	@FXML
	private TextField newGreetingTF;

	@FXML
	private CheckBox newEventCB;

	@FXML
	private TextField newEventTF;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeConversationActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeConversationAction action) {
		super(currentGameManager, mwController, action);
	}

	@Override
	protected void initialize() {
		super.initialize();

		link.setText("Changing: " + action.getConversation().toString());
		link.setOnAction((e) -> {
			objectSelected(action.getConversation());
		});

		initRadioButtonEnablingGroup(enablingConversationTG, doNotChangeConversationRB, enableConversationRB,
				disableConversationRB, action::getEnabling, action::setEnabling);
		setNodeTooltip(enableConversationRB, "Triggering this action will enable the conversation.");
		setNodeTooltip(disableConversationRB, "Triggering this action will disable the conversation.");
		setNodeTooltip(doNotChangeConversationRB,
				"Triggering this action will not change if the conversation is enabled.");

		initCheckBoxAndTextFieldSetter(newGreetingCB, newGreetingTF, action::getNewGreeting, action::setNewGreeting);
		setNodeTooltip(newGreetingTF, "This will be the new greeting from the person.");
		setNodeTooltip(newGreetingCB, "If ticked, the greeting will change.");

		initCheckBoxAndTextFieldSetter(newEventCB, newEventTF, action::getNewEvent, action::setNewEvent);
		setNodeTooltip(newEventTF, "This will be the new description of events.");
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
