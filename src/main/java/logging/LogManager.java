package logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class needs to be initiated only. The static initializer block will
 * configure the logger as needed.
 * 
 * @author Satia Herfert
 */
public class LogManager {
	// TODO proper logging everywhere

	/**
	 * The global log level.
	 */
	public static Level LOG_LEVEL = Level.CONFIG;

	/**
	 * Static initializer block
	 */
	static {

		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setLevel(LOG_LEVEL);

		// Create the directory to put logs into
		String dirName = System.getProperty("user.home") + File.separator
				+ ".textAdventureMaker" + File.separator + "logs";
		File file = new File(dirName);
		file.mkdirs();

		try {
			// Add a fileHandler
			FileHandler fileHandler = new FileHandler(dirName + File.separator
					+ "logfile%g.log", 1024 * 1024, 10, true);
			fileHandler.setFormatter(new SimpleFormatter());
			rootLogger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			// Logger not properly initialized. Log it anyway
			Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE,
					"Could not properly initialize logging: ", e);
		}

	}

}
