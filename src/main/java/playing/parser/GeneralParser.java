package playing.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import playing.GamePlayer;
import playing.command.Command;
import playing.command.CommandExecution;
import playing.parser.PatternGenerator.MultiPattern;

/**
 * The parser for recognizing commands.
 *
 * @author Satia
 */
public class GeneralParser {

	/**
	 * The GamePlayer for this session
	 */
	private final GamePlayer gamePlayer;

	/**
	 * The pattern for exit commands.
	 */
	private final MultiPattern exitPattern;

	/**
	 * Initializes this parser.
	 *
	 * @param gamePlayer
	 *            the GamePlayer for this session
	 */
	public GeneralParser(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		// Build exit pattern
		exitPattern = PatternGenerator.getPattern(gamePlayer.getGame().getExitCommands());
	}

	/**
	 * Parses an input String and invokes the according method, if the
	 * commandType had a valid syntax. Otherwise the player is told, that the
	 * commandType was not valid. Returns whether an non-exit commandType has
	 * been entered
	 *
	 * @param input
	 *            the input
	 * @return {@code true} if the commandType was NOT an exit commandType,
	 *         {@code false} otherwise.
	 */
	public boolean parse(String input) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Parsing input: {0}", input);
		// Trimmed, lower case, no multiple spaces, no punctuation
		input = input.trim().toLowerCase().replaceAll("\\p{Blank}+", " ").replaceAll("\\p{Punct}", "");

		// Is it an exit commandType?
		if (exitPattern.matcher(input).matches()) {
			return false;
		}
		

		// TODO better handling for matching of multiple commands
		// Set the input for the game players' replacer
		gamePlayer.getCurrentReplacer().reset();
		gamePlayer.getCurrentReplacer().setInput(input);
		
		// Obtain all execution objects
		List<CommandExecution> executions = new ArrayList<>();
		for (Command command : gamePlayer.getCommands()) {
			executions.add(command.newExecution(input));
		}
		
		// See if they match and execute
		for(CommandExecution e : executions) {
			if(e.matches()) {
				e.execute();
				return true;
			}
		}

		
		// No commandType matched
		gamePlayer.noCommand();
		return true;
	}
}
