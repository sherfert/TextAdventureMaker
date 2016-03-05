package playing.menu;

import java.io.File;

import exception.LoadSaveException;

/**
 * Implementations of this interface must provide methods to start a new game,
 * load and save to files, and provide the save game directory.
 * 
 * @author Satia
 *
 */
public interface LoaderSaver {

	/**
	 * @return the saveGamesDir
	 */
	String getSaveGamesDir();

	/**
	 * Starts a new game.
	 * 
	 * @throws LoadSaveException
	 *             if while loading an error occurred
	 */
	void newGame() throws LoadSaveException;

	/**
	 * Loads a file.
	 * 
	 * @param file
	 *            the file to load.
	 * @throws LoadSaveException
	 *             if while loading an error occurred
	 */
	void load(File file) throws LoadSaveException;

	/**
	 * Saves a file.
	 * 
	 * @param file
	 *            the file to save to
	 * @throws LoadSaveException
	 *             if while or after saving an error occurred
	 */
	void save(File file) throws LoadSaveException;

}
