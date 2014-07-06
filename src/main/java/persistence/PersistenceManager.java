package persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * Manages connections to database files.
 * 
 * @author Satia
 */
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
	 * Connects to the database. TODO save in ram first!?
	 * 
	 * @param filename
	 *            the filename to connect to
	 * @param dropTables
	 *            if {@code true}, the database contents will be deleted
	 *            entirely, all tables are dropped and recreated.
	 */
	public static void connect(String filename, boolean dropTables) {
		Logger.getLogger(PersistenceManager.class.getName()).log(Level.INFO,
				"Connecting to database {0}. Dropping tables: {1}",
				new Object[] { filename, dropTables });

		String ddlGenerationValue = dropTables ? "drop-and-create-tables"
				: "create-tables";
		// Create objects for database access
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + filename);
		properties.put("eclipselink.ddl-generation", ddlGenerationValue);

		entityManagerFactory = Persistence.createEntityManagerFactory(
				PERSISTENCE_UNIT_NAME, properties);

		entityManager = entityManagerFactory.createEntityManager();
		criteriaBuilder = entityManager.getCriteriaBuilder();

		// Begin a transaction
		entityManager.getTransaction().begin();
	}

	/**
	 * Disconnects from the database.
	 */
	public static void disconnect() {
		if(entityManagerFactory != null) {
			Logger.getLogger(PersistenceManager.class.getName()).log(Level.INFO,
					"Disconnecting from database");

			entityManager.getTransaction().commit();
			// Close everything
			entityManager.close();
			entityManagerFactory.close();
		}
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
	 */
	public static void updateChanges() {
		entityManager.getTransaction().commit();
		entityManager.getTransaction().begin();
	}

}
