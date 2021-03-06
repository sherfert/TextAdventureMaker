package playing;

import java.util.regex.Pattern;

import utility.CommandRegExConverter;

/**
 * Replaces placeholders used in game/item/etc. properties with the required
 * text.
 *
 * @author Satia
 */
public class PlaceholderReplacer {

	/**
	 * The pattern used to convert first parameters into second parameters.
	 */
	public static final Pattern CONVERT_FIRST_TO_SECOND_PATTERN = Pattern
			.compile("<(identifier|name)>", Pattern.CASE_INSENSITIVE);

	/**
	 * Converts all placeholders for the first parameter in a String to
	 * placeholders for the second parameter.
	 *
	 * @param message
	 *            the message
	 * @return the message with placeholders for the first parameter converted
	 *         to placeholders for the second parameter.
	 */
	public static String convertFirstToSecondPlaceholders(String message) {
		// Just if something goes terribly wrong
		if (message == null) {
			return "";
		}
		return CONVERT_FIRST_TO_SECOND_PATTERN.matcher(message).replaceAll(
				"<$12>");
	}

	/**
	 * Converts a String consisting of one word into standard case (first letter
	 * uppercase, rest lowercase).
	 * 
	 * @param inputVal
	 *            the input
	 * @return the input in stadard case
	 */
	public static String toStandardCase(String inputVal) {
		// Empty strings should be returned as-is.

		if (inputVal.length() == 0)
			return "";

		// Strings with only one character uppercased.

		if (inputVal.length() == 1)
			return inputVal.toUpperCase();

		// Otherwise uppercase first letter, lowercase the rest.

		return inputVal.substring(0, 1).toUpperCase()
				+ inputVal.substring(1).toLowerCase();
	}

	/**
	 * The input the user typed. Is already lowercase.
	 */
	private String input;

	/**
	 * The pattern that matched the user input.
	 */
	private String pattern;

	/**
	 * The identifier the user used. Is already lowercase.
	 */
	private String identifier;

	/**
	 * The second identifier the user used. Is already lowercase.
	 */
	private String identifier2;

	/**
	 * The name of the object the user addressed.
	 */
	private String name;

	/**
	 * The name of the second object the user addressed.
	 */
	private String name2;

	/**
	 * Replaces placeholders in the given message with the required text.
	 *
	 * @param message
	 *            the message
	 * @return the message with replaced placeholders where possible.
	 */
	public String replacePlaceholders(String message) {
		// Just if something goes terribly wrong
		if (message == null) {
			return "";
		}

		if (pattern != null) {
			String replacement = CommandRegExConverter
					.convertRegExToReplacementString(pattern, "\\$1", "\\$2");
			/*
			 * If the message contains a pattern replacement, that must be done
			 * first, because it can create more replacement strings.
			 */
			message = message.replaceAll("<pattern\\|(.*?)\\|(.*?)\\|>",
					replacement);
		}

		if (input != null) {
			message = message.replaceAll("<INPUT>", input.toUpperCase())
					.replaceAll("<Input>", input)
					.replaceAll("<input>", input.toLowerCase());
		}
		if (identifier != null) {
			message = message
					.replaceAll("<IDENTIFIER>", identifier.toUpperCase())
					.replaceAll("<Identifier>", toStandardCase(identifier))
					.replaceAll("<identifier>", identifier.toLowerCase());
		}
		if (identifier2 != null) {
			message = message
					.replaceAll("<IDENTIFIER2>", identifier2.toUpperCase())
					.replaceAll("<Identifier2>", toStandardCase(identifier2))
					.replaceAll("<identifier2>", identifier2.toLowerCase());
		}
		if (name != null) {
			message = message.replaceAll("<NAME>", name.toUpperCase())
					.replaceAll("<Name>", toStandardCase(name))
					.replaceAll("<name>", name.toLowerCase());
		}
		if (name2 != null) {
			message = message.replaceAll("<NAME2>", name2.toUpperCase())
					.replaceAll("<Name2>", toStandardCase(name2))
					.replaceAll("<name2>", name2.toLowerCase());
		}

		return message;
	}

	/**
	 * Resets all replacement Strings.
	 */
	public void reset() {
		this.input = null;
		this.identifier = null;
		this.identifier2 = null;
		this.name = null;
		this.name2 = null;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param identifier2
	 *            the identifier2 to set
	 */
	public void setIdentifier2(String identifier2) {
		this.identifier2 = identifier2;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * @return the input
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param name2
	 *            the name2 to set
	 */
	public void setName2(String name2) {
		this.name2 = name2;
	}
}
