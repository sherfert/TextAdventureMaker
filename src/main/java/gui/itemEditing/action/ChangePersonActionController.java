package gui.itemEditing.action;

import data.action.ChangePersonAction;
import gui.MainWindowController;
import gui.customui.ConversationChooser;
import gui.customui.LocationChooser;
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
	private ConversationChooser newConversationChooser;

	@FXML
	private CheckBox newLocationCB;

	@FXML
	private LocationChooser newLocationChooser;


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

		newTalkForbiddenTextTF.setText(action.getNewTalkingToForbiddenText());
		newTalkForbiddenTextTF.disableProperty().bind(newTalkForbiddenTextCB.selectedProperty().not());
		newTalkForbiddenTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewTalkingToForbiddenText(n);
		});
		newTalkForbiddenTextCB.setSelected(action.getNewTalkingToForbiddenText() != null);
		newTalkForbiddenTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewTalkingToForbiddenText(null);
			}
		});

		newConversationChooser.initialize(action.getNewConversation(), true, false,
				this.currentGameManager.getPersistenceManager().getConversationManager()::getAllConversations,
				action::setNewConversation);
		newConversationChooser.disableProperty().bind(newConversationCB.selectedProperty().not());
		newConversationCB.setSelected(action.getChangeConversation());
		newConversationCB.selectedProperty().addListener((f, o, n) -> {
			action.setChangeConversation(n);
		});

		newLocationChooser.initialize(action.getNewLocation(), true, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewLocation);
		newLocationChooser.disableProperty().bind(newLocationCB.selectedProperty().not());
		newLocationCB.setSelected(action.getChangeLocation());
		newLocationCB.selectedProperty().addListener((f, o, n) -> {
			action.setChangeLocation(n);
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
		} else if (type == ChangeNDObjectActionController.class) {
			return new ChangeNDObjectActionController(currentGameManager, mwController, action);
		} else if (type == ChangeInspectableObjectActionController.class) {
			return new ChangeInspectableObjectActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
