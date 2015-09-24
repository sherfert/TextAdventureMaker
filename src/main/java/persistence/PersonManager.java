package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Managing access to the persons in the database.
 * 
 * @author Satia
 */
public class PersonManager {

	/**
	 * @return a set of all additional talk to commands defined anywhere in the
	 *         game.
	 */
	public static Set<String> getAllAdditionalTalkToCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = PersistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALTALKTOCOMMANDS FROM Person_ADDITIONALTALKTOCOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}

}
