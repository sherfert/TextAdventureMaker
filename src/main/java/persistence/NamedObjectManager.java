package persistence;

import data.NamedObject;

/**
 * Managing access to the named objects in the database.
 * 
 * @author Satia
 */
public class NamedObjectManager {
	/**
	 * Removes an object from the database.
	 * 
	 * @param object
	 *            the object
	 */
	public static void removeObject(NamedObject object) {
		PersistenceManager.getEntityManager().remove(object);
	}
}