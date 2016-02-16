package gui.view;

import data.Game;
import gui.utility.StringVerification;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for the game details view.
 * 
 * @author Satia
 */
public class GameDetailsController extends GameDataController {

	public static final String GAME_TITLE_EMPTY_TOOLTIP = "The game title must not be empty";
	public static final String GAME_TITLE_CHARS_TOOLTIP = "The game title contains illegal characters";

	/** The Game **/
	private Game game;

	@FXML
	private TextField gameTitleField;

	@FXML
	private TextArea startingTextField;

	@FXML
	private void initialize() {
		// Obtain the game
		this.game = currentGameManager.getPersistenceManager().getGameManager().getGame();
		// Set all GUI fields accordingly
		this.gameTitleField.setText(game.getGameTitle());
		this.startingTextField.setText(game.getStartText());
		// TODO the others

		// Register change listeners on the fields that update the game
		// accordingly
		this.gameTitleField.textProperty().addListener((observable, oldValue, newValue) -> {
			updateGameTitle(newValue);
		});

		this.startingTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			this.game.setStartText(newValue);
		});
	}

	/**
	 * Validates the game title before actually propagating it to the game. Must
	 * not be empty, must not contain any characters forbidden in filenames.
	 * 
	 * @param newTitle
	 *            the new title
	 */
	public void updateGameTitle(String newTitle) {
		if (newTitle.isEmpty()) {
			showError(gameTitleField, GAME_TITLE_EMPTY_TOOLTIP);
		} else if (StringVerification.isUnsafeFileName(newTitle)) {
			showError(gameTitleField, GAME_TITLE_CHARS_TOOLTIP);
		} else {
			hideError(gameTitleField);
			// Set the value in the game
			game.setGameTitle(newTitle);
		}
	}

}
