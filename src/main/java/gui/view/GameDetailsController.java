package gui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.googlecode.lanterna.terminal.Terminal.Color;

import data.Game;
import gui.utility.StringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utility.CommandRegExConverter;

/**
 * Controller for the game details view.
 * 
 * @author Satia
 */
public class GameDetailsController extends GameDataController {

	public static final String GAME_TITLE_EMPTY_TOOLTIP = "The game title must not be empty";
	public static final String GAME_TITLE_CHARS_TOOLTIP = "The game title contains illegal characters";
	public static final String COMMAND_MULTI_WHITESPACE = "A command must not have mutiple white spaces";
	public static final String COMMAND_WHITESPACE_BEGINNING_END = "A command must not have white space in the beginning or end";
	public static final String COMMAND_INVALID_CHAR = "Only lowercase letters a-z and the space character are allowed.\n"
			+ "(Except for the characters '[' and ']' and the sequences '<A>' and '<B>')";
	public static final String COMMAND_NO_WORD = "A command must contain at least one word";
	public static final String COMMAND_UNMATCHING_BRACKETS = "Brackets must be matching and only contain a-z or space characters";
	public static final String COMMAND_PARAM_WRONG_NOT_ONE = "The parameter %s is expected to occur exactly once";
	public static final String COMMAND_PARAM_WRONG_NOT_ZERO = "The parameter %s is notsupported for this command";

	public static final int MIN_OPTION_LINES = 3;
	public static final int MAX_OPTION_LINES = 3;

	/** The Game **/
	private Game game;

	@FXML
	private TextField gameTitleField;
	@FXML
	private TextArea startingTextField;
	@FXML
	private TextField useWithHelpTextField;
	@FXML
	private TextField moveHelpTextField;
	@FXML
	private TextField takeHelpTextField;
	@FXML
	private TextField useHelpTextField;
	@FXML
	private TextField talkHelpTextField;
	@FXML
	private TextField lookAroundHelpTextField;
	@FXML
	private TextField inspectHelpTextField;
	@FXML
	private TextField inventoryHelpTextField;
	@FXML
	private TextField helpHelpTextField;
	@FXML
	private TextField exitHelpTextField;
	@FXML
	private TextField useWithSuccessTextField;
	@FXML
	private TextField takeSuccessTextField;
	@FXML
	private TextField useSuccessTextField;
	@FXML
	private TextField inspectSuccessTextField;
	@FXML
	private TextField emptyInvSuccessTextField;
	@FXML
	private TextField invSuccessTextField;
	@FXML
	private TextField useWithFailureTextField;
	@FXML
	private TextField moveFailureTextField;
	@FXML
	private TextField takeFailureTextField;
	@FXML
	private TextField useFailureTextField;
	@FXML
	private TextField talkFailureTextField;
	@FXML
	private TextField noSuchItemTextField;
	@FXML
	private TextField noSuchInventoryItemTextField;
	@FXML
	private TextField noSuchPersonTextField;
	@FXML
	private TextField noSuchWayTextField;
	@FXML
	private TextField noValidCommandTextField;
	@FXML
	private TextField notSensibleCommandTextField;
	@FXML
	private TextArea useWithCommandsTextField;
	@FXML
	private TextArea moveCommandsTextField;
	@FXML
	private TextArea takeCommandsTextField;
	@FXML
	private TextArea useCommandsTextField;
	@FXML
	private TextArea talkCommandsTextField;
	@FXML
	private TextArea lookAroundCommandsTextField;
	@FXML
	private TextArea inspectCommandsTextField;
	@FXML
	private TextArea inventoryCommandsTextField;
	@FXML
	private TextArea helpCommandsTextField;
	@FXML
	private TextArea exitCommandsTextField;
	@FXML
	private Spinner<Integer> optionLinesSpinner;
	@FXML
	private ComboBox<Color> successfulFGColorPicker;
	@FXML
	private ComboBox<Color> successfulBGColorPicker;
	@FXML
	private ComboBox<Color> neutralFGColorPicker;
	@FXML
	private ComboBox<Color> neutralBGColorPicker;
	@FXML
	private ComboBox<Color> failureFGColorPicker;
	@FXML
	private ComboBox<Color> failureBGColorPicker;

