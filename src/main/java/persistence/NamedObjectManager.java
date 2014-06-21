package persistence;

import java.util.List;

import data.NamedObject;
import data.interfaces.Identifiable;

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

	/**
	 * Gets the named object that macthes the given identifier-regexp or
	 * {@code null} , if there is none.
	 * 
	 * @param identifiables
	 *            the identifiables to search
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding object or {@code null}.
	 */
	public static <E extends Identifiable> E getIdentifiableWithIdentifier(
			List<E> identifiables, String identifier) {
		for (E object : identifiables) {
			for (String itemIdentifier : object.getIdentifiers()) {
				if (identifier.matches(itemIdentifier)) {
					return object;
				}
			}
		}
		return null;
	}
}