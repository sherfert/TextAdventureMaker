package gui.toplevel;

import java.util.List;

import data.InventoryItem;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
import gui.wizards.NewNamedObjectWizard;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for the inventory items view.
 * 
 * @author Satia
 */
public class InventoryItemsController extends NamedDescribedObjectsController<InventoryItem> {

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public InventoryItemsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		newButton.setText("New inventory item");
	}

	@Override
	protected List<InventoryItem> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems();
	}

	@Override
	public boolean isObsolete() {
		return false;
	}

	@Override
	protected void createObject() {
		new NewNamedObjectWizard("New inventory item").showAndGetName().map(name -> new InventoryItem(name, ""))
				.ifPresent(this::saveObject);
	}

}