	@FXML
	private void initialize() {
		// Obtain the game
		game = currentGameManager.getPersistenceManager().getGameManager().getGame();
		// Set all GUI fields accordingly
		gameTitleField.setText(game.getGameTitle());
		startingTextField.setText(game.getStartText());
		// TODO the starting location

		useWithHelpTextField.setText(game.getUseWithCombineHelpText());
		moveHelpTextField.setText(game.getMoveHelpText());
		takeHelpTextField.setText(game.getTakeHelpText());
		useHelpTextField.setText(game.getUseHelpText());
		talkHelpTextField.setText(game.getTalkToHelpText());
		lookAroundHelpTextField.setText(game.getLookAroundHelpText());
		inspectHelpTextField.setText(game.getInspectHelpText());
		inventoryHelpTextField.setText(game.getInventoryHelpText());
		helpHelpTextField.setText(game.getHelpHelpText());
		exitHelpTextField.setText(game.getExitCommandHelpText());

		useWithSuccessTextField.setText(game.getUsedWithText());
		takeSuccessTextField.setText(game.getTakenText());
		useSuccessTextField.setText(game.getUsedText());
		inspectSuccessTextField.setText(game.getInspectionDefaultText());
		emptyInvSuccessTextField.setText(game.getInventoryEmptyText());
		invSuccessTextField.setText(game.getInventoryText());

		useWithFailureTextField.setText(game.getNotUsableWithText());
		moveFailureTextField.setText(game.getNotTravelableText());
		takeFailureTextField.setText(game.getNotTakeableText());
		useFailureTextField.setText(game.getNotUsableText());
		talkFailureTextField.setText(game.getNotTalkingToEnabledText());
		noSuchItemTextField.setText(game.getNoSuchItemText());
		noSuchInventoryItemTextField.setText(game.getNoSuchInventoryItemText());
		noSuchPersonTextField.setText(game.getNoSuchPersonText());
		noSuchWayTextField.setText(game.getNoSuchWayText());
		noValidCommandTextField.setText(game.getNoCommandText());
		notSensibleCommandTextField.setText(game.getInvalidCommandText());

		useWithCommandsTextField.setText(getCommandString(game.getUseWithCombineCommands()));
		moveCommandsTextField.setText(getCommandString(game.getMoveCommands()));
		takeCommandsTextField.setText(getCommandString(game.getTakeCommands()));
		useCommandsTextField.setText(getCommandString(game.getUseCommands()));
		talkCommandsTextField.setText(getCommandString(game.getTalkToCommands()));
		lookAroundCommandsTextField.setText(getCommandString(game.getLookAroundCommands()));
		inspectCommandsTextField.setText(getCommandString(game.getInspectCommands()));
		inventoryCommandsTextField.setText(getCommandString(game.getInventoryCommands()));
		helpCommandsTextField.setText(getCommandString(game.getHelpCommands()));
		exitCommandsTextField.setText(getCommandString(game.getExitCommands()));

		SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_OPTION_LINES,
				MAX_OPTION_LINES, game.getNumberOfOptionLines());
		optionLinesSpinner.setValueFactory(svf);

		ObservableList<Color> colors = FXCollections.observableArrayList(Color.values());
		successfulFGColorPicker.setItems(colors);
		successfulFGColorPicker.setValue(game.getSuccessfullFgColor());
		successfulBGColorPicker.setItems(colors);
		successfulBGColorPicker.setValue(game.getSuccessfullBgColor());
		neutralFGColorPicker.setItems(colors);
		neutralFGColorPicker.setValue(game.getNeutralFgColor());
		neutralBGColorPicker.setItems(colors);
		neutralBGColorPicker.setValue(game.getNeutralBgColor());
		failureFGColorPicker.setItems(colors);
		failureFGColorPicker.setValue(game.getFailedFgColor());
		failureBGColorPicker.setItems(colors);
		failureBGColorPicker.setValue(game.getFailedBgColor());

		// Register change listeners on the fields that update the game
		// accordingly
		gameTitleField.textProperty().addListener((f, o, n) -> updateGameTitle(n));
		startingTextField.textProperty().addListener((f, o, n) -> game.setStartText(n));

