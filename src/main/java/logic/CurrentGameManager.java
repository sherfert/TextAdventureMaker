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
	 * The file that points to the currently open game database.
	 */
	private static File openFile;

	/**
	 * Open a file and connect to it using the Persistence unit.
	 * 
	 * @param file
	 */
	public static void open(File file) {
		// Be sure to close the connection if another file was open
		if(openFile != null) {
			close();
		}
		openFile = file;

		String fileName = openFile.getAbsolutePath();
		PersistenceManager.connect(fileName.substring(0, fileName.length() - LoadSaveManager.H2_ENDING.length()),
				false);
	}
	
	/**
	 * Closes the file by disconnecting from the DB.
	 */
	public static void close() {
		PersistenceManager.disconnect();
		openFile = null;
	}

	/**
	 * @return the openFile
	 */
	public static File getOpenFile() {
		return openFile;
	}

}
