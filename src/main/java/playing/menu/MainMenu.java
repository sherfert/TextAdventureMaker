package playing.menu;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import playing.InputOutput;
import playing.InputOutput.OptionIOManager;

public class MainMenu implements OptionIOManager {

	/**
	 * The options shown in the game menu.
	 */
	private static String[] menuEntries = new String[] { "New game",
			"Save game", "Load game", "Back" };

	private InputOutput io;

	public MainMenu(InputOutput io) {
		this.io = io;
	}

	public void show() {
		io.enterOptionMode(this, Arrays.asList(menuEntries));
	}

	@Override
	public void chooseOption(int index) {
		switch (index) {
		case 0:
			newGame();
			break;
		case 1:
			save();
			break;
		case 2:
			load();
			break;
		case 3:
			back();
			break;
		}
	}

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
