package gui.toplevel;

import java.util.List;

import data.Item;
import exception.DBClosedException;
import gui.GameDataController;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
import gui.itemEditing.ItemController;
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
		super(currentGameManager, mwController, "view/Item.fxml");
	}

	@Override
	protected List<Item> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getItemManager().getAllItems();
	}

	@Override
	protected Item createNewObject(String name, String description) {
		return new Item(name, description);
	}

	@Override
	protected GameDataController getObjectController(Item selectedObject) {
		return new ItemController(currentGameManager, mwController, selectedObject);
	}

}