		// XXX Warnings for empty fields
		useWithHelpTextField.textProperty().addListener((f, o, n) -> game.setUseWithCombineHelpText(n));
		moveHelpTextField.textProperty().addListener((f, o, n) -> game.setMoveHelpText(n));
		takeHelpTextField.textProperty().addListener((f, o, n) -> game.setTakeHelpText(n));
		useHelpTextField.textProperty().addListener((f, o, n) -> game.setUseHelpText(n));
		talkHelpTextField.textProperty().addListener((f, o, n) -> game.setTalkToHelpText(n));
		lookAroundHelpTextField.textProperty().addListener((f, o, n) -> game.setLookAroundHelpText(n));
		inspectHelpTextField.textProperty().addListener((f, o, n) -> game.setInspectHelpText(n));
		inventoryHelpTextField.textProperty().addListener((f, o, n) -> game.setInventoryHelpText(n));
		helpHelpTextField.textProperty().addListener((f, o, n) -> game.setHelpHelpText(n));
		exitHelpTextField.textProperty().addListener((f, o, n) -> game.setExitCommandHelpText(n));

		// XXX placeholder checking
		useWithSuccessTextField.textProperty().addListener((f, o, n) -> game.setUsedWithText(n));
		takeSuccessTextField.textProperty().addListener((f, o, n) -> game.setTakenText(n));
		useSuccessTextField.textProperty().addListener((f, o, n) -> game.setUsedText(n));
		inspectSuccessTextField.textProperty().addListener((f, o, n) -> game.setInspectionDefaultText(n));
		emptyInvSuccessTextField.textProperty().addListener((f, o, n) -> game.setInventoryEmptyText(n));
		invSuccessTextField.textProperty().addListener((f, o, n) -> game.setInventoryText(n));

		useWithFailureTextField.textProperty().addListener((f, o, n) -> game.setNotUsableWithText(n));
		moveFailureTextField.textProperty().addListener((f, o, n) -> game.setNotTravelableText(n));
		takeFailureTextField.textProperty().addListener((f, o, n) -> game.setNotTakeableText(n));
		useFailureTextField.textProperty().addListener((f, o, n) -> game.setNotUsableText(n));
		talkFailureTextField.textProperty().addListener((f, o, n) -> game.setNotTalkingToEnabledText(n));
		noSuchItemTextField.textProperty().addListener((f, o, n) -> game.setNoSuchItemText(n));
		noSuchInventoryItemTextField.textProperty().addListener((f, o, n) -> game.setNoSuchInventoryItemText(n));
		noSuchPersonTextField.textProperty().addListener((f, o, n) -> game.setNoSuchPersonText(n));
		noSuchWayTextField.textProperty().addListener((f, o, n) -> game.setNoSuchWayText(n));
		noValidCommandTextField.textProperty().addListener((f, o, n) -> game.setNoCommandText(n));
		notSensibleCommandTextField.textProperty().addListener((f, o, n) -> game.setInvalidCommandText(n));

		// TODO commands
		useWithCommandsTextField.textProperty().addListener((f, o, n) -> updateGameCommands(n));

