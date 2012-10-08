package persistence;

import java.util.List;

import data.NamedObject;
import data.interfaces.Inspectable;

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
		Main.getEntityManager().remove(object);
	}

	/**
	 * Gets the named object that macthes the given identifier-regexp or
	 * {@code null} , if there is none.
	 * 
	 * @param inspectables
	 *            the inspectables to search
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding object or {@code null}.
	 */
	public static <E extends Inspectable> E getInspectableWithIdentifier(
			List<E> inspectables, String identifier) {
		for (E object : inspectables) {
			for (String itemIdentifier : object.getIdentifiers()) {
				if (identifier.matches(itemIdentifier)) {
					return object;
				}
			}
		}
		return null;
	}
}