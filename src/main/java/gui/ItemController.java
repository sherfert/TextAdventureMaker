package gui;

import data.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one item.
 * 
 * TODO Support to change removeItem, location, additionalTakeActions,
 * pickUpItems
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
	private CheckBox editRemoveItemEnabledCB;

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
				(f, o, n) -> updateGameCommands(n, 1, editTakeCommandsTA, item::setAdditionalTakeCommands));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedDescribedObjectController.class) {
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
