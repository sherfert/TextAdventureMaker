package logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for copying the DB file into the existing game jar.
 * 
 * @author Satia
 *
 */
public class JARCreator {

	/**
	 * Copies the game database with the new filename game.h2.db into a new
	 * Game.jar with the contents of Game_missing_db.jar.
	 * 
	 * @param gameDB the path to the game database to copy
	 */
	public static void copyGameDBIntoGameJAR(String gameDB) {
		// Copy game db into temp folder with name game.h2.db
		File gameDBFile = new File(gameDB);
		// create a temp file
		File temp = new File("game.h2.db");

		try {
			Files.copy(gameDBFile.toPath(), temp.toPath());
		} catch (IOException e) {
			Logger.getLogger(JARCreator.class.getName()).log(Level.SEVERE,
					"Could not copy game db into temporary file. Aborting.", e);
			return;
		}

		String jarSourceS = System.getProperty("user.dir") + File.separator
				+ "Game_missing_db.jar";
		String jarDestS = System.getProperty("user.dir") + File.separator
				+ "Game.jar";
		if (!new File(jarSourceS).exists()) {
			// Not running from JAR, use "target" subfolder
			jarSourceS = System.getProperty("user.dir") + File.separator
					+ "target" + File.separator + "Game_missing_db.jar";
			jarDestS = System.getProperty("user.dir") + File.separator
					+ "target" + File.separator + "Game.jar";
		}

		copyFileIntoJar(temp.getAbsolutePath(), jarSourceS, jarDestS);

		// Delete temp file again
		temp.delete();
	}

	/**
	 * Creates a new jar file that contains everything from the source jar file
	 * plus the given file as well.
	 * 
	 * @param toCopy
	 *            the file to include in the new jar.
	 * @param jarSourceS
	 *            the source jar
	 * @param jarDest
	 *            the dest jar
	 */
	public static void copyFileIntoJar(String toCopy, String jarSourceS,
			String jarDest) {

		// try with resources
		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(
				jarDest));
				JarInputStream jis = new JarInputStream(new FileInputStream(
						jarSourceS));
				JarFile jarSource = new JarFile(jarSourceS)) {

			// Copy all elements from the source jar into the dest jar.
			Enumeration<JarEntry> entries = jarSource.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				try (InputStream is = jarSource.getInputStream(entry)) {

					jos.putNextEntry(new JarEntry(entry.getName()));
					byte[] buffer = new byte[4096];
					int bytesRead = 0;
					while ((bytesRead = is.read(buffer)) != -1) {
						jos.write(buffer, 0, bytesRead);
					}
					jos.flush();
					jos.closeEntry();
				}
			}

			File fToCopy = new File(toCopy);
			// Copy the file to copy into the new jar
			try (InputStream is = new FileInputStream(toCopy)) {

				jos.putNextEntry(new JarEntry(fToCopy.getName()));
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					jos.write(buffer, 0, bytesRead);
				}
			}

		} catch (IOException e) {
			Logger.getLogger(JARCreator.class.getName()).log(Level.SEVERE,
					"Problem copying the file into the JAR.", e);
		}
	}

}
