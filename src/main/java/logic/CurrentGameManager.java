package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import persistence.PersistenceManager;
import playing.menu.LoadSaveManager;

/**
 * This class maintains the state of the currently opened game file and forwards
 * corresponding calls to the persistence.
 * 
 * TODO autosave every X minutes, where X is a configurable property
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
	 */
	public void open(File file) {
		// Be sure to close the connection if another file was open
		if (openFile != null) {
			close();
		}
		openFile = file;

		String name = openFile.getName();
		gameName = name.substring(0, name.length() - LoadSaveManager.H2_ENDING.length());

		String fileName = openFile.getAbsolutePath();
		persistenceManager.connect(fileName.substring(0, fileName.length() - LoadSaveManager.H2_ENDING.length()),
				false);
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
	 * Starts the game from within the TextAdventureMaker.
	 * 
	 * FIXME copying with disconnect does not work. But if you disconnect,
	 * the GUI changes are not mirrored in the DB afterwards (detached state).
	 */
	public void playGame() {
		// Be sure to commit the latest changes
		persistenceManager.updateChanges();

		// Disconnect before copying
		//persistenceManager.disconnect();

		Path from = openFile.getAbsoluteFile().toPath();
		Path to = new File(openFile.getParent() + File.separator + gameName + "_temp" + LoadSaveManager.H2_ENDING).toPath();
		System.out.println("to: " + to);
		try {
			Files.copy(from, to, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not copy db file.", e);
			// TODO show error message
			return;
		}
		// Reconnect
//		String fileName = openFile.getAbsolutePath();
//		persistenceManager.connect(fileName.substring(0, fileName.length() - LoadSaveManager.H2_ENDING.length()),
//				false);

		LoadSaveManager.main(new String[] { gameName + "_temp" });

	}

}
