package persistence;

import java.util.List;

import data.interfaces.Identifiable;

/**
 * Managing access to the inspectable objects in the database.
 * 
 * @author Satia
 */
public class InspectableObjectManager {

	/**
	 * Gets the inspectable object that matches the given identifier-regexp or
	 * {@code null} , if there is none.
	 * 
	 * @param <E> the Identifiable subclass
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