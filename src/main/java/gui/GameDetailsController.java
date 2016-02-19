package gui;

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
import logic.CurrentGameManager;

/**
 * Controller for the game details view.
 * 
 * @author Satia
 */
public class GameDetailsController extends GameDataController {

	private static final String GAME_TITLE_EMPTY_TOOLTIP = "The game title must not be empty";
	private static final String GAME_TITLE_CHARS_TOOLTIP = "The game title contains illegal characters";
	private static final int MIN_OPTION_LINES = 3;
	private static final int MAX_OPTION_LINES = 3;

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
	

	/**
	 * @param currentGameManager
	 */
	public GameDetailsController(CurrentGameManager currentGameManager) {
		super(currentGameManager);
	}

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

		useWithCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 2, useWithCommandsTextField, game::setUseWithCombineCommands));
		moveCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, moveCommandsTextField, game::setMoveCommands));
		takeCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, takeCommandsTextField, game::setTakeCommands));
		useCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, useCommandsTextField, game::setUseCommands));
		talkCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, talkCommandsTextField, game::setTalkToCommands));
		lookAroundCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, lookAroundCommandsTextField, game::setLookAroundCommands));
		inspectCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, inspectCommandsTextField, game::setInspectCommands));
		inventoryCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, inventoryCommandsTextField, game::setInventoryCommands));
		helpCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, helpCommandsTextField, game::setHelpCommands));
		exitCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, exitCommandsTextField, game::setExitCommands));

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
}
