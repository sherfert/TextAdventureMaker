package gui.itemEditing.action;

import data.action.ChangeCombineInformationAction;
import gui.MainWindowController;
import gui.customui.InventoryItemListView;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeCombineInformationAction}.
 * 
 * @author Satia
 */
public class ChangeCombineInformationActionController extends ActionController<ChangeCombineInformationAction> {

	@FXML
	private Hyperlink linkItem1;
	
	@FXML
	private Hyperlink linkItem2;
	
	@FXML
	private RadioButton doNotChangeCombinationRB;

	@FXML
	private RadioButton enableCombinationRB;

	@FXML
	private RadioButton disableCombinationRB;

	@FXML
	private ToggleGroup enablingCombinationTG;
	
	@FXML
	private RadioButton doNotChangeRemoveItemsRB;

	@FXML
	private RadioButton enableRemoveItemsRB;

	@FXML
	private RadioButton disableRemoveItemsRB;

	@FXML
	private ToggleGroup enablingRemoveItemsTG;
	
	@FXML
	private CheckBox newCombineWithSuccessfulTextCB;

	@FXML
	private TextField newCombineWithSuccessfulTextTF;

	@FXML
	private CheckBox newCombineWithForbiddenTextCB;

	@FXML
	private TextField newCombineWithForbiddenTextTF;
	
	@FXML
	private InventoryItemListView itemsAddListView;
	
	@FXML
	private InventoryItemListView itemsRemoveListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeCombineInformationActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeCombineInformationAction action) {
		super(currentGameManager, mwController, action);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		linkItem1.setText("Changing combination of: " + action.getInventoryItem1().toString());
		linkItem1.setOnAction((e) -> {
			objectSelected(action.getInventoryItem1());
		});
		
		linkItem2.setText("with: " + action.getInventoryItem2().toString());
		linkItem2.setOnAction((e) -> {
			objectSelected(action.getInventoryItem2());
		});
		
		initRadioButtonEnablingGroup(enablingCombinationTG, doNotChangeCombinationRB, enableCombinationRB, disableCombinationRB,
				action::getEnablingCombinable, action::setEnablingCombinable);
		
		initRadioButtonEnablingGroup(enablingRemoveItemsTG, doNotChangeRemoveItemsRB, enableRemoveItemsRB, disableRemoveItemsRB,
				action::getEnablingRemoveCombinables, action::setEnablingRemoveCombinables);
		
		initCheckBoxAndTextFieldSetter(newCombineWithSuccessfulTextCB, newCombineWithSuccessfulTextTF,
				action::getNewCombineWithSuccessfulText, action::setNewCombineWithSuccessfulText);
		initCheckBoxAndTextFieldSetter(newCombineWithForbiddenTextCB, newCombineWithForbiddenTextTF,
				action::getNewCombineWithForbiddenText, action::setNewCombineWithForbiddenText);
		
		itemsAddListView.initialize(action.getCombinablesToAdd(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addCombinableToAdd(ii), (ii) -> action.removeCombinableToAdd(ii));
		
		itemsRemoveListView.initialize(action.getCombinablesToRemove(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (ii) -> action.addCombinableToRemove(ii), (ii) -> action.removeCombinableToRemove(ii));
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
		} else {
			return super.controllerFactory(type);
		}
	}
}
