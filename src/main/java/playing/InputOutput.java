package playing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import playing.parser.GeneralParser;
import playing.parser.PatternGenerator;

/**
 * Providing means to print and read input while playing.
 * 
 * @author Satia
 */
public class InputOutput {

	/**
	 * The input reader.
	 */
	private static BufferedReader reader;

	/**
	 * Prints a line of text for the player.
	 * 
	 * @param output
	 *            the text to be printed
	 */
	public static void println(String output) {
		// FIXME
		System.out.println(output);
	}

	/**
	 * Starts listening for the player's input and parses line by line until an
	 * exit command has been typed.
	 */
	public static void startListeningForInput() {
		reader = new BufferedReader(new InputStreamReader(System.in));

		Pattern exitPattern = PatternGenerator.getExitPattern();

		String input = null;
		try {
			while (!exitPattern.matcher(input = reader.readLine()).matches()) {
				GeneralParser.parse(input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}