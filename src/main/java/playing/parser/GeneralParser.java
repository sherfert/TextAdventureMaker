package playing.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import persistence.GameManager;
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
	public static enum Command {
		MOVE(GameManager.getGame().getMoveCommands(), "move", String.class), TAKE(
				GameManager.getGame().getTakeCommands(), "take", String.class), INVENTORY(
				GameManager.getGame().getInventoryCommands(), "inventory");

		/**
		 * The name of the method that should be invoked when this command was
		 * recognized. Must be a static method of the class {@link GamePlayer}.
		 */
		public final String methodName;

		/**
		 * The parameter types of the method denoted by methodName.
		 */
		public final Class<?>[] parameterTypes;

		/**
		 * The pattern for this command that can be used to match input Strings
		 * against.
		 */
		public final Pattern pattern;

		/**
		 * 
		 * @param commands
		 *            a list of regular expressions for each possible way to
		 *            execute this command
		 * @param methodName
		 *            the name of the method that should be invoked when this
		 *            command was recognized. Must be a static method of the
		 *            class {@link GamePlayer}.
		 * @param parameterTypes
		 *            the parameter types of the method denoted by methodName.
		 */
		private Command(List<String> commands, String methodName,
				Class<?>... parameterTypes) {
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.pattern = PatternGenerator.getPattern(commands);
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
	public static void parse(String input) {
		// Trimmed, lower case, no multiple spaces
		input = input.trim().toLowerCase().replaceAll("\\p{Blank}+", " ");

		for (Command command : Command.values()) {
			Matcher matcher = command.pattern.matcher(input);

			if (matcher.matches()) {
				String[] params = getParameters(matcher);

				try {
					Method method = GamePlayer.class.getDeclaredMethod(
							command.methodName, command.parameterTypes);
					if (method.getParameterTypes().length == params.length) {
						method.invoke(null, (Object[]) params);
					} else {
						// TODO
					}
				} catch (NoSuchMethodException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
		// No command matched
		GamePlayer.noCommand();
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
}