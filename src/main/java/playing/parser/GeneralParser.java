package playing.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Game;
import playing.GamePlayer;

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
	 * "use X with Y" as a command and USE could have "use X". Changing that
	 * order parsing "use A with b" would result in an answer like
	 * "there is no A with B here".
	 * 
	 * On the other hand, if "look around" is a LOOKAROUND command, it should
	 * come before INSPECT if "look X" is an INSPECT command, otherwise you
	 * would hear "there is no around here". The tradeoff is that no item can be
	 * named "around".
	 * 
	 * Generally speaking, commands with more parameters should often come
	 * first.
	 * 
	 * @author Satia
	 */
	public enum Command {
		USEWITHCOMBINE("getUseWithCombineCommands",
				"getUseWithCombineHelpText", "useWithOrCombine", String.class,
				String.class), //
		MOVE("getMoveCommands", "getMoveHelpText", "move", String.class), //
		TAKE("getTakeCommands", "getTakeHelpText", "take", String.class), //
		USE("getUseCommands", "getUseHelpText", "use", String.class), //
		LOOKAROUND("getLookAroundCommands", "getLookAroundHelpText",
				"lookAround"), //
		INSPECT("getInspectCommands", "getInspectHelpText", "inspect",
				String.class), //
		INVENTORY("getInventoryCommands", "getInventoryHelpText", "inventory"), //
		HELP("getHelpCommands", "getHelpHelpText", "help");

		/**
		 * The name of the method that gets the valid commands. Must be a method
		 * of the class {@link Game} with no parameters and List<String> as
		 * return type.
		 */
		public final String commandMethodName;

		/**
		 * The name of the method that gets the help text for that command. Must
		 * be a method of the class {@link Game} with no parameters and String
		 * as return type.
		 */
		public final String helpTextMethodName;

		/**
		 * The name of the method that should be invoked when this command was
		 * recognized. Must be a method of the class {@link GamePlayer}.
		 */
		public final String methodName;

		/**
		 * The parameter types of the method denoted by methodName.
		 */
		public final Class<?>[] parameterTypes;

		/**
		 * @param commandMethodName
		 *            The name of the method that gets the valid commands. Must
		 *            be a method of the class {@link Game} with no parameters
		 *            and List of Strings as return type.
		 * @param helpTextMethodName
		 *            the name of the method that gets the help text for that
		 *            command. Must be a method of the class {@link Game} with
		 *            no parameters and String as return type.
		 * @param methodName
		 *            the name of the method that should be invoked when this
		 *            command was recognized. Must be a method of the class
		 *            {@link GamePlayer}.
		 * @param parameterTypes
		 *            the parameter types of the method denoted by methodName.
		 */
		private Command(String commandMethodName, String helpTextMethodName,
				String methodName, Class<?>... parameterTypes) {
			this.commandMethodName = commandMethodName;
			this.helpTextMethodName = helpTextMethodName;
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
		}
	}

	/**
	 * Recognizes and executes commands.
	 * 
	 * @author Satia
	 */
	public class CommandRecExec {
		/**
		 * The pattern for recognizing the command.
		 */
		private Pattern pattern;

		/**
		 * The method being executed when the command was typed.
		 */
		private Method executeMethod;

		/**
		 * The command.
		 */
		private Command command;

		/**
		 * The texual commands.
		 */
		private List<String> textualCommands;
		
		/**
		 * The help text for this command.
		 */
		private String commandHelpText;

		/**
		 * @param command
		 *            the command
		 */
		@SuppressWarnings("unchecked")
		public CommandRecExec(Command command) {
			this.command = command;
			try {
				this.textualCommands = (List<String>) Game.class
						.getDeclaredMethod(command.commandMethodName).invoke(
								gamePlayer.getGame());
				this.pattern = PatternGenerator.getPattern(this.textualCommands);
				
				commandHelpText = (String) Game.class
						.getDeclaredMethod(command.helpTextMethodName).invoke(
								gamePlayer.getGame());

				this.executeMethod = GamePlayer.class.getDeclaredMethod(
						command.methodName, command.parameterTypes);
			} catch (ClassCastException | NoSuchMethodException
					| SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
						"Problems finding the executeMethod:", e);
			}
		}

		/**
		 * Tests if the input matches the pattern and executes the method in
		 * this case. This method is private, but used by the outer class.
		 * 
		 * @param input
		 *            the input
		 * @return if the input matches the command's pattern.
		 */
		private boolean recognizeAndExecute(String input) {
			Matcher matcher = pattern.matcher(input);

			if (matcher.matches()) {
				String[] params = getParameters(matcher);

				try {
					if (executeMethod.getParameterTypes().length == params.length) {
						executeMethod.invoke(gamePlayer, (Object[]) params);
					} else {
						Logger.getLogger(this.getClass().getName())
								.log(Level.SEVERE,
										"Number of parameters for method {0} is wrong.",
										executeMethod);
					}
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					Logger.getLogger(this.getClass().getName())
							.log(Level.SEVERE,
									"Could not invoke method " + executeMethod
											+ ":", e);
				}
				// This command matches
				return true;
			}
			// This command did not match
			return false;
		}

		/**
		 * @return the command
		 */
		public Command getCommand() {
			return command;
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
	 * @return the typed parameters.
	 */
	public static String[] getParameters(Matcher matcher) {
		List<String> parameters = new ArrayList<String>(matcher.groupCount());
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
	private GamePlayer gamePlayer;

	/**
	 * A list of {@link CommandRecExec}s for each {@link Command}.
	 */
	private List<CommandRecExec> commandRecExecs;

	/**
	 * The pattern for exit commands.
	 */
	private Pattern exitPattern;

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
		Command[] commands = Command.values();
		this.commandRecExecs = new ArrayList<CommandRecExec>(commands.length);
		for (Command command : commands) {
			this.commandRecExecs.add(new CommandRecExec(command));
		}
	}

	/**
	 * Parses an input String and invokes the according method, if the command
	 * had a valid syntax. Otherwise the player is told, that the command was
	 * not valid. Returns whether an non-exit command has been entered
	 * 
	 * @param input
	 *            the input
	 * @return {@code true} if the command was NOT an exit command,
	 *         {@code false} otherwise.
	 */
	public boolean parse(String input) {
		// Trimmed, lower case, no multiple spaces, no punctuation
		input = input.trim().toLowerCase().replaceAll("\\p{Blank}+", " ")
				.replaceAll("\\p{Punct}", "");

		// Is it an exit command?
		if (exitPattern.matcher(input).matches()) {
			return false;
		}

		// Set the input for the game players' replacer
		gamePlayer.setInput(input);

		for (CommandRecExec cmdRE : commandRecExecs) {
			if (cmdRE.recognizeAndExecute(input)) {
				return true;
			}
		}
		// No command matched
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