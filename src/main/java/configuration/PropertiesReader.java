package configuration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

	public static final String LOG_LEVEL_PROPERTY = "LogLevel";

	public static final String LOG_LEVEL_DEFAULT = "CONFIG";

	public static final String DIRECTORY = System
		.getProperty("user.home")
		+ File.separator
		+ ".textAdventureMaker"
		+ File.separator;

	private static final String propertiesFilePath = DIRECTORY
		+ "textAdventureMaker.properties";

	// TODO Do NOT read the file every time!!??
	/**
	 * To get the properties object
	 *
	 * @return properties object
	 */
	public static Properties getProperties() {
		return loadFile();
	}

	/**
	 * To get one value of the given parameter
	 *
	 * @param param the parameter of the property
	 * @return the value, if it exists in the property file a default value,
	 * if it don't exists in the property file, but it exists a default
	 * value else null
	 */
	public static String getProperty(String param) {
		Properties property = loadFile();

		if (property.getProperty(param) == null) {

			String defaultPropertyValue = createDefaultProperty().getProperty(
				param);

			// A default value for the param exists, but is still not in the
			// file
			// Add this to the file
			if (defaultPropertyValue != null) {

				property.setProperty(param, defaultPropertyValue);

				try {
					FileOutputStream outputStream = new FileOutputStream(
						propertiesFilePath);
					property.store(outputStream,
						"# Configuration file for TextAdventureMaker (default value for "
						+ param + " added)");
					outputStream.close();
				} catch (IOException e) {
					Logger.getLogger(PropertiesReader.class.getName()).log(Level.WARNING,
						"Could not add the not existing default property to the property-file: "
						+ param + ": " + defaultPropertyValue, e);
					return defaultPropertyValue;
				}
			}

			return defaultPropertyValue;
		}

		return property.getProperty(param);
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
				BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(propertiesFilePath));
				property.load(stream);
				stream.close();
				return result = property;
			} else {
				return result = createPropertyFile();
			}
		} catch (IOException e) {
			Logger.getLogger(PropertiesReader.class.getName()).log(Level.WARNING,
				"Problem getting or creating the properties file. Using default values.", e);

			return result;

		}
	}

	/**
	 * To create a properties file with default values
	 *
	 * @throws IOException if the file cannot created
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
		return props;
	}

	public static void logConfiguration() {
		// Log every configuration
		for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
			Logger.getLogger(PropertiesReader.class.getName()).log(Level.CONFIG,
				"Property: {0} -> {1}", new Object[]{entry.getKey(), entry.getValue()});
		}
	}
}
