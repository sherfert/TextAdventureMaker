package playing;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import persistence.PersistenceManager;
import playing.InputOutput.GeneralIOManager;
import playing.command.Command;
import playing.command.Help;
import playing.command.Inspect;
import playing.command.Inventory;
import playing.command.LookAround;
import playing.command.Move;
import playing.command.Take;
import playing.command.TalkTo;
import playing.command.Use;
import playing.command.UseWithCombine;
import playing.menu.MenuShower;
import playing.parser.GeneralParser;
import data.Game;

/**
 * Manages the actual commands needed to play a game and access to any classes
 * that are connected to this.
 * 
 * @author Satia
 */
public class GamePlayer implements GeneralIOManager {
	/**
	 * A placeholder replacer for the currently used command.
	 */
	private final PlaceholderReplacer currentReplacer;

	/**
	 * The game that is being played.
	 */
	private Game game;

	/**
	 * The IO object.
	 */
	private final InputOutput io;

	/**
	 * the menu shower
	 */
	private MenuShower ms;

	/**
	 * The parser
	 */
	private GeneralParser parser;

	/**
	 * All commands mapped to their class type for easy retrieval.
	 */
	private Map<Class<? extends Command>, Command> commands;

	/**
	 * A reference to the currently active persistenceManager
	 */
	private PersistenceManager persistenceManager;

	/**
	 * Creates a new game player. Can only be used properly after
	 * {@link #setGame(Game)} has been called.
	 * 
	 * @param pm
	 *            the PersistenceManager
	 * @param ms
	 *            menu shower
	 */
	public GamePlayer(PersistenceManager pm, MenuShower ms) {
		this.io = new InputOutput(this, ms);
		this.persistenceManager = pm;
		this.ms = ms;
		this.currentReplacer = new PlaceholderReplacer();

		// Create all commands
		commands = new HashMap<>();
		commands.put(Help.class, new Help(this));
		commands.put(Inspect.class, new Inspect(this));
		commands.put(Inventory.class, new Inventory(this));
		commands.put(LookAround.class, new LookAround(this));
		commands.put(Move.class, new Move(this));
		commands.put(Take.class, new Take(this));
		commands.put(TalkTo.class, new TalkTo(this));
		commands.put(Use.class, new Use(this));
		commands.put(UseWithCombine.class, new UseWithCombine(this));
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Sets the game and the parser
	 * 
	 * @param game
	 *            the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
		this.parser = new GeneralParser(this);
	}

	/**
	 * @return the io
	 */
	public InputOutput getIo() {
		return io;
	}

	/**
	 * @return the parser
	 */
	public GeneralParser getParser() {
		return parser;
	}

	/**
	 * @return the menu shower
	 */
	public MenuShower getMenuShower() {
		return ms;
	}

	/**
	 * @return the persistenceManager
	 */
	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	/**
	 * @return the currentReplacer
	 */
	public PlaceholderReplacer getCurrentReplacer() {
		return currentReplacer;
	}

	/**
	 * Retrieves the command of that class.
	 * 
	 * @param clazz
	 *            the class of the command to get.
	 * @return the command.
	 */
	public Command getCommand(Class<? extends Command> clazz) {
		return commands.get(clazz);
	}

	/**
	 * Displays an error message to the player if his input was not
	 * recognizable.
	 */
	public void noCommand() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "CommandType not identifiable");

		io.println(currentReplacer.replacePlaceholders(game.getNoCommandText()), game.getFailedBgColor(),
				game.getFailedFgColor());
	}

	/**
	 * Sets the input for the current replacer. The other fields will be reset.
	 * 
	 * @param input
	 *            the typed input
	 */
	public void setInput(String input) {
		currentReplacer.reset();
		currentReplacer.setInput(input);
	}

	/**
	 * Sets the pattern for the current replacer.
	 * 
	 * @param pattern
	 *            the matched pattern
	 */
	public void setPattern(String pattern) {
		currentReplacer.setPattern(pattern);
	}

	/**
	 * Starts playing the game. Returns immediately.
	 */
	public void start() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting the game");
		// clear the screen
		io.clear();

		// If the player has no location, this is a new game.
		if (game.getPlayer().getLocation() == null) {
			// Transfer him to the start location and start a new game.
			game.getPlayer().setLocation(game.getStartLocation());
			io.println(game.getStartText(), game.getNeutralBgColor(), game.getNeutralFgColor());
		}
		// Continue by printing the locations's text.
		io.println(game.getPlayer().getLocation().getEnteredText(), game.getNeutralBgColor(), game.getNeutralFgColor());
	}

	/**
	 * Exits the game. Exiting from the IO and disconnecting from the DB will
	 * shut down the VM, if no other Window is open.
	 */
	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Stopping the game");
		io.exitIO();
		persistenceManager.disconnect();
	}

	/**
	 * {@inheritDoc} Parses the input. Stops the game if necessary.
	 */
	@Override
	public boolean handleText(String text) {
		boolean cont = parser.parse(text);

		// Checks if the game has ended. If so, the main menu is shown, but one
		// can not continue the game.
		if (game.isHasEnded()) {
			ms.showMenu(false);
		}

		if (!cont) {
			stop();
		}

		return cont;
	}

	@Override
	public void updateState() {
		persistenceManager.updateChanges();
	}
}
