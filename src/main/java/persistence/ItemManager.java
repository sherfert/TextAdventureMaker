package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;

import data.Item;
import exception.DBClosedException;

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
	 * @throws DBClosedException
	 */
	public Set<String> getAllAdditionaTakeCommands() throws DBClosedException {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager.getEntityManager()
				.createNativeQuery("SELECT DISTINCT c.ADDITIONALTAKECOMMANDS FROM Item_ADDITIONALTAKECOMMANDS c")
				.getResultList();

		return new HashSet<>(resultList);
	}

	/**
	 * @return all items in the game.
	 * @throws DBClosedException
	 */
	public List<Item> getAllItems() throws DBClosedException {
		persistenceManager.updateChanges();
		
		CriteriaQuery<Item> query = persistenceManager.getCriteriaBuilder().createQuery(Item.class);
		query.from(Item.class);
		List<Item> resultList = persistenceManager.getEntityManager().createQuery(query).getResultList();
		// Lists with cascade definitions are not refreshed automatically
		for (Item i : resultList) {
			persistenceManager.getEntityManager().refresh(i);
		}
		return resultList;
	}

}
