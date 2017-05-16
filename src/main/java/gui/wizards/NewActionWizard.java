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
import gui.customui.NamedObjectChooser;
import gui.utility.Loader;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
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
	private static final String NAME_KEY = "name";
	private static final String TYPE_KEY = "type";
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

	public NewActionWizard(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;

		setTitle("New action");
		NewActionWizardFlow flow = new NewActionWizardFlow();
		setFlow(flow);

		// Set the icon using the first pane in the flow.
		Image img = new Image(WindowUtil.getWindowIconURL().toString());
		Stage stage = (Stage) flow.chooseTypePane.getScene().getWindow();
		stage.getIcons().add(img);
	}

	/**
	 * Show the wizard and obtain the newly created action, if any.
	 * 
	 * @return the new action.
	 */
	public Optional<AbstractAction> showAndGet() {
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
		private ChooseTypePane chooseTypePane;
		private ChooseNamedObjectPane<AbstractAction> chooseActionToChangePane;
		private ChooseStringPane chooseNamePane;
		private ChooseNamedObjectPane<InventoryItem> chooseRemoveItemPane;
		private ChooseNamedObjectPane<Location> chooseMoveToPane;
		private ChooseUseWithItemsPane chooseUseWithItemsPane;
		private ChooseCombineItemsPane chooseCombineItemsPane;
		private ChooseNamedObjectPane<NamedObject> chooseObjectToChangePane;

		/**
		 * Creates all panes and defines the flow between them.
		 */
		public NewActionWizardFlow() {
			this.chooseTypePane = new ChooseTypePane();
			this.chooseActionToChangePane = new ChooseNamedObjectPane<>("What action should be changed?", ACTIONTOCHANGE_KEY);
			this.chooseActionToChangePane.getChooser().setNoValueString("(no action)");
			this.chooseActionToChangePane.getChooser().initialize(null, false, false,
					currentGameManager.getPersistenceManager().getActionManager()::getAllActions, (l) -> {
						setInvalid(l == null);
					});

			this.chooseNamePane = new ChooseStringPane("Type a name for the new action", NAME_KEY, false);
			this.chooseRemoveItemPane = new ChooseNamedObjectPane<>("What inventory item should be removed?", REMOVEITEM_KEY);
			this.chooseRemoveItemPane.getChooser().setNoValueString("(no inventory item)");
			this.chooseRemoveItemPane.getChooser().initialize(null, false, false,
					currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, (l) -> {
						setInvalid(l == null);
					});

			this.chooseMoveToPane = new ChooseNamedObjectPane<>("Where should the player be moved?", MOVETARGET_KEY);
			this.chooseMoveToPane.getChooser().setNoValueString("(no location)");
			this.chooseMoveToPane.getChooser().initialize(null, false, false,
					currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
						setInvalid(l == null);
					});

			this.chooseUseWithItemsPane = new ChooseUseWithItemsPane();
			this.chooseCombineItemsPane = new ChooseCombineItemsPane();
			this.chooseObjectToChangePane = new ChooseNamedObjectPane<>(
					"What object should be changed? You can change conversations, conversation options, items, inventory items, locations, persons and ways.",
					OBJECTTOCHANGE_KEY);
			this.chooseObjectToChangePane.getChooser().setNoValueString("(no object)");
			this.chooseObjectToChangePane.getChooser().initialize(null, false, false, () -> {
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
			}, (l) -> {
				setInvalid(l == null);
			});
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == null) {
				return Optional.of(chooseTypePane);
			} else if (currentPage == chooseTypePane) {
				if (chooseTypePane.changeActionRB.isSelected()) {
					return Optional.of(chooseActionToChangePane);
				} else if (chooseTypePane.removeIIRB.isSelected()) {
					return Optional.of(chooseRemoveItemPane);
				} else if (chooseTypePane.moveRB.isSelected()) {
					return Optional.of(chooseMoveToPane);
				} else if (chooseTypePane.changeUseWithRB.isSelected()) {
					return Optional.of(chooseUseWithItemsPane);
				} else if (chooseTypePane.changeCombineRB.isSelected()) {
					return Optional.of(chooseCombineItemsPane);
				} else if (chooseTypePane.changeRB.isSelected()) {
					return Optional.of(chooseObjectToChangePane);
				} else if (chooseTypePane.endGameRB.isSelected() || chooseTypePane.multiRB.isSelected() || chooseTypePane.addIIRB.isSelected()) {
					return Optional.of(chooseNamePane);
				}
			} else if (currentPage == chooseActionToChangePane || currentPage == chooseRemoveItemPane || currentPage == chooseMoveToPane || currentPage == chooseUseWithItemsPane
					|| currentPage == chooseCombineItemsPane || currentPage == chooseObjectToChangePane) {
				return Optional.of(chooseNamePane);
			}
			// As default, don't switch
			return Optional.of(currentPage);
		}

		@Override
		public boolean canAdvance(WizardPane currentPage) {
			// the name is always chosen last
			return currentPage != chooseNamePane;
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
	 * Pane to choose which items for a {@link ChangeUseWithInformationAction}.
	 * 
	 * @author satia
	 */
	public class ChooseUseWithItemsPane extends WizardPane {
		private @FXML NamedObjectChooser<InventoryItem> invItemChooser;
		private @FXML NamedObjectChooser<PassivelyUsable> objectChooser;
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
			}, (a) -> {
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
		private @FXML NamedObjectChooser<InventoryItem> invItem1Chooser;
		private @FXML NamedObjectChooser<InventoryItem> invItem2Chooser;
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
			}, (a) -> {
				wizard.setInvalid(invItem1Chooser.getObjectValue() == null || invItem2Chooser.getObjectValue() == null);
			});
			invItem2Chooser.initialize(null, false, true, () -> {
				return allItems.stream().filter((i) -> invItem1Chooser.getObjectValue() != i)
						.collect(Collectors.toList());
			}, (a) -> {
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
}
