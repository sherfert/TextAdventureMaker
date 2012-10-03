package playing.parser;

import java.util.List;
import java.util.regex.Pattern;

import persistence.GameManager;

public class PatternGenerator {

	// TODO method necessary?
	public static Pattern getExitPattern() {
		return getPattern(GameManager.getGame().getExitCommands());
	}

	public static Pattern getPattern(List<String> cmds) {
		// TODO 0 commands?
		StringBuilder sb = new StringBuilder();
		for (String cmd : cmds) {
			sb.append(cmd.toLowerCase()).append('|');
		}
		// Delete last '|'
		sb.deleteCharAt(sb.length() - 1);
		return Pattern.compile(sb.toString());
	}
}