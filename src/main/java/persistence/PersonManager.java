package persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Person;

/**
 * Managing access to the persons in the database.
 * 
 * @author Satia
 */
public class PersonManager {
	
	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public PersonManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return a set of all additional talk to commands defined anywhere in the
	 *         game.
	 */
	public Set<String> getAllAdditionalTalkToCommands() {
		@SuppressWarnings("unchecked")
		List<String> resultList = persistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALTALKTOCOMMANDS FROM Person_ADDITIONALTALKTOCOMMANDS c")
				.getResultList();
	
		return new HashSet<>(resultList);
	}
	
	/**
	 * @return all persons in the game.
	 */
	public List<Person> getAllPersons() {
		// Find all items
		CriteriaQuery<Person> query = persistenceManager
				.getCriteriaBuilder().createQuery(Person.class);
		Root<Person> root = query.from(Person.class);
		query.select(root);
		List<Person> resultList = persistenceManager.getEntityManager()
				.createQuery(query).getResultList();
		return resultList;
	}

}
