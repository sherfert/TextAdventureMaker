package gui.itemEditing.action;

import data.Conversation;
import data.Location;
import data.action.ChangePersonAction;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.include.AbstractActionController;
import gui.include.ChangeInspectableObjectActionController;
import gui.include.ChangeNDObjectActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangePersonAction}.
 * 
 * @author Satia
 */
public class ChangePersonActionController extends ActionController<ChangePersonAction> {

	@FXML
	private CheckBox newTalkForbiddenTextCB;

	@FXML
	private TextField newTalkForbiddenTextTF;

	@FXML
	private CheckBox newConversationCB;

	@FXML
	private NamedObjectChooser<Conversation> newConversationChooser;

	@FXML
	private CheckBox newLocationCB;

	@FXML
	private NamedObjectChooser<Location> newLocationChooser;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangePersonActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangePersonAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();

		initCheckBoxAndTextFieldSetter(newTalkForbiddenTextCB, newTalkForbiddenTextTF,
				action::getNewTalkingToForbiddenText, action::setNewTalkingToForbiddenText);
		newTalkForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newTalkForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newTalkForbiddenTextTF,
				"This will be the new text when the player tries to talk to the person, unsuccessfully.", noSecondPL);
		setNodeTooltip(newTalkForbiddenTextCB,
				"If ticked, the text displayed when the person could not be talked to will change.");

		newConversationChooser.initialize(action.getNewConversation(), true, false,
				this.currentGameManager.getPersistenceManager().getConversationManager()::getAllConversations,
				action::setNewConversation);
		initCheckBoxAndChooser(newConversationCB, newConversationChooser, action::getChangeConversation,
				action::setChangeConversation);
		setNodeTooltip(newConversationChooser, "This will be the new conversation of the person.");
		setNodeTooltip(newConversationCB, "If ticked, the conversation of the person will change.");

		newLocationChooser.initialize(action.getNewLocation(), true, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewLocation);
		initCheckBoxAndChooser(newLocationCB, newLocationChooser, action::getChangeLocation, action::setChangeLocation);
		setNodeTooltip(newLocationChooser, "This will be the new location of the person.");
		setNodeTooltip(newLocationCB, "If ticked, the location of the person will change.");
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
		} else if (type == ChangeNDObjectActionController.class) {
			return new ChangeNDObjectActionController(currentGameManager, mwController, action);
		} else if (type == ChangeInspectableObjectActionController.class) {
			return new ChangeInspectableObjectActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
