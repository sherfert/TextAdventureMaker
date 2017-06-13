package gui.itemEditing.action;

import data.InventoryItem;
import data.Location;
import data.action.ChangeItemAction;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.customui.NamedObjectListView;
import gui.include.AbstractActionController;
import gui.include.ChangeInspectableObjectActionController;
import gui.include.ChangeNDObjectActionController;
import gui.include.ChangeUsableObjectActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeItemAction}.
 * 
 * @author Satia
 */
public class ChangeItemActionController extends ActionController<ChangeItemAction> {
	
	@FXML
	private TabPane tabPane;

	@FXML
	private CheckBox newLocationCB;

	@FXML
	private NamedObjectChooser<Location> newLocationChooser;

	@FXML
	private ToggleGroup enablingTakeTG;

	@FXML
	private RadioButton doNotChangeTakeRB;

	@FXML
	private RadioButton enableTakeRB;

	@FXML
	private RadioButton disableTakeRB;

	@FXML
	private ToggleGroup enablingRemoveItemTG;

	@FXML
	private RadioButton doNotChangeRemoveItemRB;

	@FXML
	private RadioButton enableRemoveItemRB;

	@FXML
	private RadioButton disableRemoveItemRB;

	@FXML
	private CheckBox newTakeSuccessfulTextCB;

	@FXML
	private TextField newTakeSuccessfulTextTF;

	@FXML
	private CheckBox newTakeForbiddenTextCB;

	@FXML
	private TextField newTakeForbiddenTextTF;

	@FXML
	private NamedObjectListView<InventoryItem> pickUpItemsAddListView;

	@FXML
	private NamedObjectListView<InventoryItem> pickUpItemsRemoveListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeItemActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeItemAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();

		newLocationChooser.initialize(action.getNewLocation(), true, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations,
				action::setNewLocation);
		initCheckBoxAndChooser(newLocationCB, newLocationChooser, action::getChangeLocation, action::setChangeLocation);
		setNodeTooltip(newLocationChooser, "This will be the new location of the item.");
		setNodeTooltip(newLocationCB, "If ticked, the location of the item will change.");

		initRadioButtonEnablingGroup(enablingTakeTG, doNotChangeTakeRB, enableTakeRB, disableTakeRB,
				action::getEnablingTakeable, action::setEnablingTakeable);
		setNodeTooltip(enableTakeRB, "Triggering this action will enable taking the item.");
		setNodeTooltip(disableTakeRB, "Triggering this action will disable taking the item.");
		setNodeTooltip(doNotChangeTakeRB, "Triggering this action will not change if the item can be taken.");

		initRadioButtonEnablingGroup(enablingRemoveItemTG, doNotChangeRemoveItemRB, enableRemoveItemRB,
				disableRemoveItemRB, action::getEnablingRemoveItem, action::setEnablingRemoveItem);
		setNodeTooltip(enableRemoveItemRB,
				"Triggering this action will enable removing the item after it has been taken.");
		setNodeTooltip(disableRemoveItemRB,
				"Triggering this action will disable removing the item after it has been taken.");
		setNodeTooltip(doNotChangeRemoveItemRB,
				"Triggering this action will not change if the item is removed after it has been taken.");

		initCheckBoxAndTextFieldSetter(newTakeSuccessfulTextCB, newTakeSuccessfulTextTF,
				action::getNewTakeSuccessfulText, action::setNewTakeSuccessfulText);
		newTakeSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newTakeSuccessfulTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newTakeSuccessfulTextTF, "This will be the new text when the player takes the item.",
				noSecondPL);
		setNodeTooltip(newTakeSuccessfulTextCB, "If ticked, the text displayed when the item was taken will change.");

		initCheckBoxAndTextFieldSetter(newTakeForbiddenTextCB, newTakeForbiddenTextTF, action::getNewTakeForbiddenText,
				action::setNewTakeForbiddenText);
		newTakeForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, newTakeForbiddenTextTF, noSecondPL, true));
		addPlaceholderTextTooltip(newTakeForbiddenTextTF,
				"This will be the new text when the player tries to take the item, unsuccessfully.", noSecondPL);
		setNodeTooltip(newTakeForbiddenTextCB,
				"If ticked, the text displayed when the item could not be taken will change.");

		pickUpItemsAddListView.initialize(action.getPickUpItemsToAdd(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addPickUpItemToAdd(ii), (ii) -> action.removePickUpItemToAdd(ii));

		pickUpItemsRemoveListView.initialize(action.getPickUpItemsToRemove(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addPickUpItemToRemove(ii),
				(ii) -> action.removePickUpItemToRemove(ii));
		
		saveTabIndex(tabPane);
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, action);
		} else if (type == AbstractActionController.class) {
			return new AbstractActionController(currentGameManager, mwController, action);
		} else if (type == ChangeNDObjectActionController.class) {
			return new ChangeNDObjectActionController(currentGameManager, mwController, action);
		} else if (type == ChangeInspectableObjectActionController.class) {
			return new ChangeInspectableObjectActionController(currentGameManager, mwController, action);
		} else if (type == ChangeUsableObjectActionController.class) {
			return new ChangeUsableObjectActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
