package gui.itemEditing;

import data.InventoryItem;
import data.action.AbstractAction;
import data.interfaces.PassivelyUsable;
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
 * Controller for the usage of an inventory item with a person or object.
 * 
 * @author Satia
 */
public class UseWithInformationController extends GameDataController {

	/** The inventory item that is being used */
	private InventoryItem item;

	/** The object it is being used with */
	private PassivelyUsable object;

	@FXML
	private CheckBox editUsingEnabledCB;

	@FXML
	private TextField editUseWithSuccessfulTextTF;

	@FXML
	private TextField editUseWithForbiddenTextTF;

	@FXML
	private TextArea editUseWithCommandsTA;

	@FXML
	private Label useWithCommandsLabel;

	@FXML
	private NamedObjectListView<AbstractAction> useWithActionsListView;

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
	public UseWithInformationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			InventoryItem item, PassivelyUsable object) {
		super(currentGameManager, mwController);
		this.item = item;
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Refresh the displayed object
		currentGameManager.getPersistenceManager().refreshEntity(item);
		currentGameManager.getPersistenceManager().refreshEntity(object);

		// Create new bindings
		editUseWithSuccessfulTextTF.setText(item.getUseWithSuccessfulText(object));
		editUseWithSuccessfulTextTF.textProperty().addListener((f, o, n) -> item.setUseWithSuccessfulText(object, n));
		editUseWithSuccessfulTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editUseWithSuccessfulTextTF, allPL, true));
		addPlaceholderTextTooltip(editUseWithSuccessfulTextTF,
				"This is the text when the two items are used together. If empty, the default will be used.", allPL);

		editUseWithForbiddenTextTF.setText(item.getUseWithForbiddenText(object));
		editUseWithForbiddenTextTF.textProperty().addListener((f, o, n) -> item.setUseWithForbiddenText(object, n));
		editUseWithForbiddenTextTF.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, editUseWithForbiddenTextTF, allPL, true));
		addPlaceholderTextTooltip(editUseWithForbiddenTextTF,
				"This is the text when the two items could not be used together. If empty, the default will be used.",
				allPL);

		editUsingEnabledCB.setSelected(item.isUsingEnabledWith(object));
		editUsingEnabledCB.selectedProperty().addListener((f, o, n) -> item.setUsingEnabledWith(object, n));
		setNodeTooltip(editUsingEnabledCB,
				"If ticked, the inventory item can be used with the other item, person, or way.");

		editUseWithCommandsTA.setText(getCommandString(item.getAdditionalUseWithCommands(object)));
		editUseWithCommandsTA.textProperty().addListener((f, o, n) -> updateGameCommands(n, 2, true,
				editUseWithCommandsTA, (cs) -> item.setAdditionalUseWithCommands(object, cs)));
		addCommandTooltip(editUseWithCommandsTA,
				"Additional commands to use the two items together. These will only be valid for this exact combination of items.");

		useWithCommandsLabel.setText("Additional commands for using " + item.getName() + " with " + object.getName());

		useWithActionsListView.initialize(item.getAdditionalActionsFromUseWith(object),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions, null,
				this::objectSelected, (a) -> item.addAdditionalActionToUseWith(object, a),
				(a) -> item.removeAdditionalActionFromUseWith(object, a));
	}

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(item)
				|| !currentGameManager.getPersistenceManager().isManaged(object);
	}
}
