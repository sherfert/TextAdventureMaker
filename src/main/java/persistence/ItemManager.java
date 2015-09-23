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
	 * @return a set of all additional take commands defined anywhere in the
	 *         game.
	 */
	public static Set<String> getAllAdditionaTakeCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = PersistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALTAKECOMMANDS FROM Item_ADDITIONALTAKECOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}

}
