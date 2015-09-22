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

	/**
	 * The main method to play a game. If there is an argument provided, it must
	 * point to a valid game database file. If there is no argument provided, it
	 * is assumed there is a file named "game"+H2_ENDING ("game.h2.db") in the
	 * resources folder of this JAR file.
	 * 
	 * @param args
	 *            either the game database file to load or nothing.
	 */
	public static void main(String[] args) {
		// Initialize the logging
		try {
			Class.forName(logging.LogManager.class.getName());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not initialize logging:", e);
		}

		Logger.getLogger(LoadSaveManager.class.getName()).log(Level.INFO,
				"Started LoadSaveManager with arguments: ", args);

		if (args.length == 0) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"No arguments provided.");
			// TODO if there is no arg, look if there is a game.db in resources
			return;
		}

		// Save game file name
		gameName = args[0];
		fileName = PropertiesReader.DIRECTORY + gameName;
		saveGamesDir = fileName + File.separator;

		File saveGamesDirFile = new File(saveGamesDir);
		// Create the dir if necessary
		if (!saveGamesDirFile.exists()) {
			if (saveGamesDirFile.mkdirs()) {
				Logger.getLogger(LoadSaveManager.class.getName()).log(
						Level.FINE, "Created save game directory: {0}",
						saveGamesDir);
			} else {
				Logger.getLogger(LoadSaveManager.class.getName()).log(
						Level.SEVERE,
						"Could not create save game directory: {0} Aborting.",
						saveGamesDir);
				return;
			}
		}

		// Create new gamePlayer
		// After that, the VM is kept alive with new threads. It must be closed
		// with
		// System.exit(...) or gamePlayer.stop() respectively.
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
		try {
			gamePlayer.setGame(GameManager.getGame());
		} catch (Exception e) {
			// This means the database is incompatible with the model.
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not get the game. Database incompatible. Exiting.",
					e);
			// This means we cannot continue in any sensible way
			System.exit(-1);
		}
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