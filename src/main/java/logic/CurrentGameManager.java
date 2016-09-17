package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.DBClosedException;
import persistence.PersistenceManager;
import playing.menu.LoadSaveManager;

/**
 * This class maintains the state of the currently opened game file and forwards
 * corresponding calls to the persistence.
 * 
 * XXX Autosave every X minutes, where X is a configurable property
 * 
 * @author Satia
 */
public class CurrentGameManager {

	/**
	 * A reference to the currently active persistenceManager
	 */
	private PersistenceManager persistenceManager;

	/**
	 * The file that points to the currently open game database.
	 */
	private File openFile;

	/**
	 * The name of the game file (without its ending)
	 */
	private String gameName;

	/**
	 * A new CurrentGameManager.
	 */
	public CurrentGameManager() {
		this.persistenceManager = new PersistenceManager();
	}

	/**
	 * Open a file and connect to it using the Persistence unit.
	 * 
	 * @param file
	 * @param create
	 *            if {@code true}, the tables are dropped and created (any old
	 *            data is lost), and a default Game is placed in the DB.
	 * @throws IOException
	 *             if the game could not be opened
	 */
	public void open(File file, boolean create) throws IOException {
		// Be sure to close the connection if another file was open
		if (openFile != null) {
			close();
		}
		openFile = file;

		String name = openFile.getName();
		gameName = name.substring(0, name.length() - LoadSaveManager.H2_ENDING.length());

		String fileName = openFile.getAbsolutePath();
		persistenceManager.connect(fileName.substring(0, fileName.length() - LoadSaveManager.H2_ENDING.length()),
				create);

		if (create) {
			// Place some default contents in the DB
			try {
				persistenceManager.getGameManager().loadDefaultGame();
			} catch (DBClosedException e) {
				// We just connected, so the DB is NOT closed at this point.
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "DB closed", e);
			}
		}
	}

	/**
	 * Closes the file by disconnecting from the DB.
	 */
	public void close() {
		// Be sure to commit any remaining changed data
		persistenceManager.updateChanges();

		persistenceManager.disconnect();
		openFile = null;
	}

	/**
	 * @return the openFile
	 */
	public File getOpenFile() {
		return openFile;
	}

	/**
	 * @return the persistenceManager
	 */
	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	/**
	 * Disconnects from the DB, carries out the given action, and reconnects.
	 * 
	 * @param doSth
	 *            what to do in-between
	 * @throws IOException
	 *             if the given action produces an {@link IOException}
	 */
	public void disconnectDoReconnect(Do doSth) throws IOException {
		// Be sure to commit the latest changes
		persistenceManager.updateChanges();

		// Disconnect before copying
		persistenceManager.disconnect();

		try {
			doSth.doSth();
		} finally {
			// Reconnect
			String fileName = openFile.getAbsolutePath();
			try {
				persistenceManager
						.connect(fileName.substring(0, fileName.length() - LoadSaveManager.H2_ENDING.length()), false);
			} catch (IOException e) {
				// Should never happen at this point
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not open db file.", e);
			}
		}
	}

	/**
	 * Starts the game from within the TextAdventureMaker.
	 */
	public void playGame() throws IOException {
		disconnectDoReconnect(() -> {
			Path from = openFile.getAbsoluteFile().toPath();
			Path to = new File(openFile.getParent() + File.separator + gameName + "_temp" + LoadSaveManager.H2_ENDING)
					.toPath();
			Files.copy(from, to, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			LoadSaveManager.main(new String[] { gameName + "_temp" });
		});
	}

}
