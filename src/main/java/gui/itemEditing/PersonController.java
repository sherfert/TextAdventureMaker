package gui.itemEditing;

import data.Conversation;
import data.Location;
import data.Person;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.include.InspectableObjectController;
import gui.include.NamedDescribedObjectController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private NamedObjectChooser<Location> locationChooser;

	@FXML
	private NamedObjectChooser<Conversation> conversationChooser;

	@FXML
	private Button removeButton;

	@FXML
	private TextField editTalkForbiddenTextTF;

	@FXML
	private TextArea editTalkCommandsTA;

	@FXML
	private Label talkCommandsLabel;

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
		setNodeTooltip(locationChooser, "The location of the person. Choosing none is valid, if you want to introduce "
				+ "the person to the game later on using an action.");

		conversationChooser.initialize(person.getConversation(), true, false,
				this.currentGameManager.getPersistenceManager().getConversationManager()::getAllConversations,
				person::setConversation);
		setNodeTooltip(conversationChooser, "The conversation associated with the person.");

		removeButton.setOnMouseClicked(
				(e) -> removeObject(person, "Deleting a person", "Do you really want to delete this person?",
						"This will delete the person, and actions associated with any of the deleted entities."));
		editTalkForbiddenTextTF.textProperty().bindBidirectional(person.talkingToForbiddenTextProperty());
		editTalkForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editTalkForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(editTalkForbiddenTextTF,
				"This is the text when the player tries to talk to this person, unsuccessfully.  If empty, the default will be used.",
				noSecondPL);

		editTalkCommandsTA.setText(getCommandString(person.getAdditionalTalkToCommands()));
		editTalkCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, true, editTalkCommandsTA, person::setAdditionalTalkToCommands));
		addCommandTooltip(editTalkCommandsTA,
				"Additional commands to talk to the person. These will only be valid for this person.");

		talkCommandsLabel.setText("Additional commands for talking to " + person.getName());

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

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(person);
	}
}
