package playing.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import persistence.GameManager;
import playing.GamePlayer;

public class GeneralParser {

	public static enum Command {
		MOVE(GameManager.getGame().getMoveCommands(), "move", String.class),
		TAKE(GameManager.getGame().getTakeCommands(), "take", String.class),
		INVENTORY(GameManager.getGame().getInventoryCommands(), "inventory");

		public final String methodName;

		public final Class<?>[] parameterTypes;

		public final Pattern pattern;

		private Command(List<String> commands, String methodName,
				Class<?>... parameterTypes) {
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.pattern = PatternGenerator.getPattern(commands);
		}
	}

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