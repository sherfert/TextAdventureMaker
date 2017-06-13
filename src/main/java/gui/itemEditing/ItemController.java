package gui.itemEditing;

import data.InventoryItem;
import data.Item;
import data.Location;
import data.action.AbstractAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.customui.NamedObjectListView;
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
	private NamedObjectChooser<Location> locationChooser;

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
	private NamedObjectListView<InventoryItem> pickUpItemsListView;

	@FXML
	private NamedObjectListView<AbstractAction> takeActionsListView;

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
		setNodeTooltip(locationChooser, "The location of the item. Choosing none is valid, if you want to introduce "
				+ "the item to the game later on using an action.");

		removeButton.setOnMouseClicked(
				(e) -> removeObject(item, "Deleting an item", "Do you really want to delete this item?",
						"This will delete the item, usage information of inventory items with this item, "
								+ "and actions associated with any of the deleted entities."));

		editTakeSuccessfulTextTF.textProperty().bindBidirectional(item.takeSuccessfulTextProperty());
		editTakeSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editTakeSuccessfulTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(editTakeSuccessfulTextTF,
				"This is the text when the item is successfully taken. If empty, the default will be used.",
				noSecondPL);

		editTakeForbiddenTextTF.textProperty().bindBidirectional(item.takeForbiddenTextProperty());
		editTakeForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editTakeForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(editTakeForbiddenTextTF,
				"This text is displayed when the player tries to take this item, unsuccessfully. If empty, the default will be used.",
				noSecondPL);

		editTakingEnabledCB.setSelected(item.isTakingEnabled());
		editTakingEnabledCB.selectedProperty().addListener((f, o, n) -> item.setTakingEnabled(n));
		setNodeTooltip(editTakingEnabledCB, "If ticked, the item can be taken.");

		editRemoveItemEnabledCB.setSelected(item.isRemoveItem());
		editRemoveItemEnabledCB.selectedProperty().addListener((f, o, n) -> item.setRemoveItem(n));
		setNodeTooltip(editTakingEnabledCB, "If ticked, the item will disappear from the room after taking it.");

		editTakeCommandsTA.setText(getCommandString(item.getAdditionalTakeCommands()));
		editTakeCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, true, editTakeCommandsTA, item::setAdditionalTakeCommands));
		addCommandTooltip(editTakeCommandsTA,
				"Additional commands to take the item. These will only be valid for this item.");

		pickUpItemsListView.initialize(item.getPickUpItems(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> item.addPickUpItem(ii), (ii) -> item.removePickUpItem(ii));

		takeActionsListView.initialize(item.getAdditionalTakeActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions,
				item::setAdditionalTakeActions, this::objectSelected, item::addAdditionalTakeAction,
				item::removeAdditionalTakeAction);

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

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(item);
	}
}
