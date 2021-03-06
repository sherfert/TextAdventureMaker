package gui.itemEditing;

import data.InventoryItem;
import data.action.AbstractAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectListView;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one the combination of two inventory items.
 * 
 * @author Satia
 */
public class CombinationInformationController extends GameDataController {

	/** The first inventory item */
	private InventoryItem item1;

	/** The second inventory item */
	private InventoryItem item2;

	@FXML
	private CheckBox editCombiningEnabledCB;

	@FXML
	private CheckBox editRemoveItemsCB;

	@FXML
	private TextField editCombineSuccessfulTextTF;

	@FXML
	private TextField editCombineForbiddenTextTF;

	@FXML
	private NamedObjectListView<InventoryItem> newItemsListView;

	@FXML
	private Label combine1CommandsLabel;

	@FXML
	private TextArea editCombine1CommandsTA;

	@FXML
	private Label combine2CommandsLabel;

	@FXML
	private TextArea editCombine2CommandsTA;

	@FXML
	private NamedObjectListView<AbstractAction> combineActionsListView;

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
	public CombinationInformationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			InventoryItem item1, InventoryItem item2) {
		super(currentGameManager, mwController);
		this.item1 = item1;
		this.item2 = item2;
	}

	@FXML
	private void initialize() {
		// Refresh the displayed object
		currentGameManager.getPersistenceManager().refreshEntity(item1);
		currentGameManager.getPersistenceManager().refreshEntity(item2);

		// Create new bindings
		editCombineSuccessfulTextTF.setText(item1.getCombineWithSuccessfulText(item2));
		editCombineSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> item1.setCombineWithSuccessfulText(item2, n));
		editCombineSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editCombineSuccessfulTextTF, allPL, true));
		addPlaceholderTextTooltip(editCombineSuccessfulTextTF,
				"This is the text when the two items are combined. If empty, the default will be used.", allPL);

		editCombineForbiddenTextTF.setText(item1.getCombineWithForbiddenText(item2));
		editCombineForbiddenTextTF.textProperty().addListener((f, o, n) -> item1.setCombineWithForbiddenText(item2, n));
		editCombineForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editCombineForbiddenTextTF, allPL, true));
		addPlaceholderTextTooltip(editCombineForbiddenTextTF,
				"This is the text when the two items could not be combined. If empty, the default will be used.",
				allPL);

		editCombiningEnabledCB.setSelected(item1.isCombiningEnabledWith(item2));
		editCombiningEnabledCB.selectedProperty().addListener((f, o, n) -> item1.setCombiningEnabledWith(item2, n));
		setNodeTooltip(editCombiningEnabledCB, "If ticked, the inventory items can be combined.");

		editRemoveItemsCB.setSelected(item1.getRemoveCombinablesWhenCombinedWith(item2));
		editRemoveItemsCB.selectedProperty()
				.addListener((f, o, n) -> item1.setRemoveCombinablesWhenCombinedWith(item2, n));
		setNodeTooltip(editRemoveItemsCB, "If ticked, the inventory items will disappear "
				+ "from the inventory after successfully combining them.");

		newItemsListView.initialize(item1.getNewCombinablesWhenCombinedWith(item2),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				(i) -> {
				}, (ii) -> item1.addNewCombinableWhenCombinedWith(item2, ii),
				(ii) -> item1.removeNewCombinableWhenCombinedWith(item2, ii));

		combine1CommandsLabel
				.setText("Additional commands for combining " + item1.getName() + " with " + item2.getName());
		combine2CommandsLabel
				.setText("Additional commands for combining " + item2.getName() + " with " + item1.getName());

		editCombine1CommandsTA.setText(getCommandString(item1.getAdditionalCombineCommands(item2)));
		editCombine1CommandsTA.textProperty().addListener((f, o, n) -> updateGameCommands(n, 2, true,
				editCombine1CommandsTA, (cs) -> item1.setAdditionalCombineCommands(item2, cs)));
		addCommandTooltip(editCombine1CommandsTA,
				"Additional commands to combine the two items. These will only be valid for this exact combination of items.");

		editCombine2CommandsTA.setText(getCommandString(item2.getAdditionalCombineCommands(item1)));
		editCombine2CommandsTA.textProperty().addListener((f, o, n) -> updateGameCommands(n, 2, true,
				editCombine2CommandsTA, (cs) -> item2.setAdditionalCombineCommands(item1, cs)));
		addCommandTooltip(editCombine2CommandsTA,
				"Additional commands to combine the two items. These will only be valid for this exact combination of items.");

		combineActionsListView.initialize(item1.getAdditionalActionsFromCombineWith(item2),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions,
				(as) -> item1.setAdditionalActionsFromCombineWith(item2, as), this::objectSelected,
				(a) -> item1.addAdditionalActionToCombineWith(item2, a),
				(a) -> item1.removeAdditionalActionFromCombineWith(item2, a));
	}

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(item1)
				|| !currentGameManager.getPersistenceManager().isManaged(item2);
	}
}
