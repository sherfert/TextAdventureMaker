package playing.parser;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * A generator for regular expression patterns
 * 
 * @author Satia
 */
public class PatternGenerator {
	/**
	 * Builds a pattern that will match any of the given commands. If the list
	 * is empty the pattern will not match anything.
	 * 
	 * @param cmds
	 *            the valid commands
	 * @return a pattern for the commands
	 */
	public static Pattern getPattern(Collection<String> cmds) {
		if (cmds.isEmpty()) {
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