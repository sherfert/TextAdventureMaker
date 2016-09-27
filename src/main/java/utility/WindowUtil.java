package utility;

import java.net.URL;

/**
 * Window utilities.
 * 
 * @author satia *
 */
public class WindowUtil {

	/**
	 * Obtains the URL where the window icon is located.
	 * @return the URL to the window icon.
	 */
	public static URL getWindowIconURL() {
		return WindowUtil.class.getClassLoader().getResource("icon.png");
	}
}
