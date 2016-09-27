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
		
		initRadioButtonEnablingGroup(enablingTakeTG, doNotChangeTakeRB, enableTakeRB, disableTakeRB,
				action::getEnablingTakeable, action::setEnablingTakeable);
		initRadioButtonEnablingGroup(enablingRemoveItemTG, doNotChangeRemoveItemRB, enableRemoveItemRB, disableRemoveItemRB,
				action::getEnablingRemoveItem, action::setEnablingRemoveItem);
		
		initCheckBoxAndTextFieldSetter(newTakeSuccessfulTextCB, newTakeSuccessfulTextTF,
				action::getNewTakeSuccessfulText, action::setNewTakeSuccessfulText);
		initCheckBoxAndTextFieldSetter(newTakeForbiddenTextCB, newTakeForbiddenTextTF,
				action::getNewTakeForbiddenText, action::setNewTakeForbiddenText);

		pickUpItemsAddListView.initialize(action.getPickUpItemsToAdd(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addPickUpItemToAdd(ii), (ii) -> action.removePickUpItemToAdd(ii));
		
		pickUpItemsRemoveListView.initialize(action.getPickUpItemsToRemove(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addPickUpItemToRemove(ii), (ii) -> action.removePickUpItemToRemove(ii));
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
