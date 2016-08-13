package gui;

import data.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one person.
 * 
 * TODO Support to change location, conversation
 * 
 * @author Satia
 */
public class PersonController extends GameDataController {

	/** The person */
	private Person person;

	@FXML
	private Button removeButton;

	@FXML
	private TextField editTalkForbiddenTextTF;

	@FXML
	private TextArea editTalkCommandsTA;

	/**
	 * @param currentGameManager
	 *            the game manager
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
		removeButton.setOnMouseClicked((e) -> removePerson());
		editTalkForbiddenTextTF.textProperty().bindBidirectional(person.talkingToForbiddenTextProperty());

		editTalkCommandsTA.setText(getCommandString(person.getAdditionalTalkToCommands()));
		editTalkCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, editTalkCommandsTA, person::setAdditionalTalkToCommands));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, person);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, person);
		}  else {
			return super.controllerFactory(type);
		}
	}

	/**
	 * Removes a person from the DB.
	 */
	private void removePerson() {
		// Show a confirmation dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Deleting a person");
		alert.setHeaderText("Do you really want to delete this person?");
		// TODO what gets deleted
		alert.setContentText("This will delete the person, "
				+ "and actions associated with any of the deleted entities.");
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Remove person from DB
				currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(person);
				currentGameManager.getPersistenceManager().updateChanges();

				// Switch back to previous view
				mwController.popCenterContent();
			}
		});
	}
}
