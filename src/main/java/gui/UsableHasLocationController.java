package gui;

import data.InventoryItem;
import data.interfaces.HasLocation;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the usage of an inventory item with a person or object.
 * 
 * TODO Support to change additionalUseWithActions
 * 
 * @author Satia
 */
public class UsableHasLocationController extends GameDataController {

	/** The inventory item that is being used */
	private InventoryItem item;

	/** The object it is being used with */
	private HasLocation object;

	@FXML
	private TabPane tabPane;

	@FXML
	private CheckBox editUsingEnabledCB;

	@FXML
	private TextField editUseWithSuccessfulTextTF;

	@FXML
	private TextField editUseWithForbiddenTextTF;

	@FXML
	private TextArea editUseWithCommandsTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param item
	 *            the inventory item that is being used
	 * @param object
	 *            the object it is being used with
	 * 
	 */
	public UsableHasLocationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			InventoryItem item, HasLocation object) {
		super(currentGameManager, mwController);
		this.item = item;
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		editUseWithSuccessfulTextTF.setText(item.getUseWithSuccessfulText(object));
		editUseWithSuccessfulTextTF.textProperty().addListener((f, o, n) -> item.setUseWithSuccessfulText(object, n));
		
		editUseWithForbiddenTextTF.setText(item.getUseWithForbiddenText(object));
		editUseWithForbiddenTextTF.textProperty().addListener((f, o, n) -> item.setUseWithForbiddenText(object, n));

		editUsingEnabledCB.setSelected(item.isUsingEnabledWith(object));
		editUsingEnabledCB.selectedProperty().addListener((f, o, n) -> item.setUsingEnabledWith(object, n));

		editUseWithCommandsTA.setText(getCommandString(item.getAdditionalUseWithCommands(object)));
		editUseWithCommandsTA.textProperty().addListener((f, o, n) -> updateGameCommands(n, 2, editUseWithCommandsTA,
				(cs) -> item.setAdditionalUseWithCommands(object, cs)));

		saveTabIndex(tabPane);
	}
}