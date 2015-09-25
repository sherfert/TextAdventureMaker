package playing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import persistence.PersistenceManager;
import persistence.UsableObjectManager;
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
import playing.menu.LoadSaveManager;
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
	 * The parser
	 */
	private GeneralParser parser;

	/**
	 * All commands mapped to their class type for easy retrieval.
	 */
	private Map<Class<? extends Command>, Command> commands;

	/**
	 * Creates a new game player. Can only be used properly after
	 * {@link #setGame(Game)} has been called.
	 */
	public GamePlayer() {
		this.io = new InputOutput(this);
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
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"CommandType not identifiable");

		io.println(
				currentReplacer.replacePlaceholders(game.getNoCommandText()),
				game.getFailedBgColor(), game.getFailedFgColor());
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
	 * Starts playing the game. Returns immediately.
	 */
	public void start() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				"Starting the game");
		// clear the screen
		io.clear();

		// If the player has no location, this is a new game.
		if (game.getPlayer().getLocation() == null) {
			// Transfer him to the start location and start a new game.
			game.getPlayer().setLocation(game.getStartLocation());
			io.println(game.getStartText(), game.getNeutralBgColor(),
					game.getNeutralFgColor());
		}
		// Continue by printing the locations's text.
		io.println(game.getPlayer().getLocation().getEnteredText(),
				game.getNeutralBgColor(), game.getNeutralFgColor());
	}

	/**
	 * Exits the game.
	 */
	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				"Stopping the game");
		io.exitIO();
		PersistenceManager.disconnect();
		// FIXME at the moment close the vm
		System.exit(0);
	}

	/**
	 * Checks if the game has ended. If so, the main menu is shown, but one can
	 * not continue the game.
	 */
	public void checkGameEnded() {
		if (game.isHasEnded()) {
			LoadSaveManager.showMenu(false);
		}
	}

	/**
	 * {@inheritDoc} Parses the input. Stops the game if necessary.
	 */
	@Override
	public void handleText(String text) {
		if (!parser.parse(text)) {
			stop();
		}
	}
}
