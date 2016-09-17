package gui;

import data.InventoryItem;
import gui.custumui.InventoryItemListView;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one the combination of two inventory items.
 * 
 * TODO Support to change 2xcombine commands, additionalCombineWithActions
 * 
 * @author Satia
 */
public class CombinableInvItemController extends GameDataController {

	/** The first inventory item */
	private InventoryItem item1;

	/** The second inventory item */
	private InventoryItem item2;

	@FXML
	private TabPane tabPane;

	@FXML
	private CheckBox editCombiningEnabledCB;

	@FXML
	private CheckBox editRemoveItemsCB;

	@FXML
	private TextField editCombineSuccessfulTextTF;

	@FXML
	private TextField editCombineForbiddenTextTF;

	@FXML
	private InventoryItemListView newItemsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param item1
	 *            the first inventory item
	 * @param second
	 *            the first inventory item
	 */
	public CombinableInvItemController(CurrentGameManager currentGameManager, MainWindowController mwController,
			InventoryItem item1, InventoryItem item2) {
		super(currentGameManager, mwController);
		this.item1 = item1;
		this.item2 = item2;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		editCombineSuccessfulTextTF.setText(item1.getCombineWithSuccessfulText(item2));
		editCombineSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> item1.setCombineWithSuccessfulText(item2, n));

		editCombineForbiddenTextTF.setText(item1.getCombineWithForbiddenText(item2));
		editCombineForbiddenTextTF.textProperty().addListener((f, o, n) -> item1.setCombineWithForbiddenText(item2, n));

		editCombiningEnabledCB.setSelected(item1.isCombiningEnabledWith(item2));
		editCombiningEnabledCB.selectedProperty().addListener((f, o, n) -> item1.setCombiningEnabledWith(item2, n));

		editRemoveItemsCB.setSelected(item1.getRemoveCombinablesWhenCombinedWith(item2));
		editRemoveItemsCB.selectedProperty()
				.addListener((f, o, n) -> item1.setRemoveCombinablesWhenCombinedWith(item2, n));
		
		newItemsListView.initialize(item1.getNewCombinablesWhenCombinedWith(item2),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				(i) -> {}, (ii) -> item1.addNewCombinableWhenCombinedWith(item2, ii),
				(ii) -> item1.removeNewCombinableWhenCombinedWith(item2, ii));

		saveTabIndex(tabPane);
	}
}