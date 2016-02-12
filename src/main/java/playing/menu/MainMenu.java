package playing.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 * XXX it would be nicer if people could enter a name for their savegames. This
 * is difficult with the current Lanterna classes.
 * 
 * @author Satia
 */
public class MainMenu implements OptionIOManager {

	/**
	 * The format for naming savegames.
	 */
	public static final String SAVEGAME_TIME_FORMAT = "YYYY.MM.dd-HH.mm.ss";

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
	private static String[] menuEntries = new String[] { "New game", "Save game", "Load game", "Back" };

	/**
	 * The options shown in the game menu if no game is running yet.
	 */
	private static String[] menuEntriesNoGameRunning = new String[] { "New game", "Load game" };

	/**
	 * The IO object.
	 */
	private InputOutput io;

	/**
	 * the LoadSaveManager
	 */
	private LoaderSaver ls;

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
	 * @param ls
	 *            the LoadSaveManager
	 */
	public MainMenu(InputOutput io, LoaderSaver ls) {
		this.io = io;
		this.ls = ls;
		this.menuStates = new Stack<>();
	}

	/**
	 * Shows the main menu
	 * 
	 * @param gameRunning
	 *            if there is a game running in the background
	 */
	public void show(boolean gameRunning) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Showing main menu");
		io.enterOptionMode(this, Arrays.asList(gameRunning ? menuEntries : menuEntriesNoGameRunning));
		menuStates.push(gameRunning ? MenuState.MAIN : MenuState.MAIN_NO_GAME_RUNNING);
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
			if (index < files.size()) {
				load(files.get(index));
			} else {
				back();
			}
			break;
		case SAVE:
			if (index < files.size()) {
				// Remove the old file
				try {
					Files.delete(files.get(index).toPath());
				} catch (IOException e) {
					Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
							"Could not remove old save file on override.", e);
				}

				// Save a file, with the current date and time
				save(new File(ls.getSaveGamesDir() + getCurrentTimeString() + LoadSaveManager.H2_ENDING));
			} else if (index == files.size()) {
				// Save a file, with the current date and time
				save(new File(ls.getSaveGamesDir() + getCurrentTimeString() + LoadSaveManager.H2_ENDING));
			} else {
				back();
			}
			break;
		}
	}

	/**
	 * @return the current date and time as a string to create a new savegame
	 *         file.
	 */
	public static String getCurrentTimeString() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(SAVEGAME_TIME_FORMAT));
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
			io.setOptions(Arrays.asList(state == MenuState.MAIN ? menuEntries : menuEntriesNoGameRunning));
		}
	}

	/**
	 * Lists all filenames of the savegames. Also saves the files in
	 * {@link #files}.
	 * 
	 * @return
	 */
	private List<String> listFiles() {
		List<String> options = new ArrayList<>();
		this.files = new ArrayList<>();

		for (File fileEntry : new File(ls.getSaveGamesDir()).listFiles()) {
			String name = fileEntry.getName();
			if (!fileEntry.isDirectory() && name.endsWith(LoadSaveManager.H2_ENDING)) {
				files.add(fileEntry);
				options.add(name.substring(0, name.length() - LoadSaveManager.H2_ENDING.length()));
			}
		}
		return options;
	}

	/**
	 * Shows the load menu.
	 */
	private void showLoadMenu() {
		List<String> options = listFiles();
		// Back option
		options.add("Back");

		io.setOptions(options);
		menuStates.push(MenuState.LOAD);
	}

	/**
	 * Shows the save menu.
	 */
	private void showSaveMenu() {
		List<String> options = listFiles();
		// New file option
		options.add("New file");
		// Back option
		options.add("Back");

		io.setOptions(options);
		menuStates.push(MenuState.SAVE);
	}

	/**
	 * Starts a new game and also exits the menu.
	 */
	private void newGame() {
		ls.newGame();
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
		ls.load(file);
		// Exit menu
		back();
		back();
	}

	/**
	 * Saves a file and also exits the menu.
	 * 
	 * @param file
	 *            the file to load.
	 */
	private void save(File file) {
		ls.save(file);
		// Exit menu
		back();
		back();

	}

	@Override
	public int getNumberOfOptionLines() {
		return OPTION_LINES;
	}

}
