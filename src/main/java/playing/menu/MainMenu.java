package playing.menu;

import java.util.Arrays;
import java.util.Stack;
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
	 * The lines reserved for options.
	 */
	private static final int OPTION_LINES = 10;

	/**
	 * The state of the menu.
	 * 
	 * @author Satia
	 */
	private enum MenuState {
		MAIN, MAIN_NO_GAME_RUNNING, LOAD, SAVE;
	}

	/**
	 * The options shown in the game menu.
	 */
	private static String[] menuEntries = new String[] { "New game",
			"Save game", "Load game", "Back" };

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
	 * The menu states.
	 */
	private Stack<MenuState> menuStates;

	/**
	 * @param io
	 *            the IO object
	 */
	public MainMenu(InputOutput io) {
		this.io = io;
		this.menuStates = new Stack<>();
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
		menuStates.push(gameRunning ? MenuState.MAIN
				: MenuState.MAIN_NO_GAME_RUNNING);
	}

	@Override
	public void chooseOption(int index) {
		switch (menuStates.peek()) {
		case MAIN:
			switch (index) {
			case 0:
				newGame();
				break;
			case 1:
				showSaveMenu();
				break;
			case 2:
				showLoadMenu();
				break;
			case 3:
				back();
				break;
			}
			break;
		case MAIN_NO_GAME_RUNNING:
			switch (index) {
			case 0:
				newGame();
				break;
			case 1:
				showLoadMenu();
				break;
			}
			break;
		case LOAD:
			// TODO
			back();
			break;
		case SAVE:
			break;
		}
	}

	/**
	 * Exits the menu and continues the running game. TODO
	 */
	private void back() {
		// Discard last menu state
		menuStates.pop();
		if (menuStates.isEmpty()) {
			// If no menu states left, exit
			io.exitOptionMode();
		} else {
			// Otherwise go back to last menu (can only be MAIN or
			// MAIN_NO_GAME_RUNNING)
			MenuState state = menuStates.peek();
			io.setOptions(Arrays.asList(state == MenuState.MAIN ? menuEntries
					: menuEntriesNoGameRunning));
		}
	}

	private void showLoadMenu() {
		// TODO Auto-generated method stub
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Load");

		io.setOptions(Arrays.asList("Back"));
		menuStates.push(MenuState.LOAD);
	}

	private void showSaveMenu() {
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
		return OPTION_LINES;
	}

}
