package persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Conversation;
import exception.DBClosedException;

/**
 * Managing access to the conversations in the database.
 * 
 * @author Satia
 */
public class ConversationManager {
	
	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public ConversationManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}
	
	/**
	 * @return all conversations in the game.
	 * @throws DBClosedException 
	 */
	public List<Conversation> getAllConversations() throws DBClosedException {
		CriteriaQuery<Conversation> query = persistenceManager
				.getCriteriaBuilder().createQuery(Conversation.class);
		Root<Conversation> root = query.from(Conversation.class);
		query.select(root);
		List<Conversation> resultList = persistenceManager.getEntityManager()
				.createQuery(query).getResultList();
		return resultList;
	}

}
