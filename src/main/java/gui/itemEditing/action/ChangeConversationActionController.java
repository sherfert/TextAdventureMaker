package gui.itemEditing.action;

import data.action.ChangeConversationAction;
import data.action.AbstractAction.Enabling;
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
		
		switch(action.getEnabling()) {
		case DISABLE:
			enablingConversationTG.selectToggle(disableConversationRB);
			break;
		case DO_NOT_CHANGE:
			enablingConversationTG.selectToggle(doNotChangeConversationRB);
			break;
		case ENABLE:
			enablingConversationTG.selectToggle(enableConversationRB);
			break;
		}
		enablingConversationTG.selectedToggleProperty().addListener((f, o, n) -> {
			if(n == doNotChangeConversationRB) {
				action.setEnabling(Enabling.DO_NOT_CHANGE);
			} else if(n == enableConversationRB) {
				action.setEnabling(Enabling.ENABLE);
			} else if(n == disableConversationRB) {
				action.setEnabling(Enabling.DISABLE);
			}
		});
		// TODO abstract all these similar GUI things
		newGreetingTF.setText(action.getNewGreeting());
		newGreetingTF.disableProperty().bind(newGreetingCB.selectedProperty().not());
		newGreetingTF.textProperty().addListener((f, o, n) -> {
			action.setNewGreeting(n);
		});
		newGreetingCB.setSelected(action.getNewGreeting() != null);
		newGreetingCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewGreeting(null);
			}
		});

		newEventTF.setText(action.getNewEvent());
		newEventTF.disableProperty().bind(newEventCB.selectedProperty().not());
		newEventTF.textProperty().addListener((f, o, n) -> {
			action.setNewEvent(n);
		});
		newEventCB.setSelected(action.getNewEvent() != null);
		newEventCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewEvent(null);
			}
		});
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
