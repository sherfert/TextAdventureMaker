package playing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputOutput {
	
	private static BufferedReader reader;

	public static void println(String output) {
		// FIXME
		System.out.println(output);
	}

	public static void startListeningForInput() {
		reader = new BufferedReader(new InputStreamReader(
				System.in));
		
		String input = null;
		try {
			input = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("You wrote: " + input);
	}
}