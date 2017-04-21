package persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.action.AbstractAction;
import exception.DBClosedException;

/**
 * Managing access to the actions in the database.
 * 
 * @author Satia
 */
public class ActionManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public ActionManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * Obtains all actions that are not marked as hidden.
	 * 
	 * @return all actions in the game.
	 * @throws DBClosedException
	 */
	public List<AbstractAction> getAllActions() throws DBClosedException {
		CriteriaBuilder cb = persistenceManager.getCriteriaBuilder();
		CriteriaQuery<AbstractAction> query = cb.createQuery(AbstractAction.class);
		Root<AbstractAction> root = query.from(AbstractAction.class);
		query.where(cb.equal(root.get("hidden"), false));
		List<AbstractAction> resultList = persistenceManager.getEntityManager().createQuery(query).getResultList();
		// Lists with cascade definitions are not refreshed automatically
		for (AbstractAction a : resultList) {
			persistenceManager.getEntityManager().refresh(a);
		}
		return resultList;
	}

}