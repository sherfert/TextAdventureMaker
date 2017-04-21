package gui.itemEditing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import data.InventoryItem;
import data.interfaces.PassivelyUsable;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectListView;
import gui.include.InspectableObjectController;
import gui.include.NamedDescribedObjectController;
import gui.include.NamedObjectController;
import gui.include.UsableObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import logic.CurrentGameManager;

/**
 * Controller for one inventory item.
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
	private NamedObjectListView<PassivelyUsable> usableObjectsListView;

	@FXML
	private NamedObjectListView<InventoryItem> usableInvItemsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param item
	 *            the inventory item to edit
	 */
	public InventoryItemController(CurrentGameManager currentGameManager, MainWindowController mwController,
			InventoryItem item) {
		super(currentGameManager, mwController);
		this.invitem = item;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		removeButton.setOnMouseClicked((e) -> removeObject(invitem, "Deleting an invenory item",
				"Do you really want to delete this inventory item?",
				"This will delete the inventory item, usage information with other inventory items, items and persons, "
						+ "and actions associated with any of the deleted entities."));

		usableObjectsListView.initialize(invitem.getObjectsUsableWith(), () -> {
			List<PassivelyUsable> allObjects = new ArrayList<>();
			allObjects.addAll(this.currentGameManager.getPersistenceManager().getItemManager().getAllItems());
			allObjects.addAll(this.currentGameManager.getPersistenceManager().getPersonManager().getAllPersons());
			allObjects.addAll(this.currentGameManager.getPersistenceManager().getWayManager().getAllWays());
			return allObjects;
		} , null, this::usableSelected, (i) -> invitem.ensureHasUsageInformation(i), null);

		usableInvItemsListView.initialize(invitem.getInventoryItemsCombinableWith(),
				() -> this.currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems()
						.stream().filter((i) -> invitem != i).collect(Collectors.toList()),
				null, this::combinableSelected, (i) -> invitem.ensureHasCombineInformation(i), null);

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
	 * Opens the usage information for editing. Invoked when an item from the
	 * list is double clicked.
	 * 
	 * @param o
	 *            the object
	 */
	private void usableSelected(PassivelyUsable o) {
		if (o == null) {
			return;
		}

		UseWithInformationController controller = new UseWithInformationController(currentGameManager, mwController,
				invitem, o);
		mwController.pushCenterContent("when used with " + o.getName(), "view/itemEditing/UseWithInformation.fxml", controller,
				controller::controllerFactory);
	}

	/**
	 * Opens the combining information for editing. Invoked when an item from
	 * the list is double clicked.
	 * 
	 * @param i
	 *            the other item
	 */
	private void combinableSelected(InventoryItem i) {
		if (i == null) {
			return;
		}

		CombinationInformationController controller = new CombinationInformationController(currentGameManager, mwController,
				invitem, i);
		mwController.pushCenterContent("when combined with " + i.getName(), "view/itemEditing/CombinationInformation.fxml", controller,
				controller::controllerFactory);
	}
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(invitem);
	}
}
