package utility;

import java.net.URL;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Window utilities.
 * 
 * @author satia
 */
public class WindowUtil {

	/**
	 * Obtains the URL where the window icon is located.
	 * 
	 * @return the URL to the window icon.
	 */
	public static URL getWindowIconURL() {
		return WindowUtil.class.getClassLoader().getResource("icon.png");
	}

	/**
	 * Attaches the standard icon to a stage
	 * 
	 * @param stage
	 */
	public static void attachIcon(Stage stage) {
		Image img = new Image(getWindowIconURL().toString());
		stage.getIcons().add(img);
	}
}
