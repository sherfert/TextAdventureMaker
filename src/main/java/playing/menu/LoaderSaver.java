package playing.menu;

import java.io.File;

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
	 */
	void newGame();

	/**
	 * Loads a file.
	 * 
	 * @param file
	 *            the file to load.
	 */
	void load(File file);

	/**
	 * Saves a file.
	 * 
	 * @param file
	 *            the file to save to
	 */
	void save(File file);

}
