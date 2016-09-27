package gui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import data.Conversation;
import data.ConversationOption;
import data.InventoryItem;
import data.Item;
import data.Location;
import data.NamedObject;
import data.Person;
import data.Way;
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
import data.interfaces.PassivelyUsable;
import exception.DBClosedException;
import gui.customui.ActionChooser;
import gui.customui.InventoryItemChooser;
import gui.customui.LocationChooser;
import gui.customui.NamedObjectChooser;
import gui.customui.PassivelyUsableChooser;
import gui.utility.Loader;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.CurrentGameManager;
import utility.WindowUtil;

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
	private static final String USEWITHINVITEM_KEY = "useWithInvItem";
	private static final String USEWITHOBJECT_KEY = "useWithObject";
	private static final String COMBINEINVITEM1_KEY = "combineInvItem1";
	private static final String COMBINEINVITEM2_KEY = "combineInvItem2";
	private static final String OBJECTTOCHANGE_KEY = "objectToChange";

	private static final String TYPE_REMOVEII = "removeII";
	private static final String TYPE_ADDII = "addII";
	private static final String TYPE_MULTI = "multi";
	private static final String TYPE_ENDGAME = "endGame";
	private static final String TYPE_MOVE = "move";
	private static final String TYPE_CHANGEACTION = "changeAction";
	private static final String TYPE_CHANGEUSEWITH = "changeUseWith";
	private static final String TYPE_CHANGECOMBINE = "changeCombine";
	private static final String TYPE_CHANGEOBJECT = "changeObject";

	/** The current game manager. */
	private CurrentGameManager currentGameManager;

	/**
	 * @param currentGameManager
	 */
	public NewActionWizard(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;

		setTitle("New action");
		NewActionWizardFlow flow = new NewActionWizardFlow();
		setFlow(flow);
		
		// Set the icon using the first pane in the flow.
		Image img = new Image(WindowUtil.getWindowIconURL().toString());
		Stage stage = (Stage) flow.ctp.getScene().getWindow();
		stage.getIcons().add(img);
	}

	/**
	 * Show the wizard and obtain the newly created action, if any.
	 * 
	 * @return the new action.
	 */
	public Optional<AbstractAction> showAndGetAction() {
		return showAndWait().map(result -> {
			AbstractAction newAction = null;

			if (result == ButtonType.FINISH) {
				// Read settings and create an action accordingly
				ObservableMap<String, Object> settings = getSettings();

				if (settings.get(TYPE_KEY).equals(TYPE_CHANGEACTION)) {
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
				} else if (settings.get(TYPE_KEY).equals(TYPE_CHANGEUSEWITH)) {
					newAction = new ChangeUseWithInformationAction((String) settings.get(NAME_KEY),
							(InventoryItem) settings.get(USEWITHINVITEM_KEY),
							(PassivelyUsable) settings.get(USEWITHOBJECT_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_CHANGECOMBINE)) {
					newAction = new ChangeCombineInformationAction((String) settings.get(NAME_KEY),
							(InventoryItem) settings.get(COMBINEINVITEM1_KEY),
							(InventoryItem) settings.get(COMBINEINVITEM2_KEY));
				} else if (settings.get(TYPE_KEY).equals(TYPE_CHANGEOBJECT)) {
					NamedObject object = (NamedObject) settings.get(OBJECTTOCHANGE_KEY);
					if (object instanceof Conversation) {
						newAction = new ChangeConversationAction((String) settings.get(NAME_KEY),
								(Conversation) object);
					} else if (object instanceof ConversationOption) {
						newAction = new ChangeConversationOptionAction((String) settings.get(NAME_KEY),
								(ConversationOption) object);
					} else if (object instanceof Item) {
						newAction = new ChangeItemAction((String) settings.get(NAME_KEY), (Item) object);
					} else if (object instanceof Location) {
						newAction = new ChangeNDObjectAction((String) settings.get(NAME_KEY), (Location) object);
					} else if (object instanceof Person) {
						newAction = new ChangePersonAction((String) settings.get(NAME_KEY), (Person) object);
					} else if (object instanceof InventoryItem) {
						newAction = new ChangeUsableObjectAction((String) settings.get(NAME_KEY),
								(InventoryItem) object);
					} else if (object instanceof Way) {
						newAction = new ChangeWayAction((String) settings.get(NAME_KEY), (Way) object);
					}
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
		private ChooseUseWithItemsPane cuwip;
		private ChooseCombineItemsPane ccip;
		private ChooseObjectToChangePane cotcp;

		/**
		 * Creates all panes and defines the flow between them.
		 */
		public NewActionWizardFlow() {
			this.ctp = new ChooseTypePane();
			this.catcp = new ChooseActionToChangePane();
			this.cnp = new ChooseNamePane();
			this.crip = new ChooseRemoveItemPane();
			this.cmtp = new ChooseMoveTargetPane();
			this.cuwip = new ChooseUseWithItemsPane();
			this.ccip = new ChooseCombineItemsPane();
			this.cotcp = new ChooseObjectToChangePane();
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
				} else if (ctp.changeUseWithRB.isSelected()) {
					return Optional.of(cuwip);
				} else if (ctp.changeCombineRB.isSelected()) {
					return Optional.of(ccip);
				} else if (ctp.changeRB.isSelected()) {
					return Optional.of(cotcp);
				} else if (ctp.endGameRB.isSelected() || ctp.multiRB.isSelected() || ctp.addIIRB.isSelected()) {
					return Optional.of(cnp);
				}
			} else if (currentPage == catcp || currentPage == crip || currentPage == cmtp || currentPage == cuwip
					|| currentPage == ccip || currentPage == cotcp) {
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
				wizard.getSettings().put(TYPE_KEY, TYPE_CHANGEUSEWITH);
			} else if (changeCombineRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_CHANGECOMBINE);
			} else if (changeActionRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_CHANGEACTION);
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
			} else if (changeRB.isSelected()) {
				wizard.getSettings().put(TYPE_KEY, TYPE_CHANGEOBJECT);
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
	 * Pane to choose which object to change for a {@link ChangeNDObjectAction}.
	 * 
	 * @author satia
	 */
	public class ChooseObjectToChangePane extends WizardPane {
		private @FXML NamedObjectChooser<NamedObject> objectChooser;
		private Wizard wizard;

		public ChooseObjectToChangePane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseObjectToChange.fxml");
			objectChooser.initialize(null, false, false, () -> {
				List<NamedObject> allObjects = new ArrayList<>();
				allObjects.addAll(
						currentGameManager.getPersistenceManager().getConversationManager().getAllConversations());
				allObjects.addAll(currentGameManager.getPersistenceManager().getConversationOptionManager()
						.getAllConversationOptions());
				allObjects.addAll(currentGameManager.getPersistenceManager().getItemManager().getAllItems());
				allObjects.addAll(currentGameManager.getPersistenceManager().getLocationManager().getAllLocations());
				allObjects.addAll(currentGameManager.getPersistenceManager().getPersonManager().getAllPersons());
				allObjects.addAll(
						currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems());
				allObjects.addAll(currentGameManager.getPersistenceManager().getWayManager().getAllWays());
				return allObjects;
			} , (a) -> {
				wizard.setInvalid(a == null);
			});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(objectChooser.getObjectValue() == null);
			this.wizard = wizard;
		}

		@Override
		public void onExitingPage(Wizard wizard) {
			wizard.getSettings().put(OBJECTTOCHANGE_KEY, objectChooser.getObjectValue());
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
	 * Pane to choose which items for a {@link ChangeUseWithInformationAction}.
	 * 
	 * @author satia
	 */
	public class ChooseUseWithItemsPane extends WizardPane {
		private @FXML InventoryItemChooser invItemChooser;
		private @FXML PassivelyUsableChooser objectChooser;
		private Wizard wizard;

		public ChooseUseWithItemsPane() {
			Loader.load(this, this, "view/wizards/CreateActionChooseUseWithItems.fxml");
			invItemChooser.initialize(null, false, false,
					currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, (a) -> {
						wizard.setInvalid(
								invItemChooser.getObjectValue() == null || objectChooser.getObjectValue() == null);
					});
			objectChooser.initialize(null, false, false, () -> {
				List<PassivelyUsable> allObjects = new ArrayList<>();
				allObjects.addAll(currentGameManager.getPersistenceManager().getItemManager().getAllItems());
				allObjects.addAll(currentGameManager.getPersistenceManager().getPersonManager().getAllPersons());
				allObjects.addAll(currentGameManager.getPersistenceManager().getWayManager().getAllWays());
				return allObjects;
			} , (a) -> {
				wizard.setInvalid(invItemChooser.getObjectValue() == null || objectChooser.getObjectValue() == null);
			});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(invItemChooser.getObjectValue() == null || objectChooser.getObjectValue() == null);
			this.wizard = wizard;
		}

		@Override
		public void onExitingPage(Wizard wizard) {
			wizard.getSettings().put(USEWITHINVITEM_KEY, invItemChooser.getObjectValue());
			wizard.getSettings().put(USEWITHOBJECT_KEY, objectChooser.getObjectValue());
		}
	}

	/**
	 * Pane to choose which items for a {@link ChangeCombineInformationAction}.
	 * 
	 * @author satia
	 */
	public class ChooseCombineItemsPane extends WizardPane {
		private @FXML InventoryItemChooser invItem1Chooser;
		private @FXML InventoryItemChooser invItem2Chooser;
		private Wizard wizard;

		public ChooseCombineItemsPane() {
			List<InventoryItem> allItems;
			try {
				allItems = currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems();
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
				return;
			}

			Loader.load(this, this, "view/wizards/CreateActionChooseCombineItems.fxml");
			invItem1Chooser.initialize(null, false, true, () -> {
				return allItems.stream().filter((i) -> invItem2Chooser.getObjectValue() != i)
						.collect(Collectors.toList());
			} , (a) -> {
				wizard.setInvalid(invItem1Chooser.getObjectValue() == null || invItem2Chooser.getObjectValue() == null);
			});
			invItem2Chooser.initialize(null, false, true, () -> {
				return allItems.stream().filter((i) -> invItem1Chooser.getObjectValue() != i)
						.collect(Collectors.toList());
			} , (a) -> {
				wizard.setInvalid(invItem1Chooser.getObjectValue() == null || invItem2Chooser.getObjectValue() == null);
			});
		}

		@Override
		public void onEnteringPage(Wizard wizard) {
			wizard.setInvalid(invItem1Chooser.getObjectValue() == null || invItem2Chooser.getObjectValue() == null);
			this.wizard = wizard;
		}

		@Override
		public void onExitingPage(Wizard wizard) {
			wizard.getSettings().put(COMBINEINVITEM1_KEY, invItem1Chooser.getObjectValue());
			wizard.getSettings().put(COMBINEINVITEM2_KEY, invItem2Chooser.getObjectValue());
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
