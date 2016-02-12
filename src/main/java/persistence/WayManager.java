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
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public WayManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

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
	public Way getWayOutFromLocation(Location location, String identifier) {
		return IdentifiableObjectManager.getIdentifiableWithIdentifier(location.getWaysOut(), identifier);
	}

	/**
	 * @return a set of all additional travel commands defined anywhere in the
	 *         game.
	 */
	public Set<String> getAllAdditionalTravelCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager.getEntityManager()
				.createNativeQuery("SELECT DISTINCT c.ADDITIONALTRAVELCOMMANDS FROM WAY_ADDITIONALTRAVELCOMMANDS c")
				.getResultList();

		return new HashSet<>(resultList);
	}
}