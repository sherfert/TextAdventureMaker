package persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

public class PersistenceManager {

	/**
	 * The name of the persistence unit. Specified in
	 * src/main/resources/persistence.xml
	 */
	public static final String PERSISTENCE_UNIT_NAME = "textAdventureMaker";

	/**
	 * The criteria builder.
	 */
	private static CriteriaBuilder criteriaBuilder;

	/**
	 * The entity manager.
	 */
	private static EntityManager entityManager;

	/**
	 * The entity manager factory.
	 */
	private static EntityManagerFactory entityManagerFactory;
	
	/**
	 * Connects to the database.
	 * 
	 * @param filename the filename to connect to
	 */
	public static void connect(String filename) {
		Logger.getLogger(PersistenceManager.class.getName()).log(Level.INFO,
			"Connecting to database {0}", filename);
		
		// Create objects for database access
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + filename);
		
		entityManagerFactory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

		entityManager = entityManagerFactory.createEntityManager();
		criteriaBuilder = entityManager.getCriteriaBuilder();

		// Begin a transaction
		entityManager.getTransaction().begin();
	}

	/**
	 * Disconnects from the database.
	 */
	public static void disconnect() {
		Logger.getLogger(PersistenceManager.class.getName()).log(Level.INFO,
			"Disconnecting from database");
		
		entityManager.getTransaction().commit();
		// Close everything
		entityManager.close();
		entityManagerFactory.close();
		// FIXME at the moment close the vm
		System.exit(0);
	}
	
	/**
	 * @return the criteriaBuilder
	 */
	public static CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}

	/**
	 * @return the entityManager
	 */
	public static EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * Updates any changes. Should be called after each change of persisted
	 * data.
	 * 
	 * TODO call this in triggerAction or not?
	 */
	public static void updateChanges() {
		entityManager.getTransaction().commit();
		entityManager.getTransaction().begin();
	}

}
