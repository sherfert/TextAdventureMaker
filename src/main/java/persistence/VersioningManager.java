package persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Model;
import exception.DBClosedException;
import exception.DBIncompatibleException;

/**
 * This class is responsible for updating older DBs to newer versions.
 * 
 * @author Satia
 *
 */
public class VersioningManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public VersioningManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * Updates a DB from an older version to a newer version.
	 * 
	 * @param fromMajor
	 *            old major
	 * @param fromMinor
	 *            old minor
	 * @param toMajor
	 *            new major
	 * @param toMinor
	 *            new minor
	 * @throws DBIncompatibleException
	 *             if the new version is older than the "old" version.
	 */
	public void updateDB(int fromMajor, int fromMinor, int toMajor, int toMinor) throws DBIncompatibleException {
		if (toMajor < fromMajor || (toMajor == fromMajor && toMinor < fromMinor)) {
			String msg = String.format(
					"The loaded DB is too new (version %d.%d vs %d.%d). Consider updating TextAdventureMaker.",
					fromMajor, fromMinor, toMajor, toMinor);
			throw new DBIncompatibleException(msg);
		}

		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				String.format("Updating the database model %d.%d -> %d.%d", fromMajor, fromMinor, toMajor, toMinor));

		// try {
		// Add updating logic here.
		// } catch (DBClosedException e) {
		// Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "DB
		// closed unexpectedly.", e);
		// }
	}

	/**
	 * Example how to update the schema.
	 * 
	 * @throws DBClosedException
	 *             should not happen
	 */
	public void from1_0to1_1Example() throws DBClosedException {
		// Change a column name in the Game table
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, String.format(
				"Renaming column in table %s from %s to %s", "Game", "inspectionDefaultText", "inspectDefaultText"));

		persistenceManager.getEntityManager().getTransaction().begin();
		persistenceManager.getEntityManager()
				.createNativeQuery("ALTER TABLE Game ALTER COLUMN inspectionDefaultText RENAME TO inspectDefaultText")
				.executeUpdate();
		persistenceManager.getEntityManager().getTransaction().commit();

		// Update version number
		Model model = persistenceManager.getModel();
		model.setMinorVersion(1);
		persistenceManager.updateChanges();
	}

}
