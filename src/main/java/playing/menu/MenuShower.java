package playing.menu;

/**
 * Anything that can show a Menu.
 * 
 * @author Satia
 *
 */
public interface MenuShower {

	/**
	 * Shows the main menu.
	 * 
	 * @param gameRunning
	 *            if there is a game running in the background
	 */
	void showMenu(boolean gameRunning);

}
