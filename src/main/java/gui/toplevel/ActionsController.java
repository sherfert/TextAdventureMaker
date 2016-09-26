package gui.toplevel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.Flow;
import org.controlsfx.dialog.WizardPane;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.ChangeActionAction;
import data.action.ChangeCombineInformationAction;
import data.action.ChangeConversationAction;
import data.action.ChangeConversationOptionAction;
import data.action.ChangeItemAction;
import data.action.ChangeNDObjectAction;
import data.action.ChangePersonAction;
import data.action.ChangeUsableObjectAction;
import data.action.ChangeUseWithInformationAction;
import data.action.ChangeWayAction;
import data.action.EndGameAction;
import data.action.MoveAction;
import data.action.MultiAction;
import data.action.RemoveInventoryItemAction;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsTableController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import logic.CurrentGameManager;

/**
 * Controller for the actions view.
 * 
 * @author Satia
 */
public class ActionsController extends NamedObjectsTableController<AbstractAction> {

	private static Map<Class<? extends AbstractAction>, String> readableActionTypeNames = new HashMap<>();

	static {
		readableActionTypeNames.put(AddInventoryItemsAction.class, "Add-Inventory-Items Action");
		readableActionTypeNames.put(ChangeActionAction.class, "Change-Action Action");
		readableActionTypeNames.put(ChangeCombineInformationAction.class, "Change-Combine-Information Action");
		readableActionTypeNames.put(ChangeConversationAction.class, "Change-Conversation Action");
		readableActionTypeNames.put(ChangeConversationOptionAction.class, "Change-Conversation-Option Action");
		readableActionTypeNames.put(ChangeItemAction.class, "Change-Item Action");
		readableActionTypeNames.put(ChangeNDObjectAction.class, "Change-Location Action");
		readableActionTypeNames.put(ChangePersonAction.class, "Change-Person Action");
		readableActionTypeNames.put(ChangeUsableObjectAction.class, "Change-Inventory-Item Action");
		readableActionTypeNames.put(ChangeUseWithInformationAction.class, "Change-Use-With-Information Action");
		readableActionTypeNames.put(ChangeWayAction.class, "Change-Way Action");
		readableActionTypeNames.put(EndGameAction.class, "End-game Action");
		readableActionTypeNames.put(MoveAction.class, "Move Action");
		readableActionTypeNames.put(MultiAction.class, "Multi Action");
		readableActionTypeNames.put(RemoveInventoryItemAction.class, "Remove-Inventory-Item Action");
	}

	/**
	 * Obtains the better readable name describing what an action class does.
	 * 
	 * @param clazz
	 *            the class
	 * @return the name
	 */
	public static String getReadableActionTypeName(Class<? extends AbstractAction> clazz) {
		return readableActionTypeNames.get(clazz);
	}

	@FXML
	private TableColumn<AbstractAction, String> typeCol;

	@FXML
	private TableColumn<AbstractAction, String> summaryCol;

	@FXML
	protected Button newButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public ActionsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		typeCol.setCellValueFactory(
				(p) -> new ReadOnlyObjectWrapper<String>(getReadableActionTypeName(p.getValue().getClass())));
		summaryCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<String>(p.getValue().actionDescription()));

		// Allow wrapped text for the summary column
		summaryCol.setCellFactory((p) -> {
			TableCell<AbstractAction, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			text.wrappingWidthProperty().bind(cell.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});

		newButton.setOnMouseClicked((e) -> createAction());
	}

	@Override
	protected List<AbstractAction> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getActionManager().getAllActions();
	}

	/**
	 * Opens a dialog to create a new action.
	 */
	private void createAction() {
		// TODO
		WizardPane page1 = new WizardPane();
		WizardPane page2 = new WizardPane();
		WizardPane page3 = new WizardPane();
		
		page1.getChildren().add(new javafx.scene.control.Label("tadaa"));
		page1.getChildren().add(new javafx.scene.control.Label("tüdelü"));

		// create wizard
		Wizard wizard = new Wizard();
		wizard.setTitle("New action");

		// create and assign the flow
		wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3));

		// show wizard and wait for response
		wizard.showAndWait().ifPresent(result -> {
			if (result == ButtonType.FINISH) {
				System.out.println("Wizard finished, settings: " + wizard.getSettings());
			}
		});
	}

}
