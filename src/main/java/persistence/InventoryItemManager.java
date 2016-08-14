package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.InventoryItem;

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
	 */
	public Set<String> getAllAdditionaCombineCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.COMMANDS FROM InventoryItem$CombineCommands_COMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}
	
	/**
	 * @return a set of all additional use with commands defined anywhere in the
	 *         game.
	 */
	public Set<String> getAllAdditionaUseWithCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALUSEWITHCOMMANDS FROM InventoryItem$UsableHasLocation_ADDITIONALUSEWITHCOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}
	
	/**
	 * @return all inventory items in the game.
	 */
	public List<InventoryItem> getAllInventoryItems() {
		CriteriaQuery<InventoryItem> query = persistenceManager
				.getCriteriaBuilder().createQuery(InventoryItem.class);
		Root<InventoryItem> root = query.from(InventoryItem.class);
		query.select(root);
		List<InventoryItem> resultList = persistenceManager.getEntityManager()
				.createQuery(query).getResultList();
		return resultList;
	}
}
