package gui.itemEditing;

import data.Item;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.ActionListView;
import gui.customui.InventoryItemListView;
import gui.customui.LocationChooser;
import gui.include.InspectableObjectController;
import gui.include.NamedDescribedObjectController;
import gui.include.NamedObjectController;
import gui.include.UsableObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one item.
 * 
 * @author Satia
 */
public class ItemController extends GameDataController {

	/** The item */
	private Item item;

	@FXML
	private TabPane tabPane;

	@FXML
	private LocationChooser locationChooser;

	@FXML
	private Button removeButton;

	@FXML
	private CheckBox editTakingEnabledCB;

	@FXML
	private CheckBox editRemoveItemEnabledCB;

	@FXML
	private TextField editTakeSuccessfulTextTF;

	@FXML
	private TextField editTakeForbiddenTextTF;

	@FXML
	private Label takeCommandsLabel;

	@FXML
	private TextArea editTakeCommandsTA;

	@FXML
	private InventoryItemListView pickUpItemsListView;

	@FXML
	private ActionListView takeActionsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param item
	 *            the item to edit
	 */
	public ItemController(CurrentGameManager currentGameManager, MainWindowController mwController, Item item) {
		super(currentGameManager, mwController);
		this.item = item;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		locationChooser.initialize(item.getLocation(), true, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				item::setLocation);

		removeButton.setOnMouseClicked(
				(e) -> removeObject(item, "Deleting an item", "Do you really want to delete this item?",
						"This will delete the item, usage information of inventory items with this item, "
								+ "and actions associated with any of the deleted entities."));
		editTakeSuccessfulTextTF.textProperty().bindBidirectional(item.takeSuccessfulTextProperty());
		editTakeForbiddenTextTF.textProperty().bindBidirectional(item.takeForbiddenTextProperty());

		editTakingEnabledCB.setSelected(item.isTakingEnabled());
		editTakingEnabledCB.selectedProperty().addListener((f, o, n) -> item.setTakingEnabled(n));

		editRemoveItemEnabledCB.setSelected(item.isRemoveItem());
		editRemoveItemEnabledCB.selectedProperty().addListener((f, o, n) -> item.setRemoveItem(n));

		editTakeCommandsTA.setText(getCommandString(item.getAdditionalTakeCommands()));
		editTakeCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, true, editTakeCommandsTA, item::setAdditionalTakeCommands));

		pickUpItemsListView.initialize(item.getPickUpItems(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> item.addPickUpItem(ii), (ii) -> item.removePickUpItem(ii));

		takeActionsListView.initialize(item.getAdditionalTakeActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions, null,
				this::objectSelected, (a) -> item.addAdditionalTakeAction(a),
				(a) -> item.removeAdditionalTakeAction(a));

		takeCommandsLabel.setText("Additional commands for taking " + item.getName());

		saveTabIndex(tabPane);
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, item);
		} else if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, item);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, item);
		} else if (type == UsableObjectController.class) {
			return new UsableObjectController(currentGameManager, mwController, item);
		} else {
			return super.controllerFactory(type);
		}
	}
}
