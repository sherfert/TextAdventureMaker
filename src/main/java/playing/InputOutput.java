package playing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Providing means to print and read input while playing.
 * 
 * @author Satia
 */
public class InputOutput {

	/**
	 * The input reader.
	 */
	private BufferedReader reader;

	/**
	 * The GamePlayer for this session
	 */
	private GamePlayer gamePlayer;

	/**
	 * @param gamePlayer
	 *            the GamePlayer for this session
	 */
	public InputOutput(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Prints a line of text for the player.
	 * 
	 * @param output
	 *            the text to be printed
	 */
	public void println(String output) {
		// TODO real IO
		System.out.println(output);
	}

	/**
	 * Starts listening for the player's input and parses line by line until an
	 * exit command has been typed.
	 */
	public void startListeningForInput() {
		try {
			while (gamePlayer.getParser().parse(reader.readLine()))
				;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}