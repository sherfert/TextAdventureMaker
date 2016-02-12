package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Managing access to the items in the database.
 * 
 * @author Satia
 */
public class ItemManager {
	
	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public ItemManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return a set of all additional take commands defined anywhere in the
	 *         game.
	 */
	public Set<String> getAllAdditionaTakeCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALTAKECOMMANDS FROM Item_ADDITIONALTAKECOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}

}
