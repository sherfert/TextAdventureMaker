package playing.menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import persistence.PersistenceManager;
import playing.GamePlayer;
import configuration.PropertiesReader;
import exception.DBIncompatibleException;
import exception.LoadSaveException;

/**
 * This class handles starting new games, loading (and saving). Saving is done
 * automatically, but upon starting a new game, a name of the savegame has to be
 * entered.
 * 
 * @author Satia
 * 
 */
public class LoadSaveManager implements MenuShower, LoaderSaver {
	// XXX if the jar is minimized, the persistence provider is not found. The
	// current maven-shade-plugin allows for no better solution
	// than not minimizing the jar or specifying ALL needed classes from the
	// artifacts.

	/**
	 * The ending of H2 databases.
	 */
	public static final String H2_ENDING = ".h2.db";

	/**
	 * The main menu
	 */
	private MainMenu mainMenu;

	/**
	 * The game player
	 */
	private GamePlayer gamePlayer;

	/**
	 * The file of the game db.
	 */
	private URL file;

	/**
	 * The file temporarily used to play the game.
	 */
	private File tempFile;

	/**
	 * The directory of the save games.
	 */
	private String saveGamesDir;

	/**
	 * The name of the game.
	 */
	private String gameName;

	/**
	 * A reference to the currently active persistenceManager.
	 */
	private PersistenceManager persistenceManager = new PersistenceManager();

	public LoadSaveManager() throws IOException {
		// Retrieve the DB File inside the JAR
		Logger.getLogger(LoadSaveManager.class.getName()).log(Level.INFO,
				"No arguments provided, using game DB inside JAR.");
		ClassLoader classLoader = LoadSaveManager.class.getClassLoader();
		this.file = classLoader.getResource("game" + H2_ENDING);

		// Test if resource exists
		if (this.file == null) {
			throw new FileNotFoundException("Game DB in JAR does not exist. File: " + this.file);
		}

		init();
	}

	public LoadSaveManager(String gameName) throws IOException {
		// Use game with the provided title
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Started LoadSaveManager with argument: {0}",
				gameName);

		File f = new File(PropertiesReader.DIRECTORY + gameName + H2_ENDING);
		// Test if file exists
		if (!f.exists()) {
			throw new FileNotFoundException("Game DB does not exist. File: " + f);
		}

		this.file = f.toURI().toURL();

		init();
	}

	private void init() {
		// Obtain the name of the game
		File tFile = copyToTempDB(file);
		// Connect
		String path = tFile.getAbsolutePath();
		this.persistenceManager.connect(path.substring(0, path.length() - H2_ENDING.length()), false);
		try {
			this.gameName = persistenceManager.getGameManager().getGameTitle();
		} catch (DBIncompatibleException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not read game name. Aborting init.",
					e);
			return;
		}
		// Do NOT disconnect from DB here, because loading a new game will
		// already disconnect and
		// otherwise there are exceptions.

		// Set the saves game directory path accordingly
		this.saveGamesDir = PropertiesReader.DIRECTORY + gameName + File.separator;

		File saveGamesDirFile = new File(saveGamesDir);
		// Create the dir if necessary
		if (!saveGamesDirFile.exists()) {
			if (saveGamesDirFile.mkdirs()) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created save game directory: {0}",
						saveGamesDir);
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
						"Could not create save game directory: {0} Aborting.", saveGamesDir);
				return;
			}
		}

		// Create new gamePlayer
		// After that, the VM is kept alive with new threads. It must be closed
		// with
		// System.exit(...) or gamePlayer.stop() respectively.
		this.gamePlayer = new GamePlayer(persistenceManager, this);

		// Create main menu
		this.mainMenu = new MainMenu(gamePlayer.getIo(), this);

		// Show main menu without save and back
		showMenu(false);
	}

	/**
	 * The main method to play a game. If there is an argument provided, it must
	 * point to a valid game database file. If there is no argument provided, it
	 * is assumed there is a file named "game"+H2_ENDING ("game.h2.db") in this
	 * JAR file.
	 * 
	 * @param args
	 *            either the game database file to load or nothing.
	 */
	public static void main(String[] args) {
		// Initialize the logging
		try {
			Class.forName(logging.LogManager.class.getName());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE, "Could not initialize logging:", e);
		}

		try {
			if (args.length == 0) {
				new LoadSaveManager();
			} else {
				new LoadSaveManager(args[0]);
			}
		} catch (IOException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not initialize the LoadSaveManager.", e);
		}

	}

	/**
	 * Tries to copy the db file to a temp file.
	 * 
	 * @param file
	 *            the original file.
	 * @return the temp file.
	 */
	private File copyToTempDB(URL file) {
		// Copy to temp file
		try {
			Path tempFile = Files.createTempFile("tam", H2_ENDING);

			FileUtils.copyURLToFile(file, tempFile.toFile());
			return tempFile.toFile();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not copy db file.",
					e);
			return null;
		}
	}

	/**
	 * Tries to copy the temp db file to a file. Only logs if that fails.
	 * 
	 * @param file
	 *            the file to copy to.
	 */
	private void copyFromTempDB(File file) {
		// Copy to temp file
		try {
			Files.copy(tempFile.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not copy db file when saving.",
					e);
		}
	}

	@Override
	public void showMenu(boolean gameRunning) {
		mainMenu.show(gameRunning);
	}

	@Override
	public String getSaveGamesDir() {
		return saveGamesDir;
	}

	@Override
	public void newGame() throws LoadSaveException {
		// "Load" the new file
		load(file);
	}

	@Override
	public void load(File file) throws LoadSaveException {
		try {
			load(file.toURI().toURL());
		} catch (MalformedURLException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Malformed file URL.", e);
		}
	}

	/**
	 * Loads a file.
	 * 
	 * @param file
	 *            the file to load.
	 */
	private void load(URL file) throws LoadSaveException {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "New game/Load game");
		
		// Disconnect from old db
		persistenceManager.disconnect();
		// Copy file to a temp db
		tempFile = copyToTempDB(file);
		// Connect
		String path = tempFile.getAbsolutePath();
		persistenceManager.connect(path.substring(0, path.length() - H2_ENDING.length()), false);
		// Set the game for the game player
		try {
			gamePlayer.setGame(persistenceManager.getGameManager().getGame());
		} catch (DBIncompatibleException e) {
			// This means the database is incompatible with the model.
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Could not get the game. Database incompatible.", e);
			// Show some error message
			gamePlayer.getIo().println(e.getMessage());
			throw new LoadSaveException(e);
		}
		// Start a game
		gamePlayer.start();
	}

	/**
	 * Disconnects, does something, reconnects.
	 * 
	 * @param doSth
	 *            the thing to do in between. Not related to threads!
	 * @param path
	 *            the path to reconnect to
	 */
	private void disconnectDoReconnect(Runnable doSth, String path) {
		// Disconnect
		persistenceManager.disconnect();
		// Do whatever
		doSth.run();
		// Reconnect to path
		persistenceManager.connect(path, false);
	}

	@Override
	public void save(File file) throws LoadSaveException {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Save game");
		
		String path = tempFile.getAbsolutePath();
		disconnectDoReconnect(() -> copyFromTempDB(file), path.substring(0, path.length() - H2_ENDING.length()));
		// We must set the reference to the game again

		try {
			gamePlayer.setGame(persistenceManager.getGameManager().getGame());
		} catch (DBIncompatibleException e) {
			// Show some error message
			gamePlayer.getIo().println(e.getMessage());
			throw new LoadSaveException(e);
		}
	}
}
