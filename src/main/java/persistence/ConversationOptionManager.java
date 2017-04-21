package persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

import data.Conversation;
import data.ConversationOption;
import exception.DBClosedException;

/**
 * Managing access to the conversation options in the database.
 * 
 * @author Satia
 */
public class ConversationOptionManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public ConversationOptionManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return all conversation options in the game.
	 * @throws DBClosedException
	 */
	public List<ConversationOption> getAllConversationOptions() throws DBClosedException {
		persistenceManager.updateChanges();
		
		CriteriaQuery<ConversationOption> query = persistenceManager.getCriteriaBuilder()
				.createQuery(ConversationOption.class);
		query.from(Conversation.class);
		List<ConversationOption> resultList = persistenceManager.getEntityManager().createQuery(query).getResultList();
		// Lists with cascade definitions are not refreshed automatically
		for (ConversationOption o : resultList) {
			persistenceManager.getEntityManager().refresh(o);
		}
		return resultList;
	}

}
