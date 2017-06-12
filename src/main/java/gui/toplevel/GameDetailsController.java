package gui.toplevel;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import com.googlecode.lanterna.terminal.Terminal.Color;

import data.Game;
import data.InventoryItem;
import data.Location;
import exception.DBClosedException;
import exception.DBIncompatibleException;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import gui.customui.NamedObjectListView;
import gui.utility.StringUtils;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

	/**
	 * Different placeholder types.
	 * 
	 * XXX not properly aligned, depending on OS Font.
	 * 
	 * @author Satia
	 */
	public enum Placeholder {
		INPUT(Pattern.compile("(<input>|<Input>|<INPUT>)"), "<input>\t\t\tThe input typed by the player.\n"), //
		IDENTIFIER(Pattern.compile("(<identifier>|<Identifier>|<IDENTIFIER>)"),
				"<identifier>\t\tThe identifier used for the first thing in the command.\n"), //
		IDENTIFIER2(Pattern.compile("(<identifier2>|<Identifier2>|<IDENTIFIER2>)"),
				"<identifier2>\t\tThe identifier used for the second thing in the command.\n"), //
		NAME(Pattern.compile("(<name>|<Name>|<NAME>)"), "<name>\t\t\tThe name of the first thing in the command.\n"), //
		NAME2(Pattern.compile("(<name2>|<Name2>|<NAME2>)"),
				"<name2>\t\tThe name of the second thing in the command.\n"), //
		PATTERN(Pattern.compile("(<pattern\\|.*?\\|.*?\\|>)"),
				"<pattern|A|B|>\tSimilar to <input>, but allows to replace the identifiers "
						+ "of the first and second thing in the command with *A* and *B*, respectively.\n");

		/**
		 * the RegEx pattern
		 */
		public Pattern pattern;

		/**
		 * The text displayed in a tooltip for a node allowing this placeholder.
		 */
		public String tooltipText;

		Placeholder(Pattern pattern, String tooltipText) {
			this.pattern = pattern;
			this.tooltipText = tooltipText;
		}
	}

	private static final Pattern ANY_PLACEHOLDER = Pattern.compile("(<pattern\\|(?<p0>.*?)\\|(?<p1>.*?)\\|>|<.*?>)");

	private static final String GAME_TITLE_EMPTY_TOOLTIP = "The game title must not be empty";
	private static final String GAME_TITLE_CHARS_TOOLTIP = "The game title contains illegal characters";
	private static final int MIN_OPTION_LINES = 3;
	private static final int MAX_OPTION_LINES = 20;

	/** The Game **/
	private Game game;

	@FXML
	private TextField gameTitleField;
	@FXML
	private TextArea startingTextField;
	@FXML
	private NamedObjectChooser<Location> startLocationChooser;
	@FXML
	private NamedObjectListView<InventoryItem> startItemsListView;
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

	private Property<Integer> numOptionLinesProp;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public GameDetailsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	private void initialize() throws DBIncompatibleException, DBClosedException {
		// Obtain the game
		try {
			game = currentGameManager.getPersistenceManager().getGameManager().getGame();
		} catch (DBIncompatibleException | PersistenceException | DBClosedException e) {
			// This means the database is incompatible with the model.
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Could not get the game. Database incompatible.", e);

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Could not load the game");
			alert.setHeaderText("The loaded file seems to be incompatible");
			alert.setContentText(e.getMessage());
			alert.showAndWait();

			// Rethrow e so that the GUI loading can take appropriate actions
			throw e;
		}
		// Refresh the displayed object
		currentGameManager.getPersistenceManager().refreshEntity(game);

		// Set all GUI fields accordingly
		gameTitleField.setText(game.getGameTitle());
		setNodeTooltip(gameTitleField, "The title of the game is shown in the title bar of the console window. "
				+ "It also determines the save location on the hard disk. It should be a unique name.");

		startingTextField.textProperty().bindBidirectional(game.startTextProperty());
		startingTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, startingTextField));
		setNodeTooltip(startingTextField, "This text is displayed at the beginning of the game and should "
				+ "introduce the setting of the game.");

		startLocationChooser.initialize(
				currentGameManager.getPersistenceManager().getGameManager().getGame().getStartLocation(), false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (loc) -> {
					try {
						currentGameManager.getPersistenceManager().getGameManager().getGame().setStartLocation(loc);
					} catch (Exception e) {
						Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not set start location.",
								e);
					}
				});
		setNodeTooltip(startLocationChooser,
				"This is where the game will start. After the starting text, "
						+ "information about this location is displayed. This is the description of the room and "
						+ "descriptions of all persons, items and ways of this location.");

		startItemsListView.initialize(game.getStartItems(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (item) -> game.addStartItem(item), (item) -> game.removeStartItem(item));
		setNodeTooltip(startItemsListView,
				"These items are placed in the player's inventory in the beginning of the game.");

		final String helpTextFieldTooltipText = "The text is displayed along with the respective commands "
				+ "when the player displays the help "
				+ "and should convey to the player what sort of command this is.";

		useWithHelpTextField.textProperty().bindBidirectional(game.useWithCombineHelpTextProperty());
		useWithHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, useWithHelpTextField));
		setNodeTooltip(useWithHelpTextField, helpTextFieldTooltipText);

		moveHelpTextField.textProperty().bindBidirectional(game.moveHelpTextProperty());
		moveHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, moveHelpTextField));
		setNodeTooltip(moveHelpTextField, helpTextFieldTooltipText);

		takeHelpTextField.textProperty().bindBidirectional(game.takeHelpTextProperty());
		takeHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, takeHelpTextField));
		setNodeTooltip(takeHelpTextField, helpTextFieldTooltipText);

		useHelpTextField.textProperty().bindBidirectional(game.useHelpTextProperty());
		useHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, useHelpTextField));
		setNodeTooltip(useHelpTextField, helpTextFieldTooltipText);

		talkHelpTextField.textProperty().bindBidirectional(game.talkToHelpTextProperty());
		talkHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, talkHelpTextField));
		setNodeTooltip(talkHelpTextField, helpTextFieldTooltipText);

		lookAroundHelpTextField.textProperty().bindBidirectional(game.lookAroundHelpTextProperty());
		lookAroundHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, lookAroundHelpTextField));
		setNodeTooltip(lookAroundHelpTextField, helpTextFieldTooltipText);

		inspectHelpTextField.textProperty().bindBidirectional(game.inspectHelpTextProperty());
		inspectHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, inspectHelpTextField));
		setNodeTooltip(inspectHelpTextField, helpTextFieldTooltipText);

		inventoryHelpTextField.textProperty().bindBidirectional(game.inventoryHelpTextProperty());
		inventoryHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, inventoryHelpTextField));
		setNodeTooltip(inventoryHelpTextField, helpTextFieldTooltipText);

		helpHelpTextField.textProperty().bindBidirectional(game.helphelpTextProperty());
		helpHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, helpHelpTextField));
		setNodeTooltip(helpHelpTextField, helpTextFieldTooltipText);

		exitHelpTextField.textProperty().bindBidirectional(game.exitCommandHelpTextProperty());
		exitHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, exitHelpTextField));
		setNodeTooltip(exitHelpTextField, helpTextFieldTooltipText);

		// Different Sets with allowed placeholders
		Set<Placeholder> input = EnumSet.of(Placeholder.INPUT);
		Set<Placeholder> inputAndPattern = EnumSet.of(Placeholder.INPUT, Placeholder.PATTERN);
		Set<Placeholder> inputPatternID = EnumSet.of(Placeholder.INPUT, Placeholder.IDENTIFIER, Placeholder.PATTERN);
		Set<Placeholder> noSecondPL = EnumSet.of(Placeholder.INPUT, Placeholder.IDENTIFIER, Placeholder.NAME,
				Placeholder.PATTERN);
		Set<Placeholder> allPL = EnumSet.allOf(Placeholder.class);

		useWithSuccessTextField.textProperty().bindBidirectional(game.usedWithTextProperty());
		useWithSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useWithSuccessTextField, allPL));
		addDefaultTextTooltip(useWithSuccessTextField,
				"This is the default text when an item is successfully used with another item.", allPL);

		takeSuccessTextField.textProperty().bindBidirectional(game.takenTextProperty());
		takeSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, takeSuccessTextField, noSecondPL));
		addDefaultTextTooltip(takeSuccessTextField, "This is the default text when an item is took.", noSecondPL);

		useSuccessTextField.textProperty().bindBidirectional(game.usedTextProperty());
		useSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useSuccessTextField, noSecondPL));
		addDefaultTextTooltip(useSuccessTextField, "This is the default text when an item is successfully used.",
				noSecondPL);

		inspectSuccessTextField.textProperty().bindBidirectional(game.inspectionDefaultTextProperty());
		inspectSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, inspectSuccessTextField, noSecondPL));
		addDefaultTextTooltip(inspectSuccessTextField, "This is the default text when an item is inspected.",
				noSecondPL);

		emptyInvSuccessTextField.textProperty().bindBidirectional(game.inventoryEmptyTextProperty());
		emptyInvSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, emptyInvSuccessTextField, inputAndPattern));
		addDefaultTextTooltip(emptyInvSuccessTextField, "This is the default text to indicate an empty inventory.",
				inputAndPattern);

		invSuccessTextField.textProperty().bindBidirectional(game.inventoryTextProperty());
		invSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, invSuccessTextField, inputAndPattern));
		addDefaultTextTooltip(invSuccessTextField,
				"This is the default text placed before listing all inventory items.", inputAndPattern);

		useWithFailureTextField.textProperty().bindBidirectional(game.notUsableWithTextProperty());
		useWithFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useWithFailureTextField, allPL));
		addDefaultTextTooltip(useWithFailureTextField,
				"This is the default text when an item is unsuccessfully used with another item.", allPL);

		moveFailureTextField.textProperty().bindBidirectional(game.notTravelableTextProperty());
		moveFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, moveFailureTextField, noSecondPL));
		addDefaultTextTooltip(moveFailureTextField,
				"This is the default text when the player tries to move to a different location, unsuccessfully.",
				noSecondPL);

		takeFailureTextField.textProperty().bindBidirectional(game.notTakeableTextProperty());
		takeFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, takeFailureTextField, noSecondPL));
		addDefaultTextTooltip(takeFailureTextField,
				"This is the default text when the player tries to take an item, unsuccessfully.", noSecondPL);

		useFailureTextField.textProperty().bindBidirectional(game.notUsableTextProperty());
		useFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useFailureTextField, noSecondPL));
		addDefaultTextTooltip(useFailureTextField, "This is the default text when an item is unsuccessfully used.",
				noSecondPL);

		talkFailureTextField.textProperty().bindBidirectional(game.notTalkingToEnabledTextProperty());
		talkFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, talkFailureTextField, noSecondPL));
		addDefaultTextTooltip(talkFailureTextField,
				"This is the default text when the player tries to talk to a person, unsuccessfully.", noSecondPL);

		noSuchItemTextField.textProperty().bindBidirectional(game.noSuchItemTextProperty());
		noSuchItemTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchItemTextField, inputPatternID));
		addDefaultTextTooltip(noSuchItemTextField,
				"This is the default text when an item to be used, took, or inspected is not found.", inputPatternID);

		noSuchInventoryItemTextField.textProperty().bindBidirectional(game.noSuchInventoryItemTextProperty());
		noSuchInventoryItemTextField.textProperty().addListener(
				(f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchInventoryItemTextField, inputPatternID));
		addDefaultTextTooltip(noSuchInventoryItemTextField,
				"This is the default text when an inventory item to be used or inspected is not found.",
				inputPatternID);

		noSuchPersonTextField.textProperty().bindBidirectional(game.noSuchPersonTextProperty());
		noSuchPersonTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchPersonTextField, inputPatternID));
		addDefaultTextTooltip(noSuchPersonTextField,
				"This is the default text when an person to talk to or inspect is not found.", inputPatternID);

		noSuchWayTextField.textProperty().bindBidirectional(game.noSuchWayTextProperty());
		noSuchWayTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchWayTextField, inputPatternID));
		addDefaultTextTooltip(noSuchWayTextField, "This is the default text when a way to move by is not found.",
				inputPatternID);

		noValidCommandTextField.textProperty().bindBidirectional(game.noCommandTextProperty());
		noValidCommandTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noValidCommandTextField, input));
		addDefaultTextTooltip(noValidCommandTextField,
				"This is the default text when the command typed by the player cannot be understood.", input);

		notSensibleCommandTextField.textProperty().bindBidirectional(game.invalidCommandTextProperty());
		notSensibleCommandTextField.textProperty().addListener(
				(f, o, n) -> checkPlaceholdersAndEmptiness(n, notSensibleCommandTextField, inputAndPattern));
		addDefaultTextTooltip(notSensibleCommandTextField,
				"This is the default text when the player used an additional command not associated with that item or person. "
						+ "For example: There is an apple in your game, and you include 'eat <A>' as an additional use command for the apple. "
						+ "If the player now tries to eat a table, this text will be the response.",
				inputPatternID);

		useWithCommandsTextField.setText(getCommandString(game.getUseWithCombineCommands()));
		addCommandTooltip(useWithCommandsTextField,
				"These commands enable the user to use one object with another. "
						+ "At least one item must be in the player's inventory. The other can be an item in the inventory, "
						+ "an item or a person in the current location.");

		moveCommandsTextField.setText(getCommandString(game.getMoveCommands()));
		addCommandTooltip(moveCommandsTextField,
				"These commands enable the user to move to a different location, using a way.");

		takeCommandsTextField.setText(getCommandString(game.getTakeCommands()));
		addCommandTooltip(takeCommandsTextField,
				"These commands enable the user to take an item. The item must be in the current location.");

		useCommandsTextField.setText(getCommandString(game.getUseCommands()));
		addCommandTooltip(useCommandsTextField,
				"These commands enable the user to use an inventory item or an item in the current location.");

		talkCommandsTextField.setText(getCommandString(game.getTalkToCommands()));
		addCommandTooltip(talkCommandsTextField,
				"These commands enable the user to talk to a person in the current location.");

		lookAroundCommandsTextField.setText(getCommandString(game.getLookAroundCommands()));
		addCommandTooltip(lookAroundCommandsTextField,
				"These commands enable the user to look around. Information about this location is displayed. "
						+ "This is the description of the room and "
						+ "descriptions of all persons, items and ways of this location.");

		inspectCommandsTextField.setText(getCommandString(game.getInspectCommands()));
		addCommandTooltip(inspectCommandsTextField,
				"These commands enable the user to inspect an inventory item or an item, person or way in the current location.");

		inventoryCommandsTextField.setText(getCommandString(game.getInventoryCommands()));
		addCommandTooltip(inventoryCommandsTextField,
				"These commands enable the user to inspect the inventory contents.");

		helpCommandsTextField.setText(getCommandString(game.getHelpCommands()));
		addCommandTooltip(helpCommandsTextField, "These commands show the help to the user.");

		exitCommandsTextField.setText(getCommandString(game.getExitCommands()));
		addCommandTooltip(exitCommandsTextField, "These commands exit the game and close the application.");

		SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_OPTION_LINES,
				MAX_OPTION_LINES, game.getNumberOfOptionLines());
		// The value numOptionLinesProp must NOT be stored in a local variable
		// or passed directly. Only the class field suffices.
		// Since JavaFX uses weak references for bidirectional bindings, it will
		// get garbage collected otherwise.
		numOptionLinesProp = game.numberOfOptionLinesProperty().asObject();
		svf.valueProperty().bindBidirectional(numOptionLinesProp);
		optionLinesSpinner.setValueFactory(svf);
		setNodeTooltip(optionLinesSpinner,
				"In a dialog or in the game menu, this number specified how many lines of options "
						+ "should be shown simultaneously.");

		ObservableList<Color> colors = FXCollections.observableArrayList(Color.values());
		successfulFGColorPicker.setItems(colors);
		successfulFGColorPicker.valueProperty().bindBidirectional(game.successfullFgColorProperty());
		setNodeTooltip(successfulFGColorPicker,
				"This is the font color used for successful actions and in a dialog for anything "
						+ "the other person says.");

		successfulBGColorPicker.setItems(colors);
		successfulBGColorPicker.valueProperty().bindBidirectional(game.successfullBgColorProperty());
		setNodeTooltip(successfulBGColorPicker,
				"This is the background color used for successful actions and in a dialog for anything "
						+ "the other person says.");

		neutralFGColorPicker.setItems(colors);
		neutralFGColorPicker.valueProperty().bindBidirectional(game.neutralFgColorProperty());
		setNodeTooltip(neutralFGColorPicker,
				"This is the font color used for looking around, inspecting things, your inventory, "
						+ "and additional events during dialogues.");

		neutralBGColorPicker.setItems(colors);
		neutralBGColorPicker.valueProperty().bindBidirectional(game.neutralBgColorProperty());
		setNodeTooltip(neutralBGColorPicker,
				"This is the background color used for looking around, inspecting things, your inventory, "
						+ "and additional events during dialogues.");

		failureFGColorPicker.setItems(colors);
		failureFGColorPicker.valueProperty().bindBidirectional(game.failedFgColorProperty());
		setNodeTooltip(failureFGColorPicker, "This is the font color used for failed actions.");

		failureBGColorPicker.setItems(colors);
		failureBGColorPicker.valueProperty().bindBidirectional(game.failedBgColorProperty());
		setNodeTooltip(failureBGColorPicker, "This is the background color used for failed actions.");

		// Register change listeners on the fields that update the game
		// accordingly, where sanity checks need to be made (no bidirectional
		// binding)
		gameTitleField.textProperty().addListener((f, o, n) -> updateGameTitle(n));

		useWithCommandsTextField.textProperty().addListener((f, o, n) -> updateGameCommands(n, 2, false,
				useWithCommandsTextField, game::setUseWithCombineCommands));
		moveCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, false, moveCommandsTextField, game::setMoveCommands));
		takeCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, false, takeCommandsTextField, game::setTakeCommands));
		useCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 1, false, useCommandsTextField, game::setUseCommands));
		talkCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, false, talkCommandsTextField, game::setTalkToCommands));
		lookAroundCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, false, lookAroundCommandsTextField, game::setLookAroundCommands));
		inspectCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 1, false, inspectCommandsTextField, game::setInspectCommands));
		inventoryCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, false, inventoryCommandsTextField, game::setInventoryCommands));
		helpCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, false, helpCommandsTextField, game::setHelpCommands));
		exitCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, false, exitCommandsTextField, game::setExitCommands));
	}

	/**
	 * Adds a tooltip to the default text TextFields.
	 * 
	 * @param node
	 *            the node
	 * @param explanation
	 *            the introduction of the tooltip.
	 * @param allowedPL
	 *            the set of allowed placeholders to explain.
	 */
	private void addDefaultTextTooltip(Node node, String explanation, Set<Placeholder> allowedPL) {
		final String placeHolderIntroduction = "You can also include placeholders, "
				+ "which will be replaced in the game. The following placeholders are allowed:\n";

		StringBuilder sb = new StringBuilder();
		sb.append(explanation).append(' ');
		sb.append(placeHolderIntroduction);
		for (Placeholder p : allowedPL) {
			sb.append(p.tooltipText);
		}
		setNodeTooltip(node, sb.toString());
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
			showError(gameTitleField, GAME_TITLE_EMPTY_TOOLTIP, TooltipSeverity.ERROR);
		} else if (StringUtils.isUnsafeFileName(newTitle)) {
			showError(gameTitleField, GAME_TITLE_CHARS_TOOLTIP, TooltipSeverity.ERROR);
		} else {
			hideError(gameTitleField);
			// Set the value in the game
			game.setGameTitle(newTitle);
		}
	}

	/**
	 * Shows or removes css and tooltips to warn if a placeholder is invalid or
	 * if a field is empty.
	 * 
	 * @param input
	 *            the entered text input
	 * @param inputNode
	 *            the node used to enter the input
	 * @param allowedPlaceholders
	 *            the placeholders supported by this field.
	 */
	private void checkPlaceholdersAndEmptiness(String input, Node inputNode, Set<Placeholder> allowedPlaceholders) {
		StringBuilder msg = new StringBuilder();
		Matcher wholeMatcher = ANY_PLACEHOLDER.matcher(input);
		Queue<Matcher> q = new LinkedList<Matcher>();
		q.add(wholeMatcher);
		while (!q.isEmpty()) {
			Matcher m = q.remove();
			while (m.find()) {
				String usedPL = m.group();
				String p0 = m.group("p0");
				if (p0 != null && !p0.isEmpty()) {
					q.add(ANY_PLACEHOLDER.matcher(p0));
				}
				String p1 = m.group("p1");
				if (p1 != null && !p1.isEmpty()) {
					q.add(ANY_PLACEHOLDER.matcher(p1));
				}

				if (!allowedPlaceholders.stream().anyMatch((p) -> p.pattern.matcher(usedPL).matches())) {
					msg.append("Placeholder " + usedPL + "is not recognized.\n");
				}

			}
		}

		// Show or hide warning
		if (msg.length() != 0) {
			showError(inputNode, msg.toString(), TooltipSeverity.WARNING);
		} else if (input.isEmpty()) {
			showError(inputNode, INPUT_EMPTY, TooltipSeverity.WARNING);
		} else {
			hideError(inputNode);
		}
	}

	@Override
	public boolean isObsolete() {
		return false;
	}
}
