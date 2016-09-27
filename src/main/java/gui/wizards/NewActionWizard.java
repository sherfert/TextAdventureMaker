package gui.wizards;

import java.util.Optional;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import data.InventoryItem;
import data.Location;
import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.ChangeActionAction;
import data.action.EndGameAction;
import data.action.MoveAction;
import data.action.MultiAction;
import data.action.RemoveInventoryItemAction;
import gui.customui.ActionChooser;
import gui.customui.InventoryItemChooser;
import gui.customui.LocationChooser;
import gui.utility.Loader;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * A wizard that guides the user through the creation of an action.
 * 
 * @author satia
 */
public class NewActionWizard extends Wizard {

	// Constants for the wizard settings
	private static final String TYPE_KEY = "type";
	private static final String NAME_KEY = "name";
	private static final String ACTIONTOCHANGE_KEY = "actionToChange";
	private static final String REMOVEITEM_KEY = "removeItem";
	private static final String MOVETARGET_KEY = "moveTarget";

	private static final String TYPE_REMOVEII = "removeII";
	private static final String TYPE_ADDII = "addII";
	private static final String TYPE_MULTI = "multi";
	private static final String TYPE_ENDGAME = "endGame";
	private static final String TYPE_MOVE = "move";

	/** The current game manager. */
	private CurrentGameManager currentGameManager;

	private NewActionWizardFlow flow;

	/**
	 * @param currentGameManager
	 */
	public NewActionWizard(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;

		setTitle("New action");
		// TODO wizard and dialog icons

		ChooseTypePane ctp = new ChooseTypePane();
		ChooseActionToChangePane catcp = new ChooseActionToChangePane();
		ChooseNamePane cnp = new ChooseNamePane();
		ChooseRemoveItemPane crip = new ChooseRemoveItemPane();
		ChooseMoveTargetPane cmtp = new ChooseMoveTargetPane();

		flow = new NewActionWizardFlow(ctp, catcp, cnp, crip, cmtp);
	}

	/**
	 * Show the wizard and obtain the newly created action, if any.
	 * 
	 * @return the new action.
	 */
	public Optional<AbstractAction> showAndGetAction() {
		// TODO complete wizard
		// assign the flow
		setFlow(flow);

		// show wizard and wait for response
		return showAndWait().map(result -> {
			AbstractAction newAction = null;

			if (result == ButtonType.FINISH) {
				// TODO String constants
				// Read settings and create an action accordingly
				ObservableMap<String, Object> settings = getSettings();
				// TODO remove sysout
				System.out.println("Wizard finished, settings: " + settings);
				if (settings.get(TYPE_KEY).equals("changeAction")) {
					newAction = new ChangeActionAction((String) settings.get(NAME_KEY),
							(AbstractAction) settings.get(ACTIONTOCHANGE_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_ENDGAME)) {
					newAction = new EndGameAction((String) settings.get(NAME_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_MULTI)) {
					newAction = new MultiAction((String) settings.get(NAME_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_ADDII)) {
					newAction = new AddInventoryItemsAction((String) settings.get(NAME_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_REMOVEII)) {
					newAction = new RemoveInventoryItemAction((String) settings.get(NAME_KEY),
							(InventoryItem) settings.get(REMOVEITEM_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_MOVE)) {
					newAction = new MoveAction((String) settings.get(NAME_KEY),
							(Location) settings.get(MOVETARGET_KEY));
				}
			}
			return newAction;
		});
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
		private ChooseRemoveItemPane crip;
		private ChooseMoveTargetPane cmtp;

		// Expects all panes
		public NewActionWizardFlow(ChooseTypePane ctp, ChooseActionToChangePane catcp, ChooseNamePane cnp,
				ChooseRemoveItemPane crip, ChooseMoveTargetPane cmtp) {
			this.ctp = ctp;
			this.catcp = catcp;
			this.cnp = cnp;
			this.crip = crip;
			this.cmtp = cmtp;
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == null) {
				return Optional.of(ctp);
			} else if (currentPage == ctp) {
				if (ctp.changeActionRB.isSelected()) {
					return Optional.of(catcp);
				} else if (ctp.removeIIRB.isSelected()) {
					return Optional.of(crip);
				} else if (ctp.moveRB.isSelected()) {
					return Optional.of(cmtp);
				} else if (ctp.endGameRB.isSelected() || ctp.multiRB.isSelected() || ctp.addIIRB.isSelected()) {
					return Optional.of(cnp);
				}
			} else if (currentPage == catcp || currentPage == crip || currentPage == cmtp) {
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

		@Override
		public void onExitingPage(Wizard wizard) {
			if (changeUseWithRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, "changeUseWith");
			} else if (changeCombineRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, "changeCombine");
			} else if (changeActionRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, "changeAction");
			} else if (addIIRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_ADDII);
			} else if (removeIIRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_REMOVEII);
			} else if (moveRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_MOVE);
			} else if (multiRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_MULTI);
			} else if (endGameRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_ENDGAME);
			}
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
			wizard.getSettings().put(ACTIONTOCHANGE_KEY, actionChooser.getObjectValue());
		}
	}

	/**
	 * Pane to choose which item to remove for a
	 * {@link RemoveInventoryItemAction}.
	 * 
	 * @author satia
	 */
	public class ChooseRemoveItemPane extends WizardPane {
		private @FXML InventoryItemChooser removeItemChooser;
		private Wizard wizard;

		public ChooseRemoveItemPane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseRemoveItem.fxml");
			removeItemChooser.initialize(null, false, false,
					currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, (a) -> {
						wizard.setInvalid(a == null);
					});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(removeItemChooser.getObjectValue() == null);
			this.wizard = wizard;
		}

		@Override
		public void onExitingPage(Wizard wizard) {
			wizard.getSettings().put(REMOVEITEM_KEY, removeItemChooser.getObjectValue());
		}
	}

	/**
	 * Pane to choose target for a {@link MoveAction}.
	 * 
	 * @author satia
	 */
	public class ChooseMoveTargetPane extends WizardPane {
		private @FXML LocationChooser moveTargetChooser;
		private Wizard wizard;

		public ChooseMoveTargetPane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseMoveTarget.fxml");
			moveTargetChooser.initialize(null, false, false,
					currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (a) -> {
						wizard.setInvalid(a == null);
					});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(moveTargetChooser.getObjectValue() == null);
			this.wizard = wizard;
		}

		@Override
		public void onExitingPage(Wizard wizard) {
			wizard.getSettings().put(MOVETARGET_KEY, moveTargetChooser.getObjectValue());
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
				// XXX cannot do this in onExitingPage: Not called for last page
				wizard.getSettings().put(NAME_KEY, n);
			});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(nameTF.getText().isEmpty());
			this.wizard = wizard;
		}
	}

}
