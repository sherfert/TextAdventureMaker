package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import com.googlecode.lanterna.terminal.Terminal.Color;

import data.Game;
import exception.DBIncompatibleException;
import gui.custumui.LocationChooser;
import gui.utility.StringUtils;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
 * TODO Support to change starting location, start items
 * 
 * XXX Warnings for empty fields
 * 
 * XXX placeholder checking for success fields
 * 
 * @author Satia
 */
public class GameDetailsController extends GameDataController {

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
	private LocationChooser startLocationChooser;
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

	public GameDetailsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	private void initialize() throws DBIncompatibleException {
		// Obtain the game
		try {
			game = currentGameManager.getPersistenceManager().getGameManager().getGame();
		} catch (DBIncompatibleException | PersistenceException e) {
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
		// Set all GUI fields accordingly
		gameTitleField.setText(game.getGameTitle());
		startingTextField.textProperty().bindBidirectional(game.startTextProperty());

		startLocationChooser.initialize(currentGameManager.getPersistenceManager().getLocationManager().getAllLocations(),
				currentGameManager.getPersistenceManager().getGameManager().getGame().getStartLocation(),
				currentGameManager, (loc) -> {
					try {
						currentGameManager.getPersistenceManager().getGameManager().getGame().setStartLocation(loc);
					} catch (Exception e) {
						Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not set start location.",
								e);
					}
				});

		useWithHelpTextField.textProperty().bindBidirectional(game.useWithCombineHelpTextProperty());
		moveHelpTextField.textProperty().bindBidirectional(game.moveHelpTextProperty());
		takeHelpTextField.textProperty().bindBidirectional(game.takeHelpTextProperty());
		useHelpTextField.textProperty().bindBidirectional(game.useHelpTextProperty());
		talkHelpTextField.textProperty().bindBidirectional(game.talkToHelpTextProperty());
		lookAroundHelpTextField.textProperty().bindBidirectional(game.lookAroundHelpTextProperty());
		inspectHelpTextField.textProperty().bindBidirectional(game.inspectHelpTextProperty());
		inventoryHelpTextField.textProperty().bindBidirectional(game.inventoryHelpTextProperty());
		helpHelpTextField.textProperty().bindBidirectional(game.helphelpTextProperty());
		exitHelpTextField.textProperty().bindBidirectional(game.exitCommandHelpTextProperty());

		useWithSuccessTextField.textProperty().bindBidirectional(game.usedWithTextProperty());
		takeSuccessTextField.textProperty().bindBidirectional(game.takenTextProperty());
		useSuccessTextField.textProperty().bindBidirectional(game.usedTextProperty());
		inspectSuccessTextField.textProperty().bindBidirectional(game.inspectionDefaultTextProperty());
		emptyInvSuccessTextField.textProperty().bindBidirectional(game.inventoryEmptyTextProperty());
		invSuccessTextField.textProperty().bindBidirectional(game.inventoryTextProperty());

		useWithFailureTextField.textProperty().bindBidirectional(game.notUsableWithTextProperty());
		moveFailureTextField.textProperty().bindBidirectional(game.notTravelableTextProperty());
		takeFailureTextField.textProperty().bindBidirectional(game.notTakeableTextProperty());
		useFailureTextField.textProperty().bindBidirectional(game.notUsableTextProperty());
		talkFailureTextField.textProperty().bindBidirectional(game.notTalkingToEnabledTextProperty());
		noSuchItemTextField.textProperty().bindBidirectional(game.noSuchItemTextProperty());
		noSuchInventoryItemTextField.textProperty().bindBidirectional(game.noSuchInventoryItemTextProperty());
		noSuchPersonTextField.textProperty().bindBidirectional(game.noSuchPersonTextProperty());
		noSuchWayTextField.textProperty().bindBidirectional(game.noSuchWayTextProperty());
		noValidCommandTextField.textProperty().bindBidirectional(game.noCommandTextProperty());
		notSensibleCommandTextField.textProperty().bindBidirectional(game.invalidCommandTextProperty());

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
		// get garbage collected otherwise. Stupid!
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

		useWithCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 2, useWithCommandsTextField, game::setUseWithCombineCommands));
		moveCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 1, moveCommandsTextField, game::setMoveCommands));
		takeCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 1, takeCommandsTextField, game::setTakeCommands));
		useCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 1, useCommandsTextField, game::setUseCommands));
		talkCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 1, talkCommandsTextField, game::setTalkToCommands));
		lookAroundCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, lookAroundCommandsTextField, game::setLookAroundCommands));
		inspectCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 1, inspectCommandsTextField, game::setInspectCommands));
		inventoryCommandsTextField.textProperty().addListener(
				(f, o, n) -> updateGameCommands(n, 0, inventoryCommandsTextField, game::setInventoryCommands));
		helpCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 0, helpCommandsTextField, game::setHelpCommands));
		exitCommandsTextField.textProperty()
				.addListener((f, o, n) -> updateGameCommands(n, 0, exitCommandsTextField, game::setExitCommands));
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
