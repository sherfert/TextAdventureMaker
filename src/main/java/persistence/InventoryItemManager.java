package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;

import data.InventoryItem;
import exception.DBClosedException;

/**
 * Managing access to the inventory items in the database.
 * 
 * @author Satia
 */
public class InventoryItemManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public InventoryItemManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return a set of all additional combine commands defined anywhere in the
	 *         game.
	 * @throws DBClosedException
	 */
	public Set<String> getAllAdditionaCombineCommands() throws DBClosedException {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager.getEntityManager()
				.createNativeQuery("SELECT DISTINCT c.COMMANDS FROM CombineCommands_COMMANDS c").getResultList();

		return new HashSet<>(resultList);
	}

	/**
	 * @return a set of all additional use with commands defined anywhere in the
	 *         game.
	 * @throws DBClosedException
	 */
	public Set<String> getAllAdditionaUseWithCommands() throws DBClosedException {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALUSEWITHCOMMANDS FROM UseWithInformation_ADDITIONALUSEWITHCOMMANDS c")
				.getResultList();

		return new HashSet<>(resultList);
	}

	/**
	 * @return all inventory items in the game.
	 * @throws DBClosedException
	 */
	public List<InventoryItem> getAllInventoryItems() throws DBClosedException {
		CriteriaQuery<InventoryItem> query = persistenceManager.getCriteriaBuilder().createQuery(InventoryItem.class);
		query.from(InventoryItem.class);
		List<InventoryItem> resultList = persistenceManager.getEntityManager().createQuery(query).getResultList();
		// Lists with cascade definitions are not refreshed automatically
		for (InventoryItem i : resultList) {
			persistenceManager.getEntityManager().refresh(i);
		}
		return resultList;
	}
}
