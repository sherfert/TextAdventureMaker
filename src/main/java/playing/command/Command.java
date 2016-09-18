package playing.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.DBClosedException;
import persistence.PersistenceManager;
import playing.GamePlayer;
import playing.InputOutput;
import playing.PlaceholderReplacer;
import playing.parser.PatternGenerator;
import playing.parser.PatternGenerator.MultiPattern;

/**
 * Abstract class for all commands.
 * 
 * @author Satia
 *
 */
public abstract class Command {

	/**
	 * The number of parameters the command expects.
	 */
	protected final int numberOfParameters;

	/**
	 * The pattern for recognizing the commandType.
	 */
	protected final MultiPattern pattern;

	/**
	 * The pattern for recognizing with additional commands.
	 */
	protected final MultiPattern additionalCommandsPattern;

	/**
	 * The textual commands.
	 */
	protected final List<String> textualCommands;

	/**
	 * The help text for this commandType.
	 */
	protected final String commandHelpText;

	/**
	 * A reference to the game player.
	 */
	protected GamePlayer gamePlayer;

	/**
	 * A reference to the replacer.
	 */
	protected PlaceholderReplacer currentReplacer;

	/**
	 * A reference to the IO.
	 */
	protected InputOutput io;

	/**
	 * A reference to the PM.
	 */
	protected PersistenceManager persistenceManager;

	/**
	 * Constructs a command and saves references to all important thing
	 * necessary for executing commands.
	 * 
	 * @param gamePlayer
	 *            the game player
	 * @param numberOfParameters
	 *            the number of parameters the command expects.
	 */
	public Command(GamePlayer gamePlayer, int numberOfParameters) {
		this.gamePlayer = gamePlayer;
		this.currentReplacer = gamePlayer.getCurrentReplacer();
		this.io = gamePlayer.getIo();
		this.persistenceManager = gamePlayer.getPersistenceManager();

		this.numberOfParameters = numberOfParameters;

		this.commandHelpText = getHelpText();
		this.textualCommands = getCommands();
		this.pattern = PatternGenerator.getPattern(textualCommands);
		Set<String> commands;
		try {
			commands = getAdditionalCommands();
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Operating on a closed DB");
			commands = new HashSet<>();
		}
		this.additionalCommandsPattern = PatternGenerator.getPattern(commands);
	}

	/**
	 * Retrieves the particular help text from the game.
	 * 
	 * @return the help text for this command
	 */
	protected abstract String getHelpText();

	/**
	 * Retrieves the particular textual commands from the game.
	 * 
	 * @return the textual commands for this command
	 */
	protected abstract List<String> getCommands();

	/**
	 * Gets all the additional commands that can be typed to trigger this
	 * command, defined anywhere in the game.
	 * 
	 * @return all additional commands.
	 * @throws DBClosedException
	 */
	protected abstract Set<String> getAdditionalCommands() throws DBClosedException;

	/**
	 * @return the commandHelpText
	 */
	public String getCommandHelpText() {
		return commandHelpText;
	}

	/**
	 * @return the textual commands
	 */
	public List<String> getTextualCommands() {
		return textualCommands;
	}

	/**
	 * Creates a new command execution which can be used for further testing and
	 * executing of the actual command with the given input.
	 * 
	 * @param input
	 *            the user input
	 * @return a new CommandExecution.
	 */
	public abstract CommandExecution newExecution(String input);
}
