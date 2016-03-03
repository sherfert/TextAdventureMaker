package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
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
	 * A method allowing to set commands as a list of strings.
	 * 
	 * @author Satia
	 */
	@FunctionalInterface
	public static interface CommandSetter {
		/**
		 * Sets the commands.
		 * 
		 * @param commands
		 *            the commands
		 */
		public void setCommands(List<String> commands);
	}

	private static final String NODE_PROPERTIES_KEY_ERROR_TOOLTIP = "Error-Tooltip";

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
	 * @param currentGameManager
	 */
	public GameDataController(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;
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

			// Save tooltip in node properties to access it later
			node.getProperties().put(NODE_PROPERTIES_KEY_ERROR_TOOLTIP, tooltip);
		}
	}

	/**
	 * Hide any previous error messages and remove the css class "error"from the
	 * node. It is safe to call this method even though no error was showing
	 * previously
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
		}
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
	 * passed input node. Otheriwse the given method is executed to set the
	 * commands.
	 * 
	 * @param commandsText
	 *            the input text for the commands as a single 'n'-separated
	 *            String.
	 * @param paramNum
	 *            the number of paramters this command expects.
	 * @param inputNode
	 *            the input node used to type the commands
	 * @param setter
	 *            the method to call if the commands are valid
	 */
	protected void updateGameCommands(String commandsText, int paramNum, Node inputNode, CommandSetter setter) {
		String[] lines = commandsText.split("\\n");
		if (Arrays.stream(lines).anyMatch((s) -> MULTIPLE_BLANKS.matcher(s).find())) {
			showError(inputNode, COMMAND_MULTI_WHITESPACE);
		} else if (Arrays.stream(lines).anyMatch((s) -> BLANKS_BEGINNING_END.matcher(s).find())) {
			showError(inputNode, COMMAND_WHITESPACE_BEGINNING_END);
		} else if (Arrays.stream(lines).anyMatch((s) -> !VALID_SEQS.matcher(s).matches())) {
			showError(inputNode, COMMAND_INVALID_CHAR);
		} else if (Arrays.stream(lines).anyMatch((s) -> !CHAR.matcher(s).find())) {
			showError(inputNode, COMMAND_NO_WORD);
		} else if (Arrays.stream(lines).anyMatch((s) -> !hasMatchingBrackets(s))) {
			showError(inputNode, COMMAND_UNMATCHING_BRACKETS);
		} else if (Arrays.stream(lines).anyMatch((s) -> !hasMatchingBrackets(s))) {
			showError(inputNode, COMMAND_UNMATCHING_BRACKETS);
		} else {
			boolean errorFound = false;
			// For each param, check if it has the right number of occurrences
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

			if (!errorFound) {
				hideError(inputNode);
				List<String> newCommands = Arrays.stream(lines).map(CommandRegExConverter::convertStringToRegEx)
						.collect(Collectors.toList());
				setter.setCommands(newCommands);
			}
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

}