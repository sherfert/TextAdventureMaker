package playing.menu;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import playing.InputOutput;
import playing.InputOutput.OptionIOManager;

/**
 * The main menu.
 * 
 * @author Satia
 */
public class MainMenu implements OptionIOManager {

	/**
	 * The options shown in the game menu.
	 */
	private static String[] menuEntries = new String[] { "New game",
			"Load game", "Save game", "Back" };

	/**
	 * The options shown in the game menu if no game is running yet.
	 */
	private static String[] menuEntriesNoGameRunning = new String[] {
			"New game", "Load game" };

	/**
	 * The IO object.
	 */
	private InputOutput io;

	/**
	 * @param io
	 *            the IO object
	 */
	public MainMenu(InputOutput io) {
		this.io = io;
	}

	/**
	 * Shows the main menu
	 * 
	 * @param gameRunning
	 *            if there is a game running in the background
	 */
	public void show(boolean gameRunning) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINER,
				"Showing main menu");
		io.enterOptionMode(this, Arrays.asList(gameRunning ? menuEntries
				: menuEntriesNoGameRunning));
	}

	@Override
	public void chooseOption(int index) {
		switch (index) {
		case 0:
			newGame();
			break;
		case 1:
			load();
			break;
		case 2:
			save();
			break;
		case 3:
			back();
			break;
		}
	}

	/**
	 * Exits the menu and continues the running game.
	 */
	private void back() {
		io.exitOptionMode();
	}

	private void load() {
		// TODO Auto-generated method stub
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Load");
	}

	private void save() {
		// TODO Auto-generated method stub
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Save");
	}

	/**
	 * Starts a new game and also exits the menu.
	 */
	private void newGame() {
		LoadSaveManager.newGame();
		// Exit menu
		back();
	}

	@Override
	public int getNumberOfOptionLines() {
		return menuEntries.length;
	}

}
