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
	 * They should be ordered by complexity. E.g. should USEWITHCOMBINE come
	 * before USE, as USEWITHCOMBINE could have "use X with Y" as a command and
	 * USE could have "use X". Generally speaking, commands with more parameters
	 * should come first.
	 * 
	 * @author Satia
	 */
	private enum Command {
		USEWITHCOMBINE("getUseWithCombineCommands", "useWithOrCombine",
				String.class, String.class), //
		INSPECT("getInspectCommands", "inspect", String.class), //
		MOVE("getMoveCommands", "move", String.class), //
		TAKE("getTakeCommands", "take", String.class), //
		USE("getUseCommands", "use", String.class), //
		INVENTORY("getInventoryCommands", "inventory");

		/**
		 * The name of the method that gets the valid commands. Must be a method
		 * of the class {@link Game} with no parameters and List<String> as
		 * return type.
		 */
		public final String commandMethodName;

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
		 * @param methodName
		 *            the name of the method that should be invoked when this
		 *            command was recognized. Must be a method of the class
		 *            {@link GamePlayer}.
		 * @param parameterTypes
		 *            the parameter types of the method denoted by methodName.
		 */
		private Command(String commandMethodName, String methodName,
				Class<?>... parameterTypes) {
			this.commandMethodName = commandMethodName;
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
		}
	}

	/**
	 * Recognizes and executes commands.
	 * 
	 * @author Satia
	 */
	private class CommandRecExec {
		/**
		 * The pattern for recognizing the command.
		 */
		private Pattern pattern;

		/**
		 * The method being executed when the command was typed.
		 */
		private Method executeMethod;

		/**
		 * @param command
		 *            the command
		 */
		public CommandRecExec(Command command) {
			try {
				@SuppressWarnings("unchecked")
				List<String> cmds = (List<String>) Game.class
						.getDeclaredMethod(command.commandMethodName).invoke(
								gamePlayer.getGame());
				this.pattern = PatternGenerator.getPattern(cmds);

				this.executeMethod = GamePlayer.class.getDeclaredMethod(
						command.methodName, command.parameterTypes);
			} catch (ClassCastException | NoSuchMethodException
					| SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				Logger.getLogger(this.getClass().getName())
				.log(Level.SEVERE,
						"Problems finding the executeMethod:", e);
			}
		}

		/**
		 * Tests if the input matches the pattern and executes the method in
		 * this case.
		 * 
		 * @param input
		 *            the input
		 * @return if the input matches the command's pattern.
		 */
		public boolean recognizeAndExecute(String input) {
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
}