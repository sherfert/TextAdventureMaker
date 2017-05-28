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
	 * @author Satia
	 */
	public enum Placeholder {
		INPUT(Pattern.compile("(<input>|<Input>|<INPUT>)")), //
		IDENTIFIER(Pattern.compile("(<identifier>|<Identifier>|<IDENTIFIER>)")), //
		IDENTIFIER2(Pattern.compile("(<identifier2>|<Identifier2>|<IDENTIFIER2>)")), //
		NAME(Pattern.compile("(<name>|<Name>|<NAME>)")), //
		NAME2(Pattern.compile("(<name2>|<Name2>|<NAME2>)")), //
		PATTERN(Pattern.compile("(<pattern\\|.*?\\|.*?\\|>)"));

		/**
		 * the RegEx pattern
		 */
		public Pattern pattern;

		/**
		 * @param pattern
		 *            the RegEx pattern
		 */
		Placeholder(Pattern pattern) {
			this.pattern = pattern;
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
		setNodeTooltip(gameTitleField, "The title of the game is shown when the game is started.");
		
		startingTextField.textProperty().bindBidirectional(game.startTextProperty());
		startingTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, startingTextField));

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

		startItemsListView.initialize(game.getStartItems(),
				this.currentGameManager.getPersistenceManager().getInventoryItemManager()::getAllInventoryItems, null,
				this::objectSelected, (item) -> game.addStartItem(item), (item) -> game.removeStartItem(item));

		useWithHelpTextField.textProperty().bindBidirectional(game.useWithCombineHelpTextProperty());
		useWithHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, useWithHelpTextField));
		moveHelpTextField.textProperty().bindBidirectional(game.moveHelpTextProperty());
		moveHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, moveHelpTextField));
		takeHelpTextField.textProperty().bindBidirectional(game.takeHelpTextProperty());
		takeHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, takeHelpTextField));
		useHelpTextField.textProperty().bindBidirectional(game.useHelpTextProperty());
		useHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, useHelpTextField));
		talkHelpTextField.textProperty().bindBidirectional(game.talkToHelpTextProperty());
		talkHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, talkHelpTextField));
		lookAroundHelpTextField.textProperty().bindBidirectional(game.lookAroundHelpTextProperty());
		lookAroundHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, lookAroundHelpTextField));
		inspectHelpTextField.textProperty().bindBidirectional(game.inspectHelpTextProperty());
		inspectHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, inspectHelpTextField));
		inventoryHelpTextField.textProperty().bindBidirectional(game.inventoryHelpTextProperty());
		inventoryHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, inventoryHelpTextField));
		helpHelpTextField.textProperty().bindBidirectional(game.helphelpTextProperty());
		helpHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, helpHelpTextField));
		exitHelpTextField.textProperty().bindBidirectional(game.exitCommandHelpTextProperty());
		exitHelpTextField.textProperty().addListener((f, o, n) -> warnOnEmpty(n, exitHelpTextField));

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
		takeSuccessTextField.textProperty().bindBidirectional(game.takenTextProperty());
		takeSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, takeSuccessTextField, noSecondPL));
		useSuccessTextField.textProperty().bindBidirectional(game.usedTextProperty());
		useSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useSuccessTextField, noSecondPL));
		inspectSuccessTextField.textProperty().bindBidirectional(game.inspectionDefaultTextProperty());
		inspectSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, inspectSuccessTextField, noSecondPL));
		emptyInvSuccessTextField.textProperty().bindBidirectional(game.inventoryEmptyTextProperty());
		emptyInvSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, emptyInvSuccessTextField, inputAndPattern));
		invSuccessTextField.textProperty().bindBidirectional(game.inventoryTextProperty());
		invSuccessTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, invSuccessTextField, inputAndPattern));

		useWithFailureTextField.textProperty().bindBidirectional(game.notUsableWithTextProperty());
		useWithFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useWithFailureTextField, allPL));
		moveFailureTextField.textProperty().bindBidirectional(game.notTravelableTextProperty());
		moveFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, moveFailureTextField, noSecondPL));
		takeFailureTextField.textProperty().bindBidirectional(game.notTakeableTextProperty());
		takeFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, takeFailureTextField, noSecondPL));
		useFailureTextField.textProperty().bindBidirectional(game.notUsableTextProperty());
		useFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, useFailureTextField, noSecondPL));
		talkFailureTextField.textProperty().bindBidirectional(game.notTalkingToEnabledTextProperty());
		talkFailureTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, talkFailureTextField, noSecondPL));
		noSuchItemTextField.textProperty().bindBidirectional(game.noSuchItemTextProperty());
		noSuchItemTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchItemTextField, inputPatternID));
		noSuchInventoryItemTextField.textProperty().bindBidirectional(game.noSuchInventoryItemTextProperty());
		noSuchInventoryItemTextField.textProperty().addListener(
				(f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchInventoryItemTextField, inputPatternID));
		noSuchPersonTextField.textProperty().bindBidirectional(game.noSuchPersonTextProperty());
		noSuchPersonTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchPersonTextField, inputPatternID));
		noSuchWayTextField.textProperty().bindBidirectional(game.noSuchWayTextProperty());
		noSuchWayTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noSuchWayTextField, inputPatternID));
		noValidCommandTextField.textProperty().bindBidirectional(game.noCommandTextProperty());
		noValidCommandTextField.textProperty()
				.addListener((f, o, n) -> checkPlaceholdersAndEmptiness(n, noValidCommandTextField, input));
		notSensibleCommandTextField.textProperty().bindBidirectional(game.invalidCommandTextProperty());
		notSensibleCommandTextField.textProperty().addListener(
				(f, o, n) -> checkPlaceholdersAndEmptiness(n, notSensibleCommandTextField, inputAndPattern));

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
		// The value numOptionLinesProp must NOT be stored in a local variable
		// or passed directly. Only the class field suffices.
		// Since JavaFX uses weak references for bidirectional bindings, it will
		// get garbage collected otherwise.
		numOptionLinesProp = game.numberOfOptionLinesProperty().asObject();
		svf.valueProperty().bindBidirectional(numOptionLinesProp);
		optionLinesSpinner.setValueFactory(svf);

		ObservableList<Color> colors = FXCollections.observableArrayList(Color.values());
		successfulFGColorPicker.setItems(colors);
		successfulFGColorPicker.valueProperty().bindBidirectional(game.successfullFgColorProperty());
		successfulBGColorPicker.setItems(colors);
		successfulBGColorPicker.valueProperty().bindBidirectional(game.successfullBgColorProperty());
		neutralFGColorPicker.setItems(colors);
		neutralFGColorPicker.valueProperty().bindBidirectional(game.neutralFgColorProperty());
		neutralBGColorPicker.setItems(colors);
		neutralBGColorPicker.valueProperty().bindBidirectional(game.neutralBgColorProperty());
		failureFGColorPicker.setItems(colors);
		failureFGColorPicker.valueProperty().bindBidirectional(game.failedFgColorProperty());
		failureBGColorPicker.setItems(colors);
		failureBGColorPicker.valueProperty().bindBidirectional(game.failedBgColorProperty());

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
