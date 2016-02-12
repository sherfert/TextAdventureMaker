package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Managing access to the usable objects in the database.
 * 
 * @author Satia
 */
public class UsableObjectManager {
	
	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public UsableObjectManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return a set of all additional use commands defined anywhere in the
	 *         game.
	 */
	public Set<String> getAllAdditionalUseCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALUSECOMMANDS FROM UsableObject_ADDITIONALUSECOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}

}
