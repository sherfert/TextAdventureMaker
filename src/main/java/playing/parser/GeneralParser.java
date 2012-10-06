package playing.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
	 * @author Satia
	 */
	private enum Command {
		INSPECT("getInspectCommands", "inspect", String.class), //
		INVENTORY("getInventoryCommands", "inventory"), //
		MOVE("getMoveCommands", "move", String.class), //
		TAKE("getTakeCommands", "take", String.class), //
		USE("getUseCommands","use",String.class);

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
		 *            and List<String> as return type.
		 * @param methodName
		 *            the name of the method that should be invoked when this
		 *            command was recognized. Must be a static method of the
		 *            class {@link GamePlayer}.
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
						// TODO wrong number of parameters
					}
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	 * Initializes this parser.
	 * 
	 * @param gamePlayer
	 *            the GamePlayer for this session
	 */
	public GeneralParser(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
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
	 * not valid.
	 * 
	 * @param input
	 *            the input
	 */
	public void parse(String input) {
		// Trimmed, lower case, no multiple spaces
		input = input.trim().toLowerCase().replaceAll("\\p{Blank}+", " ");

		for (CommandRecExec cmdRE : commandRecExecs) {
			if (cmdRE.recognizeAndExecute(input)) {
				return;
			}
		}
		// No command matched
		gamePlayer.noCommand();
	}
}