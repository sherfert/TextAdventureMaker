package configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read the information of a properties file.
 * 
 * @author Satia
 * 
 */
public class PropertiesReader {

	//********* PROPERTIES **************
	public static final String LOG_LEVEL_PROPERTY = "LogLevel";
	public static final String LOG_LEVEL_DEFAULT = "CONFIG";
	
	public static final String SWING_TERMINAL_ROWS_PROPERTY = "SwingTerminalRows";
	public static final String SWING_TERMINAL_ROWS_DEFAULT = "40";
	
	public static final String SWING_TERMINAL_COLS_PROPERTY = "SwingTerminalCols";
	public static final String SWING_TERMINAL_COLS_DEFAULT = "150";
	
	public static final String LOG_CONSOLE_PROPERTY = "LogToConsole";
	public static final String LOG_CONSOLE_DEFAULT = "false";
	//***********************************

	/**
	 * The directory to store configuration and other stuff:
	 * ~/.textAdventureMaker
	 */
	public static final String DIRECTORY = System.getProperty("user.home")
			+ File.separator + ".textAdventureMaker" + File.separator;
	/** Path to the properties file */
	private static final String propertiesFilePath = DIRECTORY
			+ "textAdventureMaker.properties";

	/** The properties */
	private static Properties properties;

	/**
	 * @return the properties
	 */
	public static synchronized Properties getProperties() {
		if (properties == null) {
			properties = loadFile();
		}
		return properties;
	}

	/**
	 * Get the value of the given parameter.
	 * 
	 * @param param
	 *            the parameter of the property
	 * @return the value, if it exists in the property file. A default value, if
	 *         it does not, but it exists a default value. Else {@code null}.
	 */
	public static String getProperty(String param) {
		Properties properties = getProperties();

		if (properties.getProperty(param) == null) {

			String defaultPropertyValue = createDefaultProperty().getProperty(
					param);

			// A default value for the param exists, but is still not in the
			// file. Add this to the file.
			if (defaultPropertyValue != null) {
				properties.setProperty(param, defaultPropertyValue);

				try {
					FileOutputStream outputStream = new FileOutputStream(
							propertiesFilePath);
					properties.store(outputStream,
							"# Configuration file for TextAdventureMaker (default value for "
									+ param + " added)");
					outputStream.close();
				} catch (IOException e) {
					Logger.getLogger(PropertiesReader.class.getName()).log(
							Level.WARNING,
							"Could not add the not existing default property to the property-file: "
									+ param + ": " + defaultPropertyValue, e);
					return defaultPropertyValue;
				}
			}

			return defaultPropertyValue;
		}

		return properties.getProperty(param);
	}

	/**
	 * to load a properties file
	 * 
	 * @return the properties loaded from the file
	 */
	private static Properties loadFile() {
		File file = new File(propertiesFilePath);

		Properties result = createDefaultProperty();
		try {
			if (file.exists()) {
				Properties property = new Properties();
				InputStream stream = new FileInputStream(propertiesFilePath);
				property.load(stream);
				stream.close();
				return result = property;
			} else {
				return result = createPropertyFile();
			}
		} catch (IOException e) {
			Logger.getLogger(PropertiesReader.class.getName())
					.log(Level.WARNING,
							"Problem getting or creating the properties file. Using default values.",
							e);

			return result;

		}
	}

	/**
	 * To create a properties file with default values
	 * 
	 * @throws IOException
	 *             if the file cannot created
	 */
	private static Properties createPropertyFile() throws IOException {
		File properties = new File(propertiesFilePath);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(properties));
		Properties result = createDefaultProperty();
		result.store(bos,
				"# Configuration file for TextAdventureMaker (default generated)");
		bos.close();

		Logger.getLogger(PropertiesReader.class.getName()).log(Level.INFO,
				"A new property-File with default values was generated in {0}",
				propertiesFilePath);

		return result;
	}

	/**
	 * Generate a property with default data.
	 * 
	 * @return the default property
	 */
	private static Properties createDefaultProperty() {
		Properties props = new Properties();
		// Set Properties for Logger
		props.setProperty(LOG_LEVEL_PROPERTY, LOG_LEVEL_DEFAULT);
		props.setProperty(SWING_TERMINAL_ROWS_PROPERTY, SWING_TERMINAL_ROWS_DEFAULT);
		props.setProperty(SWING_TERMINAL_COLS_PROPERTY, SWING_TERMINAL_COLS_DEFAULT);
		props.setProperty(LOG_CONSOLE_PROPERTY, LOG_CONSOLE_DEFAULT);
		return props;
	}

	public static void logConfiguration() {
		// Log every configuration
		for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
			Logger.getLogger(PropertiesReader.class.getName()).log(
					Level.CONFIG, "Property: {0} -> {1}",
					new Object[] { entry.getKey(), entry.getValue() });
		}
	}
}
