package playing.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A generator for regular expression patterns
 * 
 * @author Satia
 */
public class PatternGenerator {
	/**
	 * Builds a MultiPattern that will match any of the given commands. If the list
	 * is empty the pattern will not match anything.
	 * 
	 * @param cmds
	 *            the valid commands
	 * @return a MultiPattern for the commands.
	 */
	public static MultiPattern getPattern(Collection<String> cmds) {
		List<Pattern> patterns = new ArrayList<>();
		for(String cmd : cmds) {
			patterns.add(Pattern.compile(cmd));
		}
		return new MultiPattern(patterns);
	}

	/**
	 * A class composing multiple patterns by logical OR. It offers exactly one
	 * methods to get a Matcher.
	 * 
	 * @author Satia
	 */
	public static class MultiPattern {

		/**
		 * A pattern matching nothing.
		 */
		private static Pattern matchNothing = Pattern.compile("a^");

		/**
		 * The ORed patterns.
		 */
		private List<Pattern> patterns;

		/**
		 * @param patterns
		 *            the patterns
		 */
		private MultiPattern(List<Pattern> patterns) {
			this.patterns = patterns;
		}

		/**
		 * Returns the matcher for the first patterns that matches, or a matcher
		 * that doesn't match, if no pattern matches.
		 * 
		 * @param input
		 *            the input to match.
		 * @return s.a.
		 */
		public Matcher matcher(CharSequence input) {
			if (patterns.isEmpty()) {
				return matchNothing.matcher(input);
			}
			Matcher m;
			for (Pattern p : patterns) {
				m = p.matcher(input);
				if (m.matches()) {
					return m;
				}
			}
			return matchNothing.matcher(input);
		}
	}
}