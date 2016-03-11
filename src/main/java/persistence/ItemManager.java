package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Item;

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
	
	/**
	 * @return all items in the game.
	 */
	public List<Item> getAllItems() {
		// Find all items
		CriteriaQuery<Item> query = persistenceManager
				.getCriteriaBuilder().createQuery(Item.class);
		Root<Item> root = query.from(Item.class);
		query.select(root);
		List<Item> resultList = persistenceManager.getEntityManager()
				.createQuery(query).getResultList();
		return resultList;
	}

}
