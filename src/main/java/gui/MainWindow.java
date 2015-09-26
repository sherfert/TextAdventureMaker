package gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import logic.JARCreator;
import configuration.PropertiesReader;

/**
 * Test class to have a GUI and a playing main class and different JARs.
 * 
 * @author Satia
 *
 */
public class MainWindow {

	public static void main(String[] args) throws IOException {
		String gameDB = PropertiesReader.DIRECTORY + "Test-Adventure"
				+ ".h2.db";
		// Copy our TestAdvanture into the Game_missing_db.jar
		JARCreator.copyGameDBIntoGameJAR(gameDB);

		JOptionPane.showMessageDialog(null, "That's all.");
	}

}
