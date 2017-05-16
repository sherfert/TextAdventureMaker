package gui.toplevel;

import java.util.List;

import data.Item;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
import gui.wizards.NewNamedObjectWizard;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for the items view.
 * 
 * @author Satia
 */
public class ItemsController extends NamedDescribedObjectsController<Item> {

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public ItemsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		newButton.setText("New item");
	}

	@Override
	protected List<Item> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getItemManager().getAllItems();
	}

	@Override
	public boolean isObsolete() {
		return false;
	}

	@Override
	protected void createObject() {
		new NewNamedObjectWizard("New item").showAndGetName().map(name -> new Item(name, ""))
				.ifPresent(this::saveObject);
	}

}
