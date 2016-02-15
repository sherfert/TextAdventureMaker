package gui.view;

import data.Game;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import logic.CurrentGameManager;

public class GameDetailsController implements GameDataController {
	
	public static final String GAME_TITLE_EMPTY_TOOLTIP = "The game title must not be empty";

	/** The current game manager. */
	private CurrentGameManager currentGameManager;

	/** The Game **/
	private Game game;

	@FXML
	private TextField gameTitleField;

	@FXML
	private TextArea startingTextField;

	public GameDetailsController() {
	}

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

	@Override
	public void setCurrentGameManager(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;
	}

	/**
	 * Validates the game title before actually propagating it to the game. Must
	 * not be empty, must not contain any characters forbidden in filenames.
	 * 
	 * @param newTitle
	 */
	public void updateGameTitle(String newTitle) {
		// TODO forbidden characters
		if(newTitle.isEmpty()) {
			// TODO apply some css to the text field for light red BG
			// Add tooltip to the text field
			Tooltip tooltip = new Tooltip(GAME_TITLE_EMPTY_TOOLTIP);
			// TODO show tooltip immediately
			gameTitleField.setTooltip(tooltip);
		} else {
			// Unset any tooltip message
			gameTitleField.setTooltip(null);  
			
			// Set the value in the game
			game.setGameTitle(newTitle);
		}
	}
}
