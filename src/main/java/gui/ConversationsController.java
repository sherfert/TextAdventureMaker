package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Conversation;
import exception.DBClosedException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the conversations view.
 * 
 * @author Satia
 */
public class ConversationsController extends GameDataController {

	/** An observable list with the conversations. */
	private ObservableList<Conversation> conversationsOL;

	@FXML
	private TableView<Conversation> table;

	@FXML
	private TableColumn<Conversation, Integer> idCol;

	@FXML
	private TableColumn<Conversation, String> nameCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newGreetingTA;

	@FXML
	private TextArea newEventTA;

	@FXML
	private Button saveButton;

	public ConversationsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	private void initialize() {
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());

		// A listener for row double-clicks
		table.setRowFactory(tv -> {
			TableRow<Conversation> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					conversationSelected(row.getItem());
				}
			});
			return row;
		});

		// Get all conversations and store in observable list, unless the list
		// is already propagated
		if (conversationsOL == null) {
			try {
				conversationsOL = FXCollections.observableArrayList(
						currentGameManager.getPersistenceManager().getConversationManager().getAllConversations());
			} catch (DBClosedException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Abort: DB closed");
				return;
			}
		}

		// Fill table
		table.setItems(conversationsOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name and a greeting
		newNameTF.textProperty().addListener(
				(f, o, n) -> saveButton.setDisable(n.isEmpty() || newGreetingTA.textProperty().get().isEmpty()));
		newGreetingTA.textProperty().addListener(
				(f, o, n) -> saveButton.setDisable(n.isEmpty() || newNameTF.textProperty().get().isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewConversation());
	}

	@Override
	public void update() {
		if (conversationsOL != null) {
			try {
				conversationsOL
						.setAll(currentGameManager.getPersistenceManager().getConversationManager().getAllConversations());
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Abort: DB closed");
				return;
			}
		}
	}

	/**
	 * Saves a new conversation to both DB and table.
	 */
	private void saveNewConversation() {
		Conversation c = new Conversation(newNameTF.getText(), newGreetingTA.getText(), newEventTA.getText());
		// Add item to DB
		try {
			currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(c);
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Abort: DB closed");
			return;
		}
		currentGameManager.getPersistenceManager().updateChanges();
		// Add location to our table
		conversationsOL.add(c);

		// Reset the form values
		newNameTF.setText("");
		newGreetingTA.setText("");
		newEventTA.setText("");
	}

	/**
	 * Opens this conversation for editing.
	 * 
	 * @param c
	 *            the conversation
	 */
	private void conversationSelected(Conversation c) {
		if (c == null) {
			return;
		}

		// Open the conversations view
		ConversationController conversationController = new ConversationController(currentGameManager, mwController, c);
		mwController.pushCenterContent(c.getName(), "view/Conversation.fxml", conversationController,
				conversationController::controllerFactory);
	}

}
