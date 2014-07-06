package playing.menu;

import java.util.logging.Level;
import java.util.logging.Logger;

import persistence.GameManager;
import persistence.PersistenceManager;
import persistence.PlayerManager;
import playing.GamePlayer;
import configuration.PropertiesReader;

/**
 * This class handles starting new games, loading (and saving). Saving is done
 * automatically, but upon starting a new game, a name of the savegame has to be
 * entered.
 * 
 * @author Satia
 * 
 */
public class LoadSaveManager {
	
	/**
	 * The main menu
	 */
	private static MainMenu mainMenu;

	// TODO also logging
	public static void main(String[] args) {
		// Initialize the logging
		try {
			Class.forName(logging.LogManager.class.getName());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE,
					"Could not initialize logging:", e);
		}

		if (args.length == 0) {
			// TODO if there is no arg, look if there is a game.db in resources
			return;
		}

		// TODO atm just load the game
		String gameName = args[0];
		String fileName = PropertiesReader.DIRECTORY + gameName;
		PersistenceManager.connect(fileName, false);

		// Start a game
		GamePlayer gamePlayer = new GamePlayer(GameManager.getGame(), PlayerManager.getPlayer());
		gamePlayer.start();
		
		mainMenu = new MainMenu(gamePlayer.getIo());
	}

	/**
	 * Shows the main menu.
	 */
	public static void showMenu() {
		mainMenu.show();
	}

	// TODO save! also startOrLoad method called in
	// PersistenceManager.disconnect.

}
