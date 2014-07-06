package playing.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	 * The files in the save game dir
	 */
	private List<File> files;

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
			if (index >= files.size()) {
				// Default: back
				back();
			} else {
				load(files.get(index));
			}
			break;
		case SAVE:
			// TODO
			break;
		}
	}

	/**
	 * Exits the menu and continues the running game.
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

		List<String> options = new ArrayList<>();
		this.files = new ArrayList<>();

		for (File fileEntry : new File(LoadSaveManager.getSaveGamesDir())
				.listFiles()) {
			String name = fileEntry.getName();
			if (!fileEntry.isDirectory()
					&& name.endsWith(LoadSaveManager.H2_ENDING)) {
				files.add(fileEntry);
				options.add(name.substring(0, name.length()
						- LoadSaveManager.H2_ENDING.length()));
			}
		}
		// Back option
		options.add("Back");

		io.setOptions(options);
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

	/**
	 * Loads a file and also exits the menu.
	 * 
	 * @param file
	 *            the file to load.
	 */
	private void load(File file) {
		LoadSaveManager.load(file);
		// Exit menu
		back();
		back();
	}

	@Override
	public int getNumberOfOptionLines() {
		return OPTION_LINES;
	}

}
