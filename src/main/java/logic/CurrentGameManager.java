package logic;

import java.io.File;

import persistence.PersistenceManager;
import playing.menu.LoadSaveManager;

/**
 * This class maintains the state of the currently opened game file and forwards
 * corresponding calls to the persistence.
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
		System.out.println(gameName);

		String fileName = openFile.getAbsolutePath();
		persistenceManager.connect(fileName.substring(0, fileName.length() - LoadSaveManager.H2_ENDING.length()),
				false);
	}

	/**
	 * Closes the file by disconnecting from the DB.
	 */
	public void close() {
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
	 * Starts the game from within the TextAdventureMaker
	 */
	public void playGame() {
		LoadSaveManager.main(new String[] { gameName });
	}

}
