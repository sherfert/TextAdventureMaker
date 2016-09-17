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

import playing.menu.LoadSaveManager;

/**
 * This class is responsible for copying the DB file into the existing game jar.
 * 
 * @author Satia
 *
 */
public class JARCreator {

	/**
	 * Copies the game database with the new filename game.mv.db into a new file
	 * with the contents of Game_missing_db.jar.
	 * 
	 * @param gameDB
	 *            the file to the game database to copy
	 * @param jarDest
	 *            the file where to place the final jar
	 */
	public static void copyGameDBIntoGameJAR(File gameDB, File jarDest) throws IOException {
		// Copy game db into temp folder with name game.mv.db
		// create a temp file
		File temp = new File("game" + LoadSaveManager.H2_ENDING);
		try {
			Files.copy(gameDB.toPath(), temp.toPath());
		} catch (IOException e) {
			Logger.getLogger(JARCreator.class.getName()).log(Level.SEVERE,
					"Could not copy game db into temporary file. Aborting.", e);
			throw e;
		}

		String jarSourceS = System.getProperty("user.dir") + File.separator + "Game_missing_db.jar";
		if (!new File(jarSourceS).exists()) {
			// Not running from JAR, use "target" subfolder
			jarSourceS = System.getProperty("user.dir") + File.separator + "target" + File.separator
					+ "Game_missing_db.jar";
		}

		try {
			copyFileIntoJar(temp.getAbsolutePath(), jarSourceS, jarDest.getAbsolutePath());
		} catch(IOException e) {
			Logger.getLogger(JARCreator.class.getName()).log(Level.WARNING, "Could not copy DB into JAR", e);
			throw e;
		} finally {
			// Delete temp file again
			temp.delete();
		}
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
	public static void copyFileIntoJar(String toCopy, String jarSourceS, String jarDest) throws IOException {
		Logger.getLogger(JARCreator.class.getName()).log(Level.INFO, "Copying {0} (temp DB) with contents of {1} into {2}",
				new Object[] { toCopy, jarSourceS, jarDest });
		// try with resources
		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarDest));
				JarInputStream jis = new JarInputStream(new FileInputStream(jarSourceS));
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

		}
	}

}
