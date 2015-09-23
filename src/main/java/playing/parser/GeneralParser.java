package playing.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Game;
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
		USEWITHCOMBINE("getUseWithCombineCommands",
				"getUseWithCombineHelpText", UseWithCombine.class), //
		/** Move around */
		MOVE("getMoveCommands", "getMoveHelpText", Move.class), //
		/** Take something */
		TAKE("getTakeCommands", "getTakeHelpText", Take.class), //
		/** Use something */
		USE("getUseCommands", "getUseHelpText", Use.class), //
		/** Talk to someone */
		TALKTO("getTalkToCommands", "getTalkToHelpText", TalkTo.class), //
		/** Look around */
		LOOKAROUND("getLookAroundCommands", "getLookAroundHelpText",
				LookAround.class), //
		/** Inspect something */
		INSPECT("getInspectCommands", "getInspectHelpText", Inspect.class), //
		/** Look into the inventory */
		INVENTORY("getInventoryCommands", "getInventoryHelpText",
				Inventory.class), //
		/** Get help */
		HELP("getHelpCommands", "getHelpHelpText", Help.class);

		/**
		 * The name of the method that gets the valid commands. Must be a method
		 * of the class {@link Game} with no parameters and List of String as
		 * return type.
		 */
		public final String commandMethodName;

		/**
		 * The name of the method that gets the help text for that commandType.
		 * Must be a method of the class {@link Game} with no parameters and
		 * String as return type.
		 */
		public final String helpTextMethodName;

		/**
		 * The concrete class of the command implementing the functionality of
		 * this command type.
		 */
		public final Class<? extends Command> commandClass;

		/**
		 * @param commandMethodName
		 *            The name of the method that gets the valid commands. Must
		 *            be a method of the class {@link Game} with no parameters
		 *            and List of Strings as return type.
		 * @param helpTextMethodName
		 *            the name of the method that gets the help text for that
		 *            commandType. Must be a method of the class {@link Game}
		 *            with no parameters and String as return type.
		 * @param commandClass
		 *            the concrete class of the command implementing the
		 *            functionality of this command type.
		 */
		private CommandType(String commandMethodName,
				String helpTextMethodName, Class<? extends Command> commandClass) {
			this.commandMethodName = commandMethodName;
			this.helpTextMethodName = helpTextMethodName;
			this.commandClass = commandClass;
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
		private Pattern pattern;

		/**
		 * The pattern for recognizing with additional commands.
		 */
		private Pattern additionalCommandsPattern;

		/**
		 * The commandType.
		 */
		private final CommandType commandType;

		/**
		 * The command.
		 */
		private final Command command;

		/**
		 * The texual commands.
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
		@SuppressWarnings("unchecked")
		public CommandRecExec(CommandType commandType) {
			this.commandType = commandType;
			this.command = gamePlayer.getCommand(commandType.commandClass);
			
			try {
				this.textualCommands = (List<String>) Game.class
						.getDeclaredMethod(commandType.commandMethodName)
						.invoke(gamePlayer.getGame());
				this.pattern = PatternGenerator
						.getPattern(this.textualCommands);

				Set<String> commands = command.getAdditionalCommands();
				additionalCommandsPattern = PatternGenerator
						.getPattern(commands);

				commandHelpText = (String) Game.class.getDeclaredMethod(
						commandType.helpTextMethodName).invoke(
						gamePlayer.getGame());
			} catch (ClassCastException | NoSuchMethodException
					| SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
						"Problems finding one of the required methods:", e);
			}
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
			Matcher additionalCommandsMatcher = additionalCommandsPattern
					.matcher(input);
			
			boolean matchFound = false;
			boolean originalCommand = false;
			String[] params = null;
			if (matcher.matches()) {
				params = getParameters(matcher);
				matchFound = true;
				originalCommand = true;
			} else if (additionalCommandsMatcher.matches()) {
				params = getParameters(additionalCommandsMatcher);
				matchFound = true;
				originalCommand = false;
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
	 * @return an array with the
	 *         typed parameters
	 */
	public static String[] getParameters(Matcher matcher) {
		List<String> parameters = new ArrayList<>(matcher.groupCount());
		for (int i = 1; i <= matcher.groupCount(); i++) {
			if (matcher.group(i) != null) {
				parameters.add(matcher.group(i));
			}
		}

		return parameters.toArray(new String[0]);
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
	private final Pattern exitPattern;

	/**
	 * Initializes this parser.
	 *
	 * @param gamePlayer
	 *            the GamePlayer for this session
	 */
	public GeneralParser(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		// Build exit patterm
		exitPattern = PatternGenerator.getPattern(gamePlayer.getGame()
				.getExitCommands());
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
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Parsing input: {0}", input);
		// Trimmed, lower case, no multiple spaces, no punctuation
		input = input.trim().toLowerCase().replaceAll("\\p{Blank}+", " ")
				.replaceAll("\\p{Punct}", "");

		// Is it an exit commandType?
		if (exitPattern.matcher(input).matches()) {
			return false;
		}

		// Set the input for the game players' replacer
		gamePlayer.setInput(input);

		for (CommandRecExec cmdRE : commandRecExecs) {
			if (cmdRE.recognizeAndExecute(input)) {
				// Let the gamePlayer check if the game has ended.
				gamePlayer.checkGameEnded();
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
