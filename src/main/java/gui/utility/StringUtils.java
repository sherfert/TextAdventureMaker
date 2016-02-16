package gui.utility;

import java.util.regex.Pattern;

/**
 * This class can be used to work with Strings
 * 
 * @author Satia
 *
 */
public class StringUtils {

	/**
	 * A pattern matching any characters illegal in filenames
	 */
	public static final Pattern ILLEGAL_CHARACTERS_PATTERN = Pattern
			.compile("[\\/\\n\\r\\t\\f`\\?\\*\\\\\\<\\>\\|\\\"\\:]");

	/**
	 * Tests if a String represents a safe file name.
	 * 
	 * @param name
	 *            a potential part of a file name
	 * @return {@code true} iff the name contains illegal characters that are
	 *         forbidden in file names.
	 */
	public static boolean isUnsafeFileName(String name) {
		return ILLEGAL_CHARACTERS_PATTERN.matcher(name).find();
	}
}
