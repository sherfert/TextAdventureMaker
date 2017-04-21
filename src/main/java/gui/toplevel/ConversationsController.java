package gui.toplevel;

import java.util.List;

import data.Conversation;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for the conversations view.
 * 
 * @author Satia
 */
public class ConversationsController extends NamedObjectsController<Conversation> {

	@FXML
	private TextArea newGreetingTA;

	@FXML
	private TextArea newEventTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public ConversationsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}
	
	@Override
	protected void resetFormValues() {
		super.resetFormValues();
		newGreetingTA.setText("");
		newEventTA.setText("");
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		// Assure save is only enabled if there is a name and a greeting
		newNameTF.textProperty().addListener(
				(f, o, n) -> saveButton.setDisable(n.isEmpty() || newGreetingTA.textProperty().get().isEmpty()));
		newGreetingTA.textProperty().addListener(
				(f, o, n) -> saveButton.setDisable(n.isEmpty() || newNameTF.textProperty().get().isEmpty()));
	}

	@Override
	protected List<Conversation> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getConversationManager().getAllConversations();
	}

	@Override
	protected Conversation createNewObject(String name) {
		return new Conversation(name, newGreetingTA.getText(), newEventTA.getText());
	}
	
	@Override
	public boolean isObsolete() {
		return false;
	}

}
