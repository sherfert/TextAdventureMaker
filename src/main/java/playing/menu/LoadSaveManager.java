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
	public static final String H2_ENDING = ".h2.db";

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

	/**
	 * The directory of the save games.
	 */
	private static String saveGamesDir;

	/**
	 * The name of the game.
	 */
	private static String gameName;

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
		gameName = args[0];
		fileName = PropertiesReader.DIRECTORY + gameName;
		saveGamesDir = fileName + File.separator;
		// Create the dir if necessary
		new File(saveGamesDir).mkdirs();

		// Create new gamePlayer
		gamePlayer = new GamePlayer();

		// Create main menu
		mainMenu = new MainMenu(gamePlayer.getIo());

		// Show main menu without save and back
		showMenu(false);
	}

	/**
	 * @return the saveGamesDir
	 */
	public static String getSaveGamesDir() {
		return saveGamesDir;
	}

	/**
	 * Tries to copy the db file to a temp file. Shuts the whole VM down, if
	 * that fails.
	 * 
	 * @param file
	 *            the original file.
	 */
	private static void copyToTempDB(File file) {
		File tempFile = new File(PropertiesReader.DIRECTORY + gameName
				+ TEMP_APPENDIX + H2_ENDING);
		// Copy to temp file
		try {
			Files.copy(file.toPath(), tempFile.toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not copy db file. Exiting now.", e);
			System.exit(-1);
		}
	}

	/**
	 * Tries to copy the temp db file to a file. Only logs if that fails.
	 * 
	 * @param file
	 *            the file to copy to.
	 */
	private static void copyFromTempDB(File file) {
		File tempFile = new File(PropertiesReader.DIRECTORY + gameName
				+ TEMP_APPENDIX + H2_ENDING);
		// Copy to temp file
		try {
			Files.copy(tempFile.toPath(), file.toPath(),
					java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not copy db file when saving.", e);
		}
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

	/**
	 * Starts a new game.
	 */
	public static void newGame() {
		// "Load" the new file
		load(new File(fileName + H2_ENDING));
	}

	/**
	 * Loads a file.
	 * 
	 * @param file
	 *            the file to load.
	 */
	public static void load(File file) {
		// Disconnect from old db
		PersistenceManager.disconnect();
		// Copy file to a temp db
		copyToTempDB(file);
		// Connect
		PersistenceManager.connect(fileName + TEMP_APPENDIX, false);
		// Set the game for the game player
		gamePlayer.setGame(GameManager.getGame());
		// Start a game
		Logger.getLogger(LoadSaveManager.class.getName()).log(Level.INFO,
				"New game/Load game");
		gamePlayer.start();
	}

	/**
	 * Saves a file.
	 * 
	 * @param file
	 *            the file to save to
	 */
	public static void save(File file) {
		// If disconnecting should become necessary, consider the following code
		// PersistenceManager.disconnect();
		// [...]
		// Reconnect
		// PersistenceManager.connect(fileName + TEMP_APPENDIX, false);
		// Set the game for the game player
		// gamePlayer.setGame(GameManager.getGame());
		// PersistenceManager.disconnect();

		copyFromTempDB(file);

	}
}
