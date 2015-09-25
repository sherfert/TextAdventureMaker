package utility;

import java.util.regex.Pattern;

/**
 * This class can be used to convert the regular expressions for matching
 * commands to better understandable Strings and vice versa.
 * 
 * @author Satia
 *
 */
public class CommandRegExConverter {

	/**
	 * The pattern to match first parameters: (?<o0>.+?)
	 */
	public static final Pattern FIRST_PARAM_PATTERN = Pattern
			.compile("(\\(\\?\\<o0\\>\\.\\+\\?\\))");

	/**
	 * The replacement for first parameters.
	 */
	public static final String FIRST_PARAM_TEXT_REPLACEMENT = "<A>";

	/**
	 * The pattern to match second parameters: (?<o1>.+?)
	 */
	public static final Pattern SECOND_PARAM_PATTERN = Pattern
			.compile("(\\(\\?\\<o1\\>\\.\\+\\?\\))");

	/**
	 * The replacement for second parameters.
	 */
	public static final String SECOND_PARAM_TEXT_REPLACEMENT = "<B>";

	/**
	 * The pattern to match optional parts.
	 */
	public static final Pattern OPTIONAL_PATTERN = Pattern
			.compile("(\\((?<op>[^\\(\\)]+?)\\)\\?)");

	/**
	 * Converts a command consisting of a regular expression to a normal String
	 * that is better understandable. Optional parts are put into square
	 * brackets and parameters are transformed to {@literal <A>} and
	 * {@literal <B>}.
	 * 
	 * @param regex
	 *            the regular expression
	 * @return the transformed string.
	 */
	public static String convertRegExToString(String regex) {
		String result = OPTIONAL_PATTERN.matcher(regex).replaceAll("[${op}]");
		result = FIRST_PARAM_PATTERN.matcher(result).replaceAll(
				FIRST_PARAM_TEXT_REPLACEMENT);
		result = SECOND_PARAM_PATTERN.matcher(result).replaceAll(
				SECOND_PARAM_TEXT_REPLACEMENT);
		return result;
	}

	public static String convertStringToRegEx(String string) {
		// TODO
		return "";
	}

}
