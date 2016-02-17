package utility;

import java.util.regex.Pattern;

import playing.PlaceholderReplacer;

/**
 * This class can be used to convert the regular expressions for matching
 * commands to better understandable Strings and vice versa.
 * 
 * @author Satia
 *
 */
public class CommandRegExConverter {

	/**
	 * The pattern to match first parameters in RegEx: {@literal (?<o0>.+?)}
	 */
	public static final Pattern FIRST_PARAM_REGEX_PATTERN = Pattern.compile("(\\(\\?\\<o0\\>\\.\\+\\?\\))");

	/**
	 * The pattern to match first parameters in text: {@literal <A>}
	 */
	public static final Pattern FIRST_PARAM_TEXT_PATTERN = Pattern.compile("(\\<A\\>)");

	/**
	 * The text replacement for first parameters.
	 */
	public static final String FIRST_PARAM_TEXT_REPLACEMENT = "<A>";

	/**
	 * The RegEx replacement for first parameters.
	 */
	public static final String FIRST_PARAM_REGEX_REPLACEMENT = "(?<o0>.+?)";

	/**
	 * The pattern to match second parameters in RegEx: {@literal (?<o1>.+?) }
	 */
	public static final Pattern SECOND_PARAM_REGEX_PATTERN = Pattern.compile("(\\(\\?\\<o1\\>\\.\\+\\?\\))");

	/**
	 * The pattern to match second parameters in text: {@literal <B>}
	 */
	public static final Pattern SECOND_PARAM_TEXT_PATTERN = Pattern.compile("(\\<B\\>)");

	/**
	 * The text replacement for second parameters.
	 */
	public static final String SECOND_PARAM_TEXT_REPLACEMENT = "<B>";

	/**
	 * The RegEx replacement for second parameters.
	 */
	public static final String SECOND_PARAM_REGEX_REPLACEMENT = "(?<o1>.+?)";

	/**
	 * The pattern to match optional parts in RegEx.
	 */
	public static final Pattern OPTIONAL_REGEX_PATTERN = Pattern.compile("(\\((?<op>[^\\(\\)]+?)\\)\\?)");

	/**
	 * The pattern to match optional parts in text.
	 */
	public static final Pattern OPTIONAL_TEXT_PATTERN = Pattern.compile("(\\[(?<op>[^\\[\\]]+?)\\])");

	/**
	 * Converts a command consisting of a regular expression to a normal String
	 * that is better understandable. Optional parts are put into square
	 * brackets and parameters are transformed to {@literal <A>} and {@literal 
	 * <B>}.
	 * 
	 * @param regex
	 *            the regular expression
	 * @return the transformed string.
	 */
	public static String convertRegExToString(String regex) {
		String result = OPTIONAL_REGEX_PATTERN.matcher(regex).replaceAll("[${op}]");
		result = FIRST_PARAM_REGEX_PATTERN.matcher(result).replaceAll(FIRST_PARAM_TEXT_REPLACEMENT);
		result = SECOND_PARAM_REGEX_PATTERN.matcher(result).replaceAll(SECOND_PARAM_TEXT_REPLACEMENT);
		return result;
	}

	/**
	 * Converts a String in the syntax given by
	 * {@link #convertRegExToString(String)} to a regular expression in the
	 * original syntax.
	 * 
	 * @param string
	 *            the string
	 * @return the transformed regular expression string.
	 */
	public static String convertStringToRegEx(String string) {
		String result = OPTIONAL_TEXT_PATTERN.matcher(string).replaceAll("(${op})?");
		result = FIRST_PARAM_TEXT_PATTERN.matcher(result).replaceAll(FIRST_PARAM_REGEX_REPLACEMENT);
		result = SECOND_PARAM_TEXT_PATTERN.matcher(result).replaceAll(SECOND_PARAM_REGEX_REPLACEMENT);
		return result;
	}

	/**
	 * Converts a command consisting of a regular expression to a replacement
	 * String for the class {@link PlaceholderReplacer}.
	 * 
	 * @param regex
	 *            the regular expression
	 * @param firstParamReplacement
	 *            the substitution for the first parameter
	 * @param secondParamReplacement
	 *            the substitution for the second parameter
	 * @return the transformed string.
	 */
	public static String convertRegExToReplacementString(String regex, String firstParamReplacement,
			String secondParamReplacement) {
		// Remove optional parts
		String result = OPTIONAL_REGEX_PATTERN.matcher(regex).replaceAll("");
		result = FIRST_PARAM_REGEX_PATTERN.matcher(result).replaceAll(firstParamReplacement);
		result = SECOND_PARAM_REGEX_PATTERN.matcher(result).replaceAll(secondParamReplacement);
		return result;
	}

}
