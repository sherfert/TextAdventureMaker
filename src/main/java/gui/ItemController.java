package gui;

import data.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one item.
 * 
 * TODO Support to change removeItem, location, additionalTakeActions, pickUpItems
 * 
 * @author Satia
 */
public class ItemController extends GameDataController {

	/** The item */
	private Item item;

	@FXML
	private Button removeButton;
	
	@FXML
	private CheckBox editTakingEnabledCB;

	@FXML
	private TextField editTakeSuccessfulTextTF;

	@FXML
	private TextField editTakeForbiddenTextTF;

	@FXML
	private TextArea editTakeCommandsTA;

	/**
	 * @param currentGameManager
	 *            the game manager
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
		removeButton.setOnMouseClicked((e) -> removeItem());
		editTakeSuccessfulTextTF.textProperty().bindBidirectional(item.takeSuccessfulTextProperty());
		editTakeForbiddenTextTF.textProperty().bindBidirectional(item.takeForbiddenTextProperty());
		
		editTakingEnabledCB.setSelected(item.isTakingEnabled());
		editTakingEnabledCB.selectedProperty().addListener((f, o, n) -> item.setTakingEnabled(n));

		editTakeCommandsTA.setText(getCommandString(item.getAdditionalTakeCommands()));
		editTakeCommandsTA.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, editTakeCommandsTA, item::setAdditionalTakeCommands));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, item);
		} else if (type == InspectableObjectController.class) {
			return new InspectableObjectController(currentGameManager, mwController, item);
		} else if (type == UsableObjectController.class) {
			return new UsableObjectController(currentGameManager, mwController, item);
		} else {
			return super.controllerFactory(type);
		}
	}

	/**
	 * Removes an item from the DB.
	 */
	private void removeItem() {
		// Show a confirmation dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Deleting an item");
		alert.setHeaderText("Do you really want to delete this item?");
		alert.setContentText("This will delete the item, usage information of inventory items with this item, "
				+ "and actions associated with any of the deleted entities.");
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Remove item from DB
				currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(item);
				currentGameManager.getPersistenceManager().updateChanges();

				// Switch back to previous view
				mwController.popCenterContent();
			}
		});
	}
}
