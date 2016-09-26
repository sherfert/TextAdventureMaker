package gui.utility;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import gui.MainWindowController;
import javafx.fxml.FXMLLoader;

/**
 * Custom loader as extension for {@link FXMLLoader}.
 * @author satia
 *
 */
public class Loader {

	/**
	 * Loads an FXML object with the given root and controller.
	 * 
	 * @param root
	 *            the root
	 * @param controller
	 *            the controller
	 * @param fxml
	 *            the fxml
	 * @return the loaded node.
	 */
	public static Object load(Object root, Object controller, String fxml) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource(fxml));
			loader.setController(controller);
			loader.setRoot(root);
			return loader.load();
		} catch (IOException e) {
			Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, "Could not set fxml content.", e);
			return null;
		}
	}
}