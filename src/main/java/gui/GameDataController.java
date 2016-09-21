package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import data.Conversation;
import data.ConversationLayer;
import data.ConversationOption;
import data.InventoryItem;
import data.Item;
import data.Location;
import data.NamedObject;
import data.Person;
import data.Way;
import data.action.AddInventoryItemsAction;
import data.action.ChangeNDObjectAction;
import data.action.ChangePersonAction;
import data.action.ChangeWayAction;
import data.action.EndGameAction;
import data.action.MoveAction;
import data.action.MultiAction;
import data.action.RemoveInventoryItemAction;
import data.interfaces.HasId;
import gui.itemEditing.ConversationController;
import gui.itemEditing.ConversationLayerController;
import gui.itemEditing.ConversationOptionController;
import gui.itemEditing.InventoryItemController;
import gui.itemEditing.ItemController;
import gui.itemEditing.LocationController;
import gui.itemEditing.PersonController;
import gui.itemEditing.WayController;
import gui.itemEditing.action.AIIActionController;
import gui.itemEditing.action.ChangeLocationActionController;
import gui.itemEditing.action.ChangePersonActionController;
import gui.itemEditing.action.ChangeWayActionController;
import gui.itemEditing.action.EndGameActionController;
import gui.itemEditing.action.MoveActionController;
import gui.itemEditing.action.MultiActionController;
import gui.itemEditing.action.RIIActionController;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import logic.CurrentGameManager;
import utility.CommandRegExConverter;

/**
 * Abstract class for all controllers that need access to the current game
 * manager. They can use it to obtain the persistence manager and query the
 * database.
 * 
 * @author Satia
 *
 */
public abstract class GameDataController {

	/**
	 * A method allowing to set a list of strings.
	 * 
	 * @author Satia
	 */
	@FunctionalInterface
	public static interface StringListSetter {
		/**
		 * Sets the list.
		 * 
		 * @param list
		 *            the list
		 */
		public void setList(List<String> list);
	}

	private static final String NODE_PROPERTIES_KEY_ERROR_TOOLTIP = "Error-Tooltip";
	private static final String NODE_PROPERTIES_KEY_ERROR_FOCUSLISTENER = "Error-Tooltip-FocusListener";

	private static final String COMMAND_EMPTY = "At least one command must be defined";
	private static final String COMMAND_MULTI_WHITESPACE = "A command must not have mutiple white spaces";
	private static final String COMMAND_WHITESPACE_BEGINNING_END = "A command must not have white space in the beginning or end";
	private static final String COMMAND_INVALID_CHAR = "Only lowercase letters a-z and the space character are allowed.\n"
			+ "(Except for the characters '[' and ']' and the sequences '<A>' and '<B>')";
	private static final String COMMAND_NO_WORD = "A command must contain at least one word";
	private static final String COMMAND_UNMATCHING_BRACKETS = "Brackets must be matching and only contain a-z or space characters";
	private static final String COMMAND_PARAM_WRONG_NOT_ONE = "The parameter %s is expected to occur exactly once";
	private static final String COMMAND_PARAM_WRONG_NOT_ZERO = "The parameter %s is not supported for this command";

	private static final Pattern MATCHED_BRACKETS = Pattern.compile("(\\[[a-z &&[^\\]]]*\\])");
	private static final Pattern OPENING_BRACKETS = Pattern.compile("(\\[)");
	private static final Pattern CLOSING_BRACKETS = Pattern.compile("(\\])");
	private static final Pattern PARAM_A = Pattern.compile("(<A>)");
	private static final Pattern PARAM_B = Pattern.compile("(<B>)");
	private static final Pattern MULTIPLE_BLANKS = Pattern.compile("\\p{Blank}{2,}");
	private static final Pattern BLANKS_BEGINNING_END = Pattern.compile("(^\\p{Blank})|(\\p{Blank}$)");
	private static final Pattern VALID_SEQS = Pattern.compile("(([a-z])|[\\[\\] ]|(<(A|B)>))*");
	private static final Pattern CHAR = Pattern.compile("([a-z])");

	/** The current game manager. */
	protected CurrentGameManager currentGameManager;

	/**
	 * A reference to the main window controller to change the contents.
	 */
	protected MainWindowController mwController;

