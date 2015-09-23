package persistence;

import data.Player;

/**
 * Managing access to the player in a database.
 * 
 * @author Satia
 */
public class PlayerManager {
	/**
	 * 
	 * @return the player
	 */
	public static Player getPlayer() {
		// Return the game's player
		return GameManager.getGame().getPlayer();
	}
}