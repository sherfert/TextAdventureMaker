package gui;

import data.InventoryItem;
import data.interfaces.HasLocation;
import gui.custumui.ItemListView;
import gui.custumui.PersonListView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import logic.CurrentGameManager;

/**
 * Controller for one inventory item.
 * 
 * TODO Support to change additionalCombineCommands, combinableInvItems
 * 
 * @author Satia
 */
public class InventoryItemController extends GameDataController {

	/** The inventory item */
	private InventoryItem invitem;
	
	@FXML
	private TabPane tabPane;

	@FXML
	private Button removeButton;
	
	@FXML
	private ItemListView usableItemsListView;
	
	@FXML
	private PersonListView usablePersonsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param item
	 *            the inventory item to edit
	 */
	public InventoryItemController(CurrentGameManager currentGameManager, MainWindowController mwController, InventoryItem item) {
		super(currentGameManager, mwController);
		this.invitem = item;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		removeButton.setOnMouseClicked(
				(e) -> removeObject(invitem, "Deleting an invenory item", "Do you really want to delete this inventory item?",
						"This will delete the inventory item, usage information with other inventory items, items and persons, "
								+ "and actions associated with any of the deleted entities."));
		
		usableItemsListView.initialize(invitem.getItemsUsableWith(),
				this.currentGameManager.getPersistenceManager().getItemManager()::getAllItems, null,
				this::usableSelected, (i) -> invitem.ensureHasUsageInformation(i), null);
		
		usablePersonsListView.initialize(invitem.getPersonsUsableWith(),
				this.currentGameManager.getPersistenceManager().getPersonManager()::getAllPersons, null,
				this::usableSelected, (i) -> invitem.ensureHasUsageInformation(i), null);
		
		saveTabIndex(tabPane);
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, invitem);
		} else if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, invitem);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, invitem);
		} else if (type == UsableObjectController.class) {
			return new UsableObjectController(currentGameManager, mwController, invitem);
		} else {
			return super.controllerFactory(type);
		}
	}
	


	/**
	 * Opens the usage information for editing. Invoked when an item from the list is double
	 * clicked.
	 * 
	 * @param o
	 *            the object
	 */
	private void usableSelected(HasLocation o) {
		if (o == null) {
			return;
		}

		UsableHasLocationController controller = new UsableHasLocationController(currentGameManager, mwController, invitem, o);
		mwController.pushCenterContent("when used with " + o.getName(), "view/UsableHasLocation.fxml", controller,
				controller::controllerFactory);
	}
}