	/**
	 * For subtypes that have a TabPane, this saves the current index.
	 */
	private int currentTabIndex;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public GameDataController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		this.currentGameManager = currentGameManager;
		this.mwController = mwController;
	}

	/**
	 * Default controller factory. This can be used by subclasses. They should
	 * override the method, do some type checks and create their own instances.
	 * In an else branch, they should return
	 * {@code super.controllerFactory(type)}.
	 * 
	 * Or they don't override it if they don't have any nested controllers in
	 * the FXML.
	 * 
	 * @param type
	 *            the type of the object
	 * @return the new controller
	 */
	public Object controllerFactory(Class<?> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not create controller for  {0}",
					type.getName());
			return null;
		}
	}

	/**
	 * Show an error for any (input) node. This applies the css class "error" to
	 * the node (light red background) and shows a tooltip with the given error
	 * message right below the node. This tooltip cannot be hidden, until
	 * {@link #hideError(Node)} is called.
	 * 
	 * @param node
	 *            the node with the erroneous input
	 * @param errorMessage
	 *            the error message to display
	 */
	protected void showError(Node node, String errorMessage) {
		if (!node.getStyleClass().contains("error")) {
			// Apply some css to the node for light red BG
			node.getStyleClass().add("error");
		}

		// First check if a previous tooltip is still in place
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_ERROR_TOOLTIP)) {
			Tooltip tooltip = (Tooltip) node.getProperties().get(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);

			// Just change the error message
			tooltip.setText(errorMessage);
		} else {
			// Create a new tooltip
			Tooltip tooltip = new Tooltip(errorMessage);
			// Disallow hiding it
			tooltip.setHideOnEscape(false);

			// Show tooltip immediately below the input field
			Point2D p = node.localToScreen(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMaxY());
			tooltip.show(node, p.getX(), p.getY());

			ChangeListener<? super Boolean> focusListener = (f, o, n) -> {
				if (n) {
					Point2D p2 = node.localToScreen(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMaxY());
					tooltip.show(node, p2.getX(), p2.getY());
				} else {
					tooltip.hide();
				}
			};

			node.focusedProperty().addListener(focusListener);

			// Save tooltip and focusListener in node properties to access it
			// later
			node.getProperties().put(NODE_PROPERTIES_KEY_ERROR_TOOLTIP, tooltip);
			node.getProperties().put(NODE_PROPERTIES_KEY_ERROR_FOCUSLISTENER, focusListener);
		}
	}

	/**
	 * Hide any previous error messages and remove the css class "error" from
	 * the node. It is safe to call this method even though no error was showing
	 * previously.
	 * 
	 * @param node
	 *            the node
	 */
	protected void hideError(Node node) {
		// Remove error css class
		node.getStyleClass().remove("error");
		// Unset any tooltip message
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_ERROR_TOOLTIP)) {
			Tooltip tooltip = (Tooltip) node.getProperties().get(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);
			tooltip.hide();
			// Unset tooltip property
			node.getProperties().remove(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);

			// Remove focus listener
			@SuppressWarnings("unchecked")
			ChangeListener<? super Boolean> focusListener = (ChangeListener<? super Boolean>) node.getProperties()
					.get(NODE_PROPERTIES_KEY_ERROR_FOCUSLISTENER);
			node.focusedProperty().removeListener(focusListener);
		}
	}

	/**
	 * Obtains a single String from the list of Strings in the game. strings are
	 * joined with the '\n' character
	 * 
	 * @param strings
	 *            the list of strings
	 * @return the joined string.
	 */
	protected String getListString(List<String> strings) {
		// Assure no lazy loading DB list is used, therefore copy to a new list
		// The DB lists are incompatible with streams
		return new ArrayList<String>(strings).stream().collect(Collectors.joining("\n"));
	}

	/**
	 * Splits the string by the '\n' char and then invokes the setter with the
	 * obtained list.
	 * 
	 * @param joinedString
	 *            a string joined with '\n'chars.
	 * @param setter
	 *            a method to call that accepts a list of strings.
	 */
	protected void updateList(String joinedString, StringListSetter setter) {
		ArrayList<String> lines = new ArrayList<String>(Arrays.asList(joinedString.split("\\n")));
		setter.setList(lines);
	}

	/**
	 * Obtains a single command String from the list of commands in the game.
	 * Each command is converted into a better human readable form and the
	 * strings are joined with the '\n' character
	 * 
	 * @param commands
	 *            the list of commands
	 * @return the command string.
	 */
	protected String getCommandString(List<String> commands) {
		// Assure no lazy loading DB list is used, therefore copy to a new list
		// The DB lists are incompatible with streams

		// Iterate through the list and
		// convert the RegEx to a more readable form
		return new ArrayList<String>(commands).stream().map(CommandRegExConverter::convertRegExToString)
				.collect(Collectors.joining("\n"));
	}

	/**
	 * Updates a list of commands in the game, after various error checks. If a
	 * required property does not hold, an error tooltip will be shown on the
	 * passed input node. Otherwise the given method is executed to set the
	 * commands.
	 * 
	 * @param commandsText
	 *            the input text for the commands as a single 'n'-separated
	 *            String.
	 * @param paramNum
	 *            the number of parameters this command expects.
	 * @param allowEmpty
	 *            if it is allowed to enter no command
	 * @param inputNode
	 *            the input node used to type the commands
	 * @param setter
	 *            the method to call if the commands are valid
	 */
	protected void updateGameCommands(String commandsText, int paramNum, boolean allowEmpty, Node inputNode,
			StringListSetter setter) {
		boolean errorFound = false;
		String[] lines = commandsText.split("\\n");

		if (commandsText.isEmpty()) {
			if (!allowEmpty) {
				errorFound = true;
				showError(inputNode, COMMAND_EMPTY);
			}
		} else {
			// At least one command was entered
			if (Arrays.stream(lines).anyMatch((s) -> MULTIPLE_BLANKS.matcher(s).find())) {
				errorFound = true;
				showError(inputNode, COMMAND_MULTI_WHITESPACE);
			} else if (Arrays.stream(lines).anyMatch((s) -> BLANKS_BEGINNING_END.matcher(s).find())) {
				errorFound = true;
				showError(inputNode, COMMAND_WHITESPACE_BEGINNING_END);
			} else if (Arrays.stream(lines).anyMatch((s) -> !VALID_SEQS.matcher(s).matches())) {
				errorFound = true;
				showError(inputNode, COMMAND_INVALID_CHAR);
			} else if (Arrays.stream(lines).anyMatch((s) -> !CHAR.matcher(s).find())) {
				errorFound = true;
				showError(inputNode, COMMAND_NO_WORD);
			} else if (Arrays.stream(lines).anyMatch((s) -> !hasMatchingBrackets(s))) {
				errorFound = true;
				showError(inputNode, COMMAND_UNMATCHING_BRACKETS);
			} else {
				// For each param, check if it has the right number of
				// occurrences
				for (int i = 0; i < 2; i++) {
					int num = i;
					int expectedCount = i < paramNum ? 1 : 0;
					if (Arrays.stream(lines).anyMatch((s) -> !checkParamOccurrences(s, num, expectedCount))) {
						errorFound = true;
						String param = i == 0 ? "<A>" : "<B>";
						String err = expectedCount == 0 ? COMMAND_PARAM_WRONG_NOT_ZERO : COMMAND_PARAM_WRONG_NOT_ONE;
						showError(inputNode, String.format(err, param));
						break;
					}
				}
			}
		}

		// Hide error message and set value in DB if everything OK
		if (!errorFound) {
			hideError(inputNode);
			List<String> newCommands = Arrays.stream(lines).map(CommandRegExConverter::convertStringToRegEx)
					.collect(Collectors.toList());
			setter.setList(newCommands);
		}
	}

	/**
	 * Checks if a parameter in a command occurs the right amount of times.
	 * 
	 * @param text
	 *            the command
	 * @param paramNum
	 *            the number of the parameter, {@literal 0} or {@literal 1}
	 * @param expectedCount
	 *            the expected count for this parameter
	 * @return if the parameter was found the right number of times.
	 */
	private boolean checkParamOccurrences(String text, int paramNum, int expectedCount) {
		Pattern patternToFind = paramNum == 0 ? PARAM_A : PARAM_B;
		Matcher matcher = patternToFind.matcher(text);

		int count = 0;
		while (matcher.find()) {
			count++;
		}

		return count == expectedCount;
	}

	/**
	 * Checks if a string has matching square brackets. In this context that
	 * means only depth one of [] pairs with nothing but a-z and ' ' inside.
	 * 
	 * @param text
	 *            the String to test.
	 * @return if it has only matching brackets.
	 */
	private boolean hasMatchingBrackets(String text) {
		Matcher matcher1 = MATCHED_BRACKETS.matcher(text);
		Matcher matcher2 = OPENING_BRACKETS.matcher(text);
		Matcher matcher3 = CLOSING_BRACKETS.matcher(text);
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		while (matcher1.find()) {
			count1++;
		}
		while (matcher2.find()) {
			count2++;
		}
		while (matcher3.find()) {
			count3++;
		}
		return count1 == count2 && count1 == count3;
	}

	/**
	 * Removes an object from the DB, asking for confirmation first.
	 * 
	 * TODO not all text visible
	 */
	protected void removeObject(HasId object, String title, String header, String content) {
		// Show a confirmation dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Update any previous changes first, otherwise the deletion may
				// not work
				currentGameManager.getPersistenceManager().updateChanges();

				// Remove item from DB
				try {
					currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(object);
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
					return;
				}
				// Update the deletion
				currentGameManager.getPersistenceManager().updateChanges();

				// Switch back to previous view
				mwController.popCenterContent();
			}
		});
	}

	/**
	 * Saves the index of the TabPane
	 * 
	 * @param tabPane
	 *            the TabPane
	 */
	protected void saveTabIndex(TabPane tabPane) {
		tabPane.getSelectionModel().select(currentTabIndex);
		tabPane.getSelectionModel().selectedIndexProperty().addListener((f, o, n) -> {
			currentTabIndex = n.intValue();
		});
	}

	/**
	 * Opens this object for editing in a new center view atop the stack.
	 * 
	 * @param o
	 *            the object
	 */
	protected void objectSelected(NamedObject o) {
		if (o == null) {
			return;
		}

		GameDataController c = null;
		String fxml = null;

		if (o.getClass() == Conversation.class) {
			c = new ConversationController(currentGameManager, mwController, (Conversation) o);
			fxml = "view/Conversation.fxml";
		} else if (o.getClass() == ConversationLayer.class) {
			c = new ConversationLayerController(currentGameManager, mwController, (ConversationLayer) o);
			fxml = "view/ConversationLayer.fxml";
		} else if (o.getClass() == ConversationOption.class) {
			c = new ConversationOptionController(currentGameManager, mwController, (ConversationOption) o);
			fxml = "view/ConversationOption.fxml";
		} else if (o.getClass() == InventoryItem.class) {
			c = new InventoryItemController(currentGameManager, mwController, (InventoryItem) o);
			fxml = "view/InventoryItem.fxml";
		} else if (o.getClass() == Item.class) {
			c = new ItemController(currentGameManager, mwController, (Item) o);
			fxml = "view/Item.fxml";
		} else if (o.getClass() == Location.class) {
			c = new LocationController(currentGameManager, mwController, (Location) o);
			fxml = "view/Location.fxml";
		} else if (o.getClass() == Person.class) {
			c = new PersonController(currentGameManager, mwController, (Person) o);
			fxml = "view/Person.fxml";
		} else if (o.getClass() == Way.class) {
			c = new WayController(currentGameManager, mwController, (Way) o);
			fxml = "view/Way.fxml";
		} else if (o.getClass() == AddInventoryItemsAction.class) {
			c = new AIIActionController(currentGameManager, mwController, (AddInventoryItemsAction) o);
			fxml = "view/AIIAction.fxml";
		} else if (o.getClass() == ChangeNDObjectAction.class) {
			c = new ChangeLocationActionController(currentGameManager, mwController, (ChangeNDObjectAction) o);
			fxml = "view/ChangeLocationAction.fxml";
		} else if (o.getClass() == ChangePersonAction.class) {
			c = new ChangePersonActionController(currentGameManager, mwController, (ChangePersonAction) o);
			fxml = "view/ChangePersonAction.fxml";
		} else if (o.getClass() == ChangeWayAction.class) {
			c = new ChangeWayActionController(currentGameManager, mwController, (ChangeWayAction) o);
			fxml = "view/ChangeWayAction.fxml";
		} else if (o.getClass() == EndGameAction.class) {
			c = new EndGameActionController(currentGameManager, mwController, (EndGameAction) o);
			fxml = "view/EndGameAction.fxml";
		} else if (o.getClass() == MoveAction.class) {
			c = new MoveActionController(currentGameManager, mwController, (MoveAction) o);
			fxml = "view/MoveAction.fxml";
		} else if (o.getClass() == MultiAction.class) {
			c = new MultiActionController(currentGameManager, mwController, (MultiAction) o);
			fxml = "view/MultiAction.fxml";
		} else if (o.getClass() == RemoveInventoryItemAction.class) {
			c = new RIIActionController(currentGameManager, mwController, (RemoveInventoryItemAction) o);
			fxml = "view/RIIAction.fxml";
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"An item was intended to be edited, that has no fitting controller: {0} of type {1}",
					new Object[] { o, o.getClass().getSimpleName() });
			return;
		}

		mwController.pushCenterContent(o.getName(), fxml, c, c::controllerFactory);
	}

}
