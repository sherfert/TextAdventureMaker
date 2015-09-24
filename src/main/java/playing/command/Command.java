package playing.command;

import java.util.Set;

import playing.GamePlayer;
import playing.InputOutput;
import playing.PlaceholderReplacer;
import playing.parser.Parameter;

/**
 * Abstract class for all commands.
 * 
 * @author Satia
 *
 */
public abstract class Command {

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
	 * Constructs a command and saves references to all important thing
	 * necessary for executing commands.
	 * 
	 * @param gamePlayer
	 *            the game player
	 */
	public Command(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.currentReplacer = gamePlayer.getCurrentReplacer();
		this.io = gamePlayer.getIo();
	}

	/**
	 * Gets all the additional commands that can be typed to trigger this
	 * command, defined anywhere in the game.
	 * 
	 * @return all additional commands.
	 */
	public abstract Set<String> getAdditionalCommands();

	/**
	 * Executes the command.
	 * 
	 * @param originalCommand
	 *            if the command was original (or else additional). Used to test
	 *            if an additional command really belonged to the chosen
	 *            identifier.
	 * @param parameters
	 *            the parameters. Depending on the command this must be none,
	 *            one, or more.
	 */
	public abstract void execute(boolean originalCommand, Parameter... parameters);
}
