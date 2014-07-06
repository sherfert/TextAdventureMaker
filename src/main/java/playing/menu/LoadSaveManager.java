package playing.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import persistence.GameManager;
import persistence.PersistenceManager;
import playing.GamePlayer;
import configuration.PropertiesReader;

/**
 * This class handles starting new games, loading (and saving). Saving is done
 * automatically, but upon starting a new game, a name of the savegame has to be
 * entered.
 * 
 * @author Satia
 * 
 */
public class LoadSaveManager {

	/**
	 * The ending of H2 databases.
	 */
	private static final String H2_ENDING = ".h2.db";

	/**
	 * The appendix for temp files.
	 */
	private static final String TEMP_APPENDIX = "-temp";

	/**
	 * The main menu
	 */
	private static MainMenu mainMenu;

	/**
	 * The game player
	 */
	private static GamePlayer gamePlayer;

	/**
	 * The file name of the game. TODO replace with resource?
	 */
	private static String fileName;

	// TODO also logging
	public static void main(String[] args) {
		// Initialize the logging
		try {
			Class.forName(logging.LogManager.class.getName());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not initialize logging:", e);
		}

		if (args.length == 0) {
			// TODO if there is no arg, look if there is a game.db in resources
			return;
		}

		// Save game file name
		String gameName = args[0];
		fileName = PropertiesReader.DIRECTORY + gameName;

		// Create new gamePlayer
		gamePlayer = new GamePlayer();

		// Create main menu
		mainMenu = new MainMenu(gamePlayer.getIo());

		// Show main menu without save and back
		showMenu(false);
	}

	/**
	 * Tries to copy the db file to a temp file. Shuts the whole VM down, if
	 * that fails.
	 * 
	 * @param fileName
	 *            the original file name.
	 */
	private static void copyToTempDB(String fileName) {
		// TODO consider if db file is in resources...

		// Append ending
		String actualName = fileName + H2_ENDING;
		String tempName = fileName + TEMP_APPENDIX + H2_ENDING;
		// Copy to temp file
		try {
			Files.copy(new File(actualName).toPath(),
					new File(tempName).toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not copy db file. Exiting now.", e);
			System.exit(-1);
		}
	}

	/**
	 * Starts a new game.
	 */
	public static void newGame() {
		// Disconnect from old db
		PersistenceManager.disconnect();
		// Copy file to a temp db
		copyToTempDB(fileName);
		// Connect
		PersistenceManager.connect(fileName + TEMP_APPENDIX, false);
		// Set the game for the game player
		gamePlayer.setGame(GameManager.getGame());
		// Start a game
		Logger.getLogger(LoadSaveManager.class.getName()).log(Level.INFO,
				"New game");
		gamePlayer.start();
	}

	/**
	 * Shows the main menu.
	 * 
	 * @param gameRunning
	 *            if there is a game running in the background
	 */
	public static void showMenu(boolean gameRunning) {
		mainMenu.show(gameRunning);
	}

	// TODO save! also startOrLoad method called in
	// PersistenceManager.disconnect.

}
