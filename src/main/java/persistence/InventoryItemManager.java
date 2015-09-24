package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Managing access to the inventory items in the database.
 * 
 * @author Satia
 */
public class InventoryItemManager {

	/**
	 * @return a set of all additional combine commands defined anywhere in the
	 *         game.
	 */
	public static Set<String> getAllAdditionaTakeCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = PersistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.COMMANDS FROM InventoryItem$CombineCommands_COMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}
}
