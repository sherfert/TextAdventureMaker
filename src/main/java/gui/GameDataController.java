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
import data.Person;
import data.Way;
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
import data.interfaces.HasId;
import data.interfaces.HasName;
import exception.DBClosedException;
import gui.itemEditing.ConversationController;
import gui.itemEditing.ConversationLayerController;
import gui.itemEditing.ConversationOptionController;
import gui.itemEditing.InventoryItemController;
import gui.itemEditing.ItemController;
import gui.itemEditing.LocationController;
import gui.itemEditing.PersonController;
import gui.itemEditing.WayController;
import gui.itemEditing.action.AIIActionController;
import gui.itemEditing.action.ChangeActionActionController;
import gui.itemEditing.action.ChangeCombineInformationActionController;
import gui.itemEditing.action.ChangeConversationActionController;
import gui.itemEditing.action.ChangeConversationOptionActionController;
import gui.itemEditing.action.ChangeInventoryItemActionController;
import gui.itemEditing.action.ChangeItemActionController;
import gui.itemEditing.action.ChangeLocationActionController;
import gui.itemEditing.action.ChangePersonActionController;
import gui.itemEditing.action.ChangeUseWithInformationActionController;
import gui.itemEditing.action.ChangeWayActionController;
import gui.itemEditing.action.EndGameActionController;
import gui.itemEditing.action.MoveActionController;
import gui.itemEditing.action.MultiActionController;
import gui.itemEditing.action.RIIActionController;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import logic.CurrentGameManager;
import utility.CommandRegExConverter;
import utility.WindowUtil;

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

	/**
	 * The severity of a tooltip shown on an input field.
	 * 
	 * @author Satia
	 */
	public enum TooltipSeverity {
		WARNING, ERROR
	}

	private static final String NODE_PROPERTIES_KEY_ERROR_TOOLTIP = "Error-Tooltip";
	private static final String NODE_PROPERTIES_KEY_ERROR_FOCUSLISTENER = "Error-Tooltip-FocusListener";
	private static final String NODE_PROPERTIES_KEY_TOOLTIP_MOUSEENTER_LISTENER = "Tooltip-MouseEnterListener";
	private static final String NODE_PROPERTIES_KEY_TOOLTIP_MOUSEEXIT_LISTENER = "Tooltip-MouseExitListener";
	private static final String NODE_PROPERTIES_KEY_TOOLTIP = "Tooltip";

	protected static final String INPUT_EMPTY = "It is not recommended to leave this empty";

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
	 * Controllers can become obsolete if an item they manage has been deleted
	 * from the database.
	 * 
	 * @return if the controller has become obsolete.
	 */
	public abstract boolean isObsolete();

	/**
	 * Point below the node.
	 */
	protected Point2D pointBelow(Node node) {
		return node.localToScreen(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMaxY());
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
	 * @param severity
	 *            the severity of the tooltip
	 */
	protected void showError(Node node, String errorMessage, TooltipSeverity severity) {
		String css;
		switch (severity) {
		case WARNING:
			css = "warning";
			break;
		case ERROR:
			css = "error";
			break;
		default:
			css = "";
		}

		if (!node.getStyleClass().contains(css)) {
			// Apply some css to the node for BG
			node.getStyleClass().add(css);
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
			Point2D p = pointBelow(node);
			tooltip.show(node, p.getX(), p.getY());

			ChangeListener<? super Boolean> focusListener = (f, o, n) -> {
				if (n) {
					Point2D p2 = pointBelow(node);
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

		// Do not show regular tooltips while this one is in place
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_TOOLTIP)) {
			Tooltip tooltip = (Tooltip) node.getProperties().get(NODE_PROPERTIES_KEY_TOOLTIP);
			tooltip.hide();
			// Unset tooltip property
			node.getProperties().remove(NODE_PROPERTIES_KEY_TOOLTIP);

			// Remove listeners
			node.setOnMouseEntered(null);
			node.setOnMouseExited(null);
		}
	}

	/**
	 * Hide any previous error messages and remove the css class
	 * "error"/"warning" from the node. It is safe to call this method even
	 * though no error was showing previously.
	 * 
	 * @param node
	 *            the node
	 */
	@SuppressWarnings("unchecked")
	protected void hideError(Node node) {
		// Remove css class
		node.getStyleClass().remove("error");
		node.getStyleClass().remove("warning");
		// Unset any tooltip message
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_ERROR_TOOLTIP)) {
			Tooltip tooltip = (Tooltip) node.getProperties().get(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);
			tooltip.hide();
			// Unset tooltip property
			node.getProperties().remove(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);

			// Remove focus listener
			ChangeListener<? super Boolean> focusListener = (ChangeListener<? super Boolean>) node.getProperties()
					.get(NODE_PROPERTIES_KEY_ERROR_FOCUSLISTENER);
			node.focusedProperty().removeListener(focusListener);
		}
		// Show regular tooltips again
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_TOOLTIP_MOUSEENTER_LISTENER)) {
			node.setOnMouseEntered((EventHandler<MouseEvent>) node.getProperties()
					.get(NODE_PROPERTIES_KEY_TOOLTIP_MOUSEENTER_LISTENER));
		}
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_TOOLTIP_MOUSEEXIT_LISTENER)) {
			node.setOnMouseExited((EventHandler<MouseEvent>) node.getProperties()
					.get(NODE_PROPERTIES_KEY_TOOLTIP_MOUSEEXIT_LISTENER));
		}
	}

	/**
	 * Defines a tooltip to be shown for the duration of the mouse being inside
	 * the node. Hidden immediately when the mouse exits.
	 * 
	 * @param node
	 *            the node.
	 * @param tooltip
	 *            the tooltip.
	 */
	protected void setNodeTooltip(Node node, Tooltip tooltip) {
		EventHandler<MouseEvent> enterHandler = (e) -> {
			Point2D p = pointBelow(node);
			tooltip.show(node, p.getX(), p.getY());
		};

		EventHandler<MouseEvent> exitHandler = (e) -> {
			tooltip.hide();
		};

		node.setOnMouseEntered(enterHandler);
		node.setOnMouseExited(exitHandler);

		node.getProperties().put(NODE_PROPERTIES_KEY_TOOLTIP_MOUSEENTER_LISTENER, enterHandler);
		node.getProperties().put(NODE_PROPERTIES_KEY_TOOLTIP_MOUSEEXIT_LISTENER, exitHandler);
		node.getProperties().put(NODE_PROPERTIES_KEY_TOOLTIP, tooltip);
	}

	/**
	 * Defines a tooltip to be shown for the duration of the mouse being inside
	 * the node. Hidden immediately when the mouse exits.
	 * 
	 * @param node
	 *            the node.
	 * @param text
	 *            the text of the tooltip.
	 */
	protected void setNodeTooltip(Node node, String text) {
		setNodeTooltip(node, new Tooltip(text));
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
	 * Shows or removes css and tooltips to warn if a field is empty.
	 * 
	 * @param input
	 *            the enetred text input
	 * @param inputNode
	 *            the node used to enter the input
	 */
	protected void warnOnEmpty(String input, Node inputNode) {
		if (input.isEmpty()) {
			showError(inputNode, INPUT_EMPTY, TooltipSeverity.WARNING);
		} else {
			hideError(inputNode);
		}
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
				showError(inputNode, COMMAND_EMPTY, TooltipSeverity.ERROR);
			}
		} else {
			// At least one command was entered
			if (Arrays.stream(lines).anyMatch((s) -> MULTIPLE_BLANKS.matcher(s).find())) {
				errorFound = true;
				showError(inputNode, COMMAND_MULTI_WHITESPACE, TooltipSeverity.ERROR);
			} else if (Arrays.stream(lines).anyMatch((s) -> BLANKS_BEGINNING_END.matcher(s).find())) {
				errorFound = true;
				showError(inputNode, COMMAND_WHITESPACE_BEGINNING_END, TooltipSeverity.ERROR);
			} else if (Arrays.stream(lines).anyMatch((s) -> !VALID_SEQS.matcher(s).matches())) {
				errorFound = true;
				showError(inputNode, COMMAND_INVALID_CHAR, TooltipSeverity.ERROR);
			} else if (Arrays.stream(lines).anyMatch((s) -> !CHAR.matcher(s).find())) {
				errorFound = true;
				showError(inputNode, COMMAND_NO_WORD, TooltipSeverity.ERROR);
			} else if (Arrays.stream(lines).anyMatch((s) -> !hasMatchingBrackets(s))) {
				errorFound = true;
				showError(inputNode, COMMAND_UNMATCHING_BRACKETS, TooltipSeverity.ERROR);
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
						showError(inputNode, String.format(err, param), TooltipSeverity.ERROR);
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
	 */
	protected void removeObject(HasId object, String title, String header, String content) {
		// Show a confirmation dialog
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

		Image img = new Image(WindowUtil.getWindowIconURL().toString());
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(img);

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Update any previous changes first, otherwise the deletion may
				// not work
				currentGameManager.getPersistenceManager().updateChanges();

				// Remove item from DB
				try {
					currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(object);
				} catch (DBClosedException e) {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed", e);
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
	protected void objectSelected(HasName o) {
		if (o == null) {
			return;
		}

		GameDataController c = null;
		String fxml = null;

		if (o.getClass() == Conversation.class) {
			c = new ConversationController(currentGameManager, mwController, (Conversation) o);
			fxml = "view/itemEditing/Conversation.fxml";
		} else if (o.getClass() == ConversationLayer.class) {
			c = new ConversationLayerController(currentGameManager, mwController, (ConversationLayer) o);
			fxml = "view/itemEditing/ConversationLayer.fxml";
		} else if (o.getClass() == ConversationOption.class) {
			c = new ConversationOptionController(currentGameManager, mwController, (ConversationOption) o);
			fxml = "view/itemEditing/ConversationOption.fxml";
		} else if (o.getClass() == InventoryItem.class) {
			c = new InventoryItemController(currentGameManager, mwController, (InventoryItem) o);
			fxml = "view/itemEditing/InventoryItem.fxml";
		} else if (o.getClass() == Item.class) {
			c = new ItemController(currentGameManager, mwController, (Item) o);
			fxml = "view/itemEditing/Item.fxml";
		} else if (o.getClass() == Location.class) {
			c = new LocationController(currentGameManager, mwController, (Location) o);
			fxml = "view/itemEditing/Location.fxml";
		} else if (o.getClass() == Person.class) {
			c = new PersonController(currentGameManager, mwController, (Person) o);
			fxml = "view/itemEditing/Person.fxml";
		} else if (o.getClass() == Way.class) {
			c = new WayController(currentGameManager, mwController, (Way) o);
			fxml = "view/itemEditing/Way.fxml";
		} else if (o.getClass() == AddInventoryItemsAction.class) {
			c = new AIIActionController(currentGameManager, mwController, (AddInventoryItemsAction) o);
			fxml = "view/itemEditing/action/AIIAction.fxml";
		} else if (o.getClass() == ChangeActionAction.class) {
			c = new ChangeActionActionController(currentGameManager, mwController, (ChangeActionAction) o);
			fxml = "view/itemEditing/action/ChangeActionAction.fxml";
		} else if (o.getClass() == ChangeCombineInformationAction.class) {
			c = new ChangeCombineInformationActionController(currentGameManager, mwController,
					(ChangeCombineInformationAction) o);
			fxml = "view/itemEditing/action/ChangeCombineInformationAction.fxml";
		} else if (o.getClass() == ChangeConversationAction.class) {
			c = new ChangeConversationActionController(currentGameManager, mwController, (ChangeConversationAction) o);
			fxml = "view/itemEditing/action/ChangeConversationAction.fxml";
		} else if (o.getClass() == ChangeConversationOptionAction.class) {
			c = new ChangeConversationOptionActionController(currentGameManager, mwController,
					(ChangeConversationOptionAction) o);
			fxml = "view/itemEditing/action/ChangeConversationOptionAction.fxml";
		} else if (o.getClass() == ChangeItemAction.class) {
			c = new ChangeItemActionController(currentGameManager, mwController, (ChangeItemAction) o);
			fxml = "view/itemEditing/action/ChangeItemAction.fxml";
		} else if (o.getClass() == ChangeNDObjectAction.class) {
			c = new ChangeLocationActionController(currentGameManager, mwController, (ChangeNDObjectAction) o);
			fxml = "view/itemEditing/action/ChangeLocationAction.fxml";
		} else if (o.getClass() == ChangePersonAction.class) {
			c = new ChangePersonActionController(currentGameManager, mwController, (ChangePersonAction) o);
			fxml = "view/itemEditing/action/ChangePersonAction.fxml";
		} else if (o.getClass() == ChangeUsableObjectAction.class) {
			c = new ChangeInventoryItemActionController(currentGameManager, mwController, (ChangeUsableObjectAction) o);
			fxml = "view/itemEditing/action/ChangeInventoryItemAction.fxml";
		} else if (o.getClass() == ChangeUseWithInformationAction.class) {
			c = new ChangeUseWithInformationActionController(currentGameManager, mwController,
					(ChangeUseWithInformationAction) o);
			fxml = "view/itemEditing/action/ChangeUseWithInformationAction.fxml";
		} else if (o.getClass() == ChangeWayAction.class) {
			c = new ChangeWayActionController(currentGameManager, mwController, (ChangeWayAction) o);
			fxml = "view/itemEditing/action/ChangeWayAction.fxml";
		} else if (o.getClass() == EndGameAction.class) {
			c = new EndGameActionController(currentGameManager, mwController, (EndGameAction) o);
			fxml = "view/itemEditing/action/EndGameAction.fxml";
		} else if (o.getClass() == MoveAction.class) {
			c = new MoveActionController(currentGameManager, mwController, (MoveAction) o);
			fxml = "view/itemEditing/action/MoveAction.fxml";
		} else if (o.getClass() == MultiAction.class) {
			c = new MultiActionController(currentGameManager, mwController, (MultiAction) o);
			fxml = "view/itemEditing/action/MultiAction.fxml";
		} else if (o.getClass() == RemoveInventoryItemAction.class) {
			c = new RIIActionController(currentGameManager, mwController, (RemoveInventoryItemAction) o);
			fxml = "view/itemEditing/action/RIIAction.fxml";
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"An item was intended to be edited, that has no fitting controller: {0} of type {1}",
					new Object[] { o, o.getClass().getSimpleName() });
			return;
		}

		mwController.pushCenterContent(o.getName(), fxml, c, c::controllerFactory);
	}

}
