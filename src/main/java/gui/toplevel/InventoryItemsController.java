package gui.toplevel;

import java.util.List;

import data.InventoryItem;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
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

	@Override
	protected List<InventoryItem> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems();
	}

	@Override
	protected InventoryItem createNewObject(String name, String description) {
		return new InventoryItem(name, description);
	}

}
