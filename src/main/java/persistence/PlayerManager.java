package persistence;

import data.Player;
import exception.DBClosedException;
import exception.DBIncompatibleException;

/**
 * Managing access to the player in a database.
 * 
 * @author Satia
 */
public class PlayerManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public PlayerManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return the player
	 * @throws DBClosedException 
	 * @throws DBIncompatibleException 
	 */
	public Player getPlayer() throws DBClosedException, DBIncompatibleException {
		// Return the game's player
		return persistenceManager.getGameManager().getGame().getPlayer();
	}
}