package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.Location;
import data.Way;

/**
 * Managing access to the ways in the database.
 * 
 * @author Satia
 */
public class WayManager {

	/**
	 * Gets the way out from the given location with the given identifier or
	 * {@code null} , if there is none.
	 * 
	 * @param location
	 *            the location
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Way getWayOutFromLocation(Location location, String identifier) {
		return IdentifiableObjectManager.getIdentifiableWithIdentifier(
				location.getWaysOut(), identifier);
	}
	
	/**
	 * @return a set of all additional travel commands defined anywhere in the
	 *         game.
	 */
	public static Set<String> getAllAdditionalTravelCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = PersistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALTRAVELCOMMANDS FROM WAY_ADDITIONALTRAVELCOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}
}