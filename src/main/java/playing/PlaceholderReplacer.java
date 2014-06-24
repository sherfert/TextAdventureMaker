package playing;

import java.util.regex.Pattern;

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
	 * Converts all placeholders for the first parameter in a Sring to
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
	 * The identifier the user used. Is already lowercase.
	 */
	private String identifier;

	/**
	 * The second identifier the user used. Is already lowercase.
	 */
	private String identifier2;

	/**
	 * The input the user typed. Is already lowercase.
	 */
	private String input;

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

		if (input != null) {
			message = message.replaceAll("<INPUT>", input.toUpperCase())
					.replaceAll("<Input>|<input>", input);
		}
		// TODO differentiate normal_case and lower_case
		if (identifier != null) {
			message = message.replaceAll("<IDENTIFIER>",
					identifier.toUpperCase()).replaceAll(
					"<Identifier>|<identifier>", identifier);
		}
		if (identifier2 != null) {
			message = message.replaceAll("<IDENTIFIER2>",
					identifier2.toUpperCase()).replaceAll(
					"<Identifier2>|<identifier2>", identifier2);
		}
		if (name != null) {
			message = message.replaceAll("<NAME>", name.toUpperCase())
					.replaceAll("<Name>", name)
					.replaceAll("<name>", name.toLowerCase());
		}
		if (name2 != null) {
			message = message.replaceAll("<NAME2>", name2.toUpperCase())
					.replaceAll("<Name2>", name2)
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