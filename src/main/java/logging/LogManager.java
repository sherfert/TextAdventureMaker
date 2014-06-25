package logging;

import configuration.PropertiesReader;
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

	// TODO use an xml file. Like that the PropertiesReader Logs are also
	// Written into the log file! or not?
	
	/**
	 * Static initializer block
	 */
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format",
			"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
		Logger rootLogger = Logger.getLogger("");
		
		// Create the directory to put logs into
		String dirName = PropertiesReader.DIRECTORY + "logs";
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
		
		// Get the configured log level
		try {
			rootLogger.setLevel(Level.parse(PropertiesReader.getProperty(PropertiesReader.LOG_LEVEL_PROPERTY)));
		} catch (IllegalArgumentException e) {
			Logger.getLogger(LogManager.class.getName()).log(Level.WARNING,
				"Logging propery faulty. Thus logging ALL. ", e);
			rootLogger.setLevel(Level.ALL);
		}
		
		// Log the configuration
		PropertiesReader.logConfiguration();
	}
}
