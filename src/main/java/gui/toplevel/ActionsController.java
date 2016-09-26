package gui.toplevel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.dialog.Wizard;
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
import gui.customui.ActionChooser;
import gui.utility.Loader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
		ChooseTypePane ctp = new ChooseTypePane();
		ChooseActionToChangePane catcp = new ChooseActionToChangePane();
		ChooseNamePane cnp = new ChooseNamePane();

		NewActionWizardFlow flow = new NewActionWizardFlow(ctp, catcp, cnp);

		// create wizard
		Wizard wizard = new Wizard();
		wizard.setTitle("New action");

		// assign the flow
		wizard.setFlow(flow);

		// show wizard and wait for response
		wizard.showAndWait().ifPresent(result -> {
			if (result == ButtonType.FINISH) {
				AbstractAction newAction = null;

				// Read settings and create an action accordingly
				ObservableMap<String, Object> settings = wizard.getSettings();
				// TODO remove sysout
				System.out.println("Wizard finished, settings: " + settings);
				if ((Boolean) settings.get("changeActionRB")) {
					newAction = new ChangeActionAction((String) settings.get("name"),
							(AbstractAction) settings.get("actionToChange"));
				}

				if (newAction != null) {
					saveNewAction(newAction);
				}
			}
		});
	}

	/**
	 * Saves a new action to both DB and table.
	 * 
	 * @param a
	 *            the action
	 */
	private void saveNewAction(AbstractAction a) {
		// Add item to DB
		try {
			saveObject(a);
		} catch (DBClosedException e1) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}
		// Add item to our table
		objectsOL.add(a);
	}

	/**
	 * Flow for a wizard to create a new action.
	 * 
	 * @author satia
	 */
	private class NewActionWizardFlow implements Wizard.Flow {
		private ChooseTypePane ctp;
		private ChooseActionToChangePane catcp;
		private ChooseNamePane cnp;

		// Expects all panes
		public NewActionWizardFlow(ChooseTypePane ctp, ChooseActionToChangePane catcp, ChooseNamePane cnp) {
			this.ctp = ctp;
			this.catcp = catcp;
			this.cnp = cnp;
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == null) {
				return Optional.of(ctp);
			} else if (currentPage == ctp) {
				if (ctp.typeTG.getSelectedToggle() == ctp.changeActionRB) {
					return Optional.of(catcp);
				}
			} else if (currentPage == catcp) {
				return Optional.of(cnp);
			}
			// As default, don't switch
			return Optional.of(currentPage);
		}

		@Override
		public boolean canAdvance(WizardPane currentPage) {
			// the name is always chosen last
			return currentPage != cnp;
		}

	}

	/**
	 * Pane to choose the type of action to create.
	 * 
	 * @author satia
	 */
	public class ChooseTypePane extends WizardPane {
		private @FXML ToggleGroup typeTG;
		private @FXML RadioButton changeRB;
		private @FXML RadioButton changeUseWithRB;
		private @FXML RadioButton changeCombineRB;
		private @FXML RadioButton changeActionRB;
		private @FXML RadioButton addIIRB;
		private @FXML RadioButton removeIIRB;
		private @FXML RadioButton moveRB;
		private @FXML RadioButton multiRB;
		private @FXML RadioButton endGameRB;

		public ChooseTypePane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseType.fxml");
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(false);
		}
	}

	/**
	 * Pane to choose which action to change for a {@link ChangeActionAction}.
	 * 
	 * @author satia
	 */
	public class ChooseActionToChangePane extends WizardPane {
		private @FXML ActionChooser actionChooser;
		private Wizard wizard;

		public ChooseActionToChangePane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseActionToChange.fxml");
			actionChooser.initialize(null, false, false,
					currentGameManager.getPersistenceManager().getActionManager()::getAllActions, (a) -> {
						wizard.setInvalid(a == null);
					});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(actionChooser.getObjectValue() == null);
			this.wizard = wizard;
		}

		@Override
		public void onExitingPage(Wizard wizard) {
			wizard.getSettings().put("actionToChange", actionChooser.getObjectValue());
		}
	}

	/**
	 * Pane to choose the name of the new action.
	 * 
	 * @author satia
	 */
	public class ChooseNamePane extends WizardPane {
		private @FXML TextField nameTF;
		private Wizard wizard;

		public ChooseNamePane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseName.fxml");
			nameTF.textProperty().addListener((f, o, n) -> {
				wizard.setInvalid(n.isEmpty());
				wizard.getSettings().put("name", n);
			});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(nameTF.getText().isEmpty());
			this.wizard = wizard;
		}
	}

}
