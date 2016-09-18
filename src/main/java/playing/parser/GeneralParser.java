package playing.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import data.Game;
import exception.DBClosedException;
import playing.GamePlayer;
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
import playing.parser.PatternGenerator.MultiPattern;

/**
 * The parser for recognizing commands.
 *
 * @author Satia
 */
public class GeneralParser {

	/**
	 * All possible commands.
	 *
	 * The ordering must be chosen carefully.
	 *
	 * E.g. should USEWITHCOMBINE come before USE, as USEWITHCOMBINE could have
	 * "use X with Y" as a commandType and USE could have "use X". Changing that
	 * order parsing "use A with b" would result in an answer like
	 * "there is no A with B here".
	 *
	 * On the other hand, if "look around" is a LOOKAROUND commandType, it
	 * should come before INSPECT if "look X" is an INSPECT commandType,
	 * otherwise you would hear "there is no around here". The tradeoff is that
	 * no item can be named "around".
	 *
	 * Generally speaking, commands with more parameters should often come
	 * first.
	 *
	 * @author Satia
	 */
	public enum CommandType {
		/** Use one item with another or combine two items */
		USEWITHCOMBINE(Game::getUseWithCombineCommands, Game::getUseWithCombineHelpText, UseWithCombine.class, 2), //
		/** Move around */
		MOVE(Game::getMoveCommands, Game::getMoveHelpText, Move.class, 1), //
		/** Take something */
		TAKE(Game::getTakeCommands, Game::getTakeHelpText, Take.class, 1), //
		/** Use something */
		USE(Game::getUseCommands, Game::getUseHelpText, Use.class, 1), //
		/** Talk to someone */
		TALKTO(Game::getTalkToCommands, Game::getTalkToHelpText, TalkTo.class, 1), //
		/** Look around */
		LOOKAROUND(Game::getLookAroundCommands, Game::getLookAroundHelpText, LookAround.class, 0), //
		/** Inspect something */
		INSPECT(Game::getInspectCommands, Game::getInspectHelpText, Inspect.class, 1), //
		/** Look into the inventory */
		INVENTORY(Game::getInventoryCommands, Game::getInventoryHelpText, Inventory.class, 0), //
		/** Get help */
		HELP(Game::getHelpCommands, Game::getHelpHelpText, Help.class, 0);

		/**
		 * The method that gets the valid commands.
		 */
		public final Function<Game, List<String>> commandMethod;

		/**
		 * The method that gets the help text for that commandType.
		 */
		public final Function<Game, String> helpTextMethod;

		/**
		 * The concrete class of the command implementing the functionality of
		 * this command type.
		 */
		public final Class<? extends Command> commandClass;

		/**
		 * The number of parameters the command expects.
		 */
		public final int numberOfParameters;

		/**
		 * @param commandMethod
		 *            The method that gets the valid commands.
		 * @param helpTextMethod
		 *            The method that gets the help text for that commandType.
		 * @param commandClass
		 *            the concrete class of the command implementing the
		 *            functionality of this command type.
		 * @param numberOfParameters
		 *            the number of parameters the command expects.
		 */
		private CommandType(Function<Game, List<String>> commandMethod, Function<Game, String> helpTextMethod,
				Class<? extends Command> commandClass, int numberOfParameters) {
			this.commandMethod = commandMethod;
			this.helpTextMethod = helpTextMethod;
			this.commandClass = commandClass;
			this.numberOfParameters = numberOfParameters;
		}
	}

	/**
	 * Recognizes and executes commands.
	 *
	 * @author Satia
	 */
	public class CommandRecExec {

		/**
		 * The pattern for recognizing the commandType.
		 */
		private MultiPattern pattern;

		/**
		 * The pattern for recognizing with additional commands.
		 */
		private MultiPattern additionalCommandsPattern;

		/**
		 * The commandType.
		 */
		private final CommandType commandType;

		/**
		 * The command.
		 */
		private final Command command;

		/**
		 * The textual commands.
		 */
		private List<String> textualCommands;

		/**
		 * The help text for this commandType.
		 */
		private String commandHelpText;

		/**
		 * @param commandType
		 *            the commandType
		 */
		public CommandRecExec(CommandType commandType) {
			this.commandType = commandType;
			this.command = gamePlayer.getCommand(commandType.commandClass);

			this.textualCommands = commandType.commandMethod.apply(gamePlayer.getGame());
			this.pattern = PatternGenerator.getPattern(this.textualCommands);

			Set<String> commands;
			try {
				commands = command.getAdditionalCommands();
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Operating on a closed DB");
				commands = new HashSet<>();
			}
			additionalCommandsPattern = PatternGenerator.getPattern(commands);

			commandHelpText = commandType.helpTextMethod.apply(gamePlayer.getGame());
		}

		/**
		 * Tests if the input matches the pattern and executes the method in
		 * this case. This method is private, but used by the outer class.
		 *
		 * @param input
		 *            the input
		 * @return if the input matches the commandType's pattern.
		 */
		private boolean recognizeAndExecute(String input) {
			Matcher matcher = pattern.matcher(input);
			Matcher additionalCommandsMatcher = additionalCommandsPattern.matcher(input);

			boolean matchFound = false;
			boolean originalCommand = false;
			Parameter[] params = null;
			if (matcher.matches()) {
				params = getParameters(matcher, commandType.numberOfParameters);
				matchFound = true;
				originalCommand = true;

				// Save the pattern in the replacer
				gamePlayer.getCurrentReplacer().setPattern(matcher.pattern().toString());
			} else if (additionalCommandsMatcher.matches()) {
				params = getParameters(additionalCommandsMatcher, commandType.numberOfParameters);
				matchFound = true;
				originalCommand = false;

				// Save the pattern in the replacer
				gamePlayer.getCurrentReplacer().setPattern(additionalCommandsMatcher.pattern().toString());
			}

			// Either a normal or an additional commandType matched
			if (matchFound) {
				command.execute(originalCommand, params);
				// This commandType matches
				return true;
			}
			// This commandType did not match
			return false;
		}

		/**
		 * @return the commandType
		 */
		public CommandType getCommand() {
			return commandType;
		}

		/**
		 * @return the textualCommands
		 */
		public List<String> getTextualCommands() {
			return textualCommands;
		}

		/**
		 * @return the commandHelpText
		 */
		public String getCommandHelpText() {
			return commandHelpText;
		}
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

	/**
	 * The GamePlayer for this session
	 */
	private final GamePlayer gamePlayer;

	/**
	 * A list of {@link CommandRecExec}s for each {@link CommandType}.
	 */
	private final List<CommandRecExec> commandRecExecs;

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
		// Build CommandRecExecs
		CommandType[] commands = CommandType.values();
		this.commandRecExecs = new ArrayList<>(commands.length);
		for (CommandType commandType : commands) {
			this.commandRecExecs.add(new CommandRecExec(commandType));
		}
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

		// Set the input for the game players' replacer
		gamePlayer.setInput(input);

		// TODO better handling for matching of multiple commands
		for (CommandRecExec cmdRE : commandRecExecs) {
			if (cmdRE.recognizeAndExecute(input)) {
				return true;
			}
		}
		// No commandType matched
		gamePlayer.noCommand();
		return true;
	}

	/**
	 * @return the commandRecExecs
	 */
	public List<CommandRecExec> getCommandRecExecs() {
		return commandRecExecs;
	}
}
