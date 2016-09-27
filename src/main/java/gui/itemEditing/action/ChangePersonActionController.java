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

		newConversationChooser.initialize(action.getNewConversation(), true, false,
				this.currentGameManager.getPersistenceManager().getConversationManager()::getAllConversations,
				action::setNewConversation);
		initCheckBoxAndChooser(newConversationCB, newConversationChooser, action::getChangeConversation,
				action::setChangeConversation);

		newLocationChooser.initialize(action.getNewLocation(), true, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewLocation);
		initCheckBoxAndChooser(newLocationCB, newLocationChooser, action::getChangeLocation, action::setChangeLocation);
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
