package persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	 * TODO some backwards compatibility tests
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
		// Add updating logic here.
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				String.format("Updating the database model %d.%d -> %d.%d", fromMajor, fromMinor, toMajor, toMinor));
	}

}
