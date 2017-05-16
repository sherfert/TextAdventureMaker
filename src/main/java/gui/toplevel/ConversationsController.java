package gui.toplevel;

import java.util.List;

import data.Conversation;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsTableController;
import gui.wizards.NewConversationWizard;
import logic.CurrentGameManager;

/**
 * Controller for the conversations view.
 * 
 * @author Satia
 */
public class ConversationsController extends NamedObjectsTableController<Conversation> {

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
	protected List<Conversation> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getConversationManager().getAllConversations();
	}
	
	@Override
	public boolean isObsolete() {
		return false;
	}

	@Override
	protected void createObject() {
		new NewConversationWizard().showAndGet().ifPresent(this::saveObject);
	}

}
