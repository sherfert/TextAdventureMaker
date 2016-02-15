package gui.view;

import logic.CurrentGameManager;

/**
 * Interface for all controllers that need access to the current game manager. They can use
 * it to obtain the persistence manager and query the database.
 * 
 * Any implementing class must provide a public Constructor with no parameters.
 * 
 * @author Satia
 *
 */
public interface GameDataController {

	/**
	 * @param currentGameManager
	 *            the currentGameManager to set
	 */
	void setCurrentGameManager(CurrentGameManager currentGameManager);

}
