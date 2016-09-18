package playing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
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
	 * All commands.
	 */
	private List<Command> commands;

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

		/*
		 * The ordering must be chosen carefully.
		 *
		 * E.g. should USEWITHCOMBINE come before USE, as USEWITHCOMBINE could
		 * have "use X with Y" as a commandType and USE could have "use X".
		 * Changing that order parsing "use A with b" would result in an answer
		 * like "there is no A with B here".
		 *
		 * On the other hand, if "look around" is a LOOKAROUND commandType, it
		 * should come before INSPECT if "look X" is an INSPECT commandType,
		 * otherwise you would hear "there is no around here". The tradeoff is
		 * that no item can be named "around".
		 *
		 * Generally speaking, commands with more parameters should often come
		 * first.
		 */

		// Create all commands
		commands = new ArrayList<>();

		commands.add(new UseWithCombine(this));
		commands.add(new Move(this));
		commands.add(new Take(this));
		commands.add(new Use(this));
		commands.add(new TalkTo(this));
		commands.add(new LookAround(this));
		commands.add(new Inspect(this));
		commands.add(new Inventory(this));
		commands.add(new Help(this));
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
	 * Retrieves the commands.
	 * 
	 * @return the command.
	 */
	public List<Command> getCommands() {
		return commands;
	}

	/**
	 * Displays an error message to the player if his input was not
	 * recognizable.
	 */
	public void noCommand() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Command type not identifiable");

		io.println(currentReplacer.replacePlaceholders(game.getNoCommandText()), game.getFailedBgColor(),
				game.getFailedFgColor());
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
			// Place the start items in his inventory
			game.putStartItemsIntoInventory();
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
		if (game.getHasEnded()) {
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
