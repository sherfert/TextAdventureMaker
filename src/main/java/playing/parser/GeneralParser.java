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

	public static void parse(String input) {
		// TODO
		System.out.print("You wrote: " + input);

		Pattern movePattern = PatternGenerator.getPattern(GameManager.getGame()
				.getMoveCommands());
		Matcher matcher = movePattern.matcher(input);

		if (matcher.matches()) {
			String[] params = getParameters(matcher);

			Method moveMethod;
			try {
				moveMethod = GamePlayer.class.getDeclaredMethod("move",
						String.class);
				if (moveMethod.getParameterTypes().length == params.length) {
					moveMethod.invoke(null, (Object[]) params);
				} else {
					// TODO
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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