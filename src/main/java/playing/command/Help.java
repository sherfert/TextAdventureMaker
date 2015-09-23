package playing.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import playing.GamePlayer;
import playing.parser.GeneralParser.CommandRecExec;

/**
 * CommandType displaying help information to the user.
 * 
 * @author Satia
 */
public class Help extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public Help(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands for commands with no parameters
		return new HashSet<>();
	}

	@Override
	public void execute(boolean originalCommand, String... identifiers) {
		if (identifiers.length != 0) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of identifiers");
		}
		help();
	}

	/**
	 * Displays the help text to the player.
	 */
	private void help() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Displaying help");

		Game game = gamePlayer.getGame();

		// Iterate through all commands and all their textual commands
		// Also include exit
		for (CommandRecExec command : gamePlayer.getParser()
				.getCommandRecExecs()) {
			printCommandHelp(command.getCommandHelpText(),
					command.getTextualCommands(), game);
		}

		printCommandHelp(game.getExitCommandHelpText(), game.getExitCommands(),
				game);
	}

	/**
	 * Prints the help for a command
	 * 
	 * @param commandHelpText
	 *            the help text specified for that command
	 * @param textualCommands
	 *            all the textual commands triggering that command
	 * @param game
	 *            the game
	 */
	private void printCommandHelp(String commandHelpText,
			List<String> textualCommands, Game game) {
		io.println(commandHelpText, game.getNeutralBgColor(),
				game.getNeutralFgColor());
		for (String textualCommand : textualCommands) {
			io.println(" " + textualCommand, game.getNeutralBgColor(),
					game.getNeutralFgColor());
		}
	}

}
