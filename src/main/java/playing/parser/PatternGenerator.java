package playing.parser;

import java.util.List;
import java.util.regex.Pattern;

import persistence.GameManager;

public class PatternGenerator {

	public static Pattern getExitPattern() {
		return getPattern(GameManager.getGame().getExitCommands());
	}

	public static Pattern getPattern(List<String> cmds) {
		if(cmds.isEmpty()) {
			// Pattern matching nothing
			return Pattern.compile("a^");
		}
		// Combine regular expressions with '|' (logical OR)
		StringBuilder sb = new StringBuilder();
		for (String cmd : cmds) {
			sb.append(cmd).append('|');
		}
		// Delete last '|'
		sb.deleteCharAt(sb.length() - 1);
		return Pattern.compile(sb.toString());
	}
}