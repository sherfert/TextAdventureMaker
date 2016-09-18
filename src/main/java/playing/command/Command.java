package playing.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import exception.DBClosedException;
import persistence.PersistenceManager;
import playing.GamePlayer;
import playing.InputOutput;
import playing.PlaceholderReplacer;
import playing.parser.GeneralParser;
import playing.parser.Parameter;
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
	 * Tests if the input matches the pattern and executes the method in this
	 * case. This method is private, but used by the outer class.
	 *
	 * @param input
	 *            the input
	 * @return if the input matches the commandType's pattern.
	 */
	public boolean recognizeAndExecute(String input) {
		Matcher matcher = pattern.matcher(input);
		Matcher additionalCommandsMatcher = additionalCommandsPattern.matcher(input);

		boolean matchFound = false;
		boolean originalCommand = false;
		Parameter[] params = null;
		if (matcher.matches()) {
			params = getParameters(matcher, numberOfParameters);
			matchFound = true;
			originalCommand = true;

			// Save the pattern in the replacer
			gamePlayer.getCurrentReplacer().setPattern(matcher.pattern().toString());
		} else if (additionalCommandsMatcher.matches()) {
			params = getParameters(additionalCommandsMatcher, numberOfParameters);
			matchFound = true;
			originalCommand = false;

			// Save the pattern in the replacer
			gamePlayer.getCurrentReplacer().setPattern(additionalCommandsMatcher.pattern().toString());
		}

		// Either a normal or an additional commandType matched
		if (matchFound) {
			execute(originalCommand, params);
			// This commandType matches
			return true;
		}
		// This commandType did not match
		return false;
	}
	
	/**
	 * Extracts the used parameters from a given matcher.
	 *
	 * @param matcher
	 *            the matcher that must have matched an input
	 * @param numberOfParameters
	 *            the expected number of parameters
	 * @return an array with the typed parameters
	 */
	public static Parameter[] getParameters(Matcher matcher, int numberOfParameters) {
		/*
		 * By convention, all capturing groups that denote parameters must be
		 * named with o0, o1, o2, and so on, without leaving gaps. This can be
		 * used to pass the parameter to the command function together with its
		 * capturing group title for further processing.
		 */

		List<Parameter> parameters = new ArrayList<>(2);
		try {
			for (int i = 0; i < numberOfParameters; i++) {
				String groupName = "o" + i;
				String identifier;
				if ((identifier = matcher.group(groupName)) != null) {
					parameters.add(new Parameter(identifier, groupName));
				} else {
					break;
				}
			}
		} catch (IllegalArgumentException e) {
			Logger.getLogger(GeneralParser.class.getName()).log(Level.SEVERE,
					"Capturing group in the range of expected parameters not found.", e);
		}

		return parameters.toArray(new Parameter[0]);
	}
}
