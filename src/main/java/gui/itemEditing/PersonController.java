package gui.itemEditing;

import data.Person;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.ConversationChooser;
import gui.customui.LocationChooser;
import gui.include.InspectableObjectController;
import gui.include.NamedDescribedObjectController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one person.
 * 
 * @author Satia
 */
public class PersonController extends GameDataController {

	/** The person */
	private Person person;
	
	@FXML
	private TabPane tabPane;

	@FXML
	private LocationChooser locationChooser;

	@FXML
	private ConversationChooser conversationChooser;

	@FXML
	private Button removeButton;

	@FXML
	private TextField editTalkForbiddenTextTF;

	@FXML
	private TextArea editTalkCommandsTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param person
	 *            the person to edit
	 */
	public PersonController(CurrentGameManager currentGameManager, MainWindowController mwController, Person person) {
		super(currentGameManager, mwController);
		this.person = person;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		locationChooser.initialize(person.getLocation(), true, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				person::setLocation);

		conversationChooser.initialize(person.getConversation(), true, false,
				this.currentGameManager.getPersistenceManager().getConversationManager()::getAllConversations,
				person::setConversation);

		removeButton.setOnMouseClicked(
				(e) -> removeObject(person, "Deleting a person", "Do you really want to delete this person?",
						"This will delete the person, and actions associated with any of the deleted entities."));
		editTalkForbiddenTextTF.textProperty().bindBidirectional(person.talkingToForbiddenTextProperty());

		editTalkCommandsTA.setText(getCommandString(person.getAdditionalTalkToCommands()));
		editTalkCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, true, editTalkCommandsTA, person::setAdditionalTalkToCommands));
		
		saveTabIndex(tabPane);
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, person);
		} else if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, person);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, person);
		} else {
			return super.controllerFactory(type);
		}
	}
}