		optionLinesSpinner.valueProperty().addListener((f, o, n) -> game.setNumberOfOptionLines(n));
		successfulFGColorPicker.valueProperty().addListener((f, o, n) -> game.setSuccessfullFgColor(n));
		successfulBGColorPicker.valueProperty().addListener((f, o, n) -> game.setSuccessfullBgColor(n));
		neutralFGColorPicker.valueProperty().addListener((f, o, n) -> game.setNeutralFgColor(n));
		neutralBGColorPicker.valueProperty().addListener((f, o, n) -> game.setNeutralBgColor(n));
		failureFGColorPicker.valueProperty().addListener((f, o, n) -> game.setFailedFgColor(n));
		failureBGColorPicker.valueProperty().addListener((f, o, n) -> game.setFailedBgColor(n));
	}

	/**
	 * Validates the game title before actually propagating it to the game. Must
	 * not be empty, must not contain any characters forbidden in filenames.
	 * 
	 * @param newTitle
	 *            the new title
	 */
	private void updateGameTitle(String newTitle) {
		if (newTitle.isEmpty()) {
			showError(gameTitleField, GAME_TITLE_EMPTY_TOOLTIP);
		} else if (StringUtils.isUnsafeFileName(newTitle)) {
			showError(gameTitleField, GAME_TITLE_CHARS_TOOLTIP);
		} else {
			hideError(gameTitleField);
			// Set the value in the game
			game.setGameTitle(newTitle);
		}
	}

	private static String getCommandString(List<String> commands) {
		// Assure no lazy loading DB list is used, therefore copy to a new list
		// The DB lists are incompatible with streams

		// Iterate through the list and
		// convert the RegEx to a more readable form
		return new ArrayList<String>(commands).stream().map(CommandRegExConverter::convertRegExToString)
				.collect(Collectors.joining("\n"));
	}

	// TODO Flexible
	private void updateGameCommands(String commandsText) {
		Pattern MULTIPLE_BLANKS = Pattern.compile("\\p{Blank}{2,}");
		Pattern BLANKS_BEGINNING_END = Pattern.compile("(^\\p{Blank})|(\\p{Blank}$)");
		Pattern VALID_SEQS = Pattern.compile("(([a-z])|[\\[\\] ]|(<(A|B)>))*");
		Pattern CHAR = Pattern.compile("([a-z])");
		
		int paramNum = 2;

		String[] lines = commandsText.split("\\n");
		if (Arrays.stream(lines).anyMatch((s) -> MULTIPLE_BLANKS.matcher(s).find())) {
			showError(useWithCommandsTextField, COMMAND_MULTI_WHITESPACE);
		} else if (Arrays.stream(lines).anyMatch((s) -> BLANKS_BEGINNING_END.matcher(s).find())) {
			showError(useWithCommandsTextField, COMMAND_WHITESPACE_BEGINNING_END);
		} else if (Arrays.stream(lines).anyMatch((s) -> !VALID_SEQS.matcher(s).matches())) {
			showError(useWithCommandsTextField, COMMAND_INVALID_CHAR);
		} else if (Arrays.stream(lines).anyMatch((s) -> !CHAR.matcher(s).find())) {
			showError(useWithCommandsTextField, COMMAND_NO_WORD);
		} else if (Arrays.stream(lines).anyMatch((s) -> !hasMatchingBrackets(s))) {
			showError(useWithCommandsTextField, COMMAND_UNMATCHING_BRACKETS);
		} else if (Arrays.stream(lines).anyMatch((s) -> !hasMatchingBrackets(s))) {
			showError(useWithCommandsTextField, COMMAND_UNMATCHING_BRACKETS);
		} else {
			boolean errorFound = false;
			//For each param, check if it has the right number of occurrences
			for(int i = 0; i < 2; i++) {
				int num = i;
				int expectedCount = i < paramNum ? 1 : 0;
				if (Arrays.stream(lines).anyMatch((s) -> !checkParamOccurrences(s, num, expectedCount))) {
					errorFound = true;
					String param = i == 0 ? "<A>" : "<B>";
					String err = expectedCount == 0 ? COMMAND_PARAM_WRONG_NOT_ZERO : COMMAND_PARAM_WRONG_NOT_ONE;
					showError(useWithCommandsTextField, String.format(err, param));
					break;
				}
			}
			
			if(!errorFound) {
				hideError(useWithCommandsTextField);
				List<String> newCommands = Arrays.stream(lines).map(CommandRegExConverter::convertStringToRegEx)
						.collect(Collectors.toList());
				System.out.println(newCommands);

				game.setUseWithCombineCommands(newCommands);
			}
		}
	}
	
	private boolean checkParamOccurrences(String text, int paramNum, int expectedCount) {
		Pattern PARAM_A = Pattern.compile("(<A>)");
		Pattern PARAM_B = Pattern.compile("(<B>)");
		
		Pattern patternToFind = paramNum == 0 ? PARAM_A : PARAM_B;
		Matcher matcher = patternToFind.matcher(text);
		
		int count = 0;
		while(matcher.find()) {
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
		Pattern MATCHED_BRACKETS = Pattern.compile("(\\[[a-z &&[^\\]]]*\\])");
		Pattern OPENING_BRACKETS = Pattern.compile("(\\[)");
		Pattern CLOSING_BRACKETS = Pattern.compile("(\\])");

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
