package gui;

import data.Conversation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one conversation.
 * 
 * TODO Support to change layers, startLayer, additionalActions
 * 
 * @author Satia
 */
public class ConversationController extends GameDataController {

	/** The conversation */
	private Conversation conversation;

	@FXML
	private Button removeButton;
	
	@FXML
	private CheckBox editConversationEnabledCB;

	@FXML
	private TextField editNameTF;

	@FXML
	private TextArea editGreetingTA;

	@FXML
	private TextArea editEventTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param item
	 *            the item to edit
	 */
	public ConversationController(CurrentGameManager currentGameManager, MainWindowController mwController, Conversation conversation) {
		super(currentGameManager, mwController);
		this.conversation = conversation;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		removeButton.setOnMouseClicked((e) -> removeConversation());
		
		editConversationEnabledCB.setSelected(conversation.getEnabled());
		editConversationEnabledCB.selectedProperty().addListener((f, o, n) -> conversation.setEnabled(n));

		editNameTF.textProperty().bindBidirectional(conversation.nameProperty());
		editGreetingTA.textProperty().bindBidirectional(conversation.greetingProperty());
		editEventTA.textProperty().bindBidirectional(conversation.eventProperty());
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		return super.controllerFactory(type);
	}

	/**
	 * Removes a conversation from the DB.
	 */
	private void removeConversation() {
		// Show a confirmation dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Deleting a conversation");
		alert.setHeaderText("Do you really want to delete this conversation?");
		alert.setContentText("This will delete the conversation, all layers and options of the conversation, "
				+ "and actions associated with any of the deleted entities.");
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Remove item from DB
				currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(conversation);
				currentGameManager.getPersistenceManager().updateChanges();

				// Switch back to previous view
				mwController.popCenterContent();
			}
		});
	}
}
