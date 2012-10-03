package playing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import playing.parser.GeneralParser;
import playing.parser.PatternGenerator;

public class InputOutput {

	private static BufferedReader reader;

	public static void println(String output) {
		// FIXME
		System.out.println(output);
	}

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