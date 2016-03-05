package persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Player;
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
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		// Return the game's player
		try {
			return persistenceManager.getGameManager().getGame().getPlayer();
		} catch (DBIncompatibleException e) {
			// This means the database is incompatible with the model.
			// This should never happen at this point!
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Could not get the game. Database incompatible.", e);
		}
		return null;
	}
}