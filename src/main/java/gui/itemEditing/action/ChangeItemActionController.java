package gui.itemEditing.action;

import data.action.ChangeItemAction;
import data.action.AbstractAction.Enabling;
import gui.MainWindowController;
import gui.customui.InventoryItemListView;
import gui.customui.LocationChooser;
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
	private LocationChooser newLocationChooser;
	
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
	private InventoryItemListView pickUpItemsAddListView;
	
	@FXML
	private InventoryItemListView pickUpItemsRemoveListView;

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
		newLocationChooser.disableProperty().bind(newLocationCB.selectedProperty().not());
		newLocationCB.setSelected(action.getChangeLocation());
		newLocationCB.selectedProperty().addListener((f, o, n) -> {
			action.setChangeLocation(n);
		});
		
		switch(action.getEnablingTakeable()) {
		case DISABLE:
			enablingTakeTG.selectToggle(disableTakeRB);
			break;
		case DO_NOT_CHANGE:
			enablingTakeTG.selectToggle(doNotChangeTakeRB);
			break;
		case ENABLE:
			enablingTakeTG.selectToggle(enableTakeRB);
			break;
		}
		enablingTakeTG.selectedToggleProperty().addListener((f, o, n) -> {
			if(n == doNotChangeTakeRB) {
				action.setEnablingTakeable(Enabling.DO_NOT_CHANGE);
			} else if(n == enableTakeRB) {
				action.setEnablingTakeable(Enabling.ENABLE);
			} else if(n == disableTakeRB) {
				action.setEnablingTakeable(Enabling.DISABLE);
			}
		});
		
		switch(action.getEnablingRemoveItem()) {
		case DISABLE:
			enablingRemoveItemTG.selectToggle(disableRemoveItemRB);
			break;
		case DO_NOT_CHANGE:
			enablingRemoveItemTG.selectToggle(doNotChangeRemoveItemRB);
			break;
		case ENABLE:
			enablingRemoveItemTG.selectToggle(enableRemoveItemRB);
			break;
		}
		enablingRemoveItemTG.selectedToggleProperty().addListener((f, o, n) -> {
			if(n == doNotChangeRemoveItemRB) {
				action.setEnablingRemoveItem(Enabling.DO_NOT_CHANGE);
			} else if(n == enableRemoveItemRB) {
				action.setEnablingRemoveItem(Enabling.ENABLE);
			} else if(n == disableRemoveItemRB) {
				action.setEnablingRemoveItem(Enabling.DISABLE);
			}
		});

		newTakeSuccessfulTextTF.setText(action.getNewTakeSuccessfulText());
		newTakeSuccessfulTextTF.disableProperty().bind(newTakeSuccessfulTextCB.selectedProperty().not());
		newTakeSuccessfulTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewTakeSuccessfulText(n);
		});
		newTakeSuccessfulTextCB.setSelected(action.getNewTakeSuccessfulText() != null);
		newTakeSuccessfulTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewTakeSuccessfulText(null);
			}
		});

		newTakeForbiddenTextTF.setText(action.getNewTakeForbiddenText());
		newTakeForbiddenTextTF.disableProperty().bind(newTakeForbiddenTextCB.selectedProperty().not());
		newTakeForbiddenTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewTakeForbiddenText(n);
		});
		newTakeForbiddenTextCB.setSelected(action.getNewTakeForbiddenText() != null);
		newTakeForbiddenTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewTakeForbiddenText(null);
			}
		});

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
