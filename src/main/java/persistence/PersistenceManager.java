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
	private CriteriaBuilder criteriaBuilder;

	/**
	 * The entity manager.
	 */
	private EntityManager entityManager;

	/**
	 * The entity manager factory.
	 */
	private EntityManagerFactory entityManagerFactory;

	// All sub-managers responsible for certain classes in the DB
	private AllObjectsManager allObjectsManager;
	private GameManager gameManager;
	private IdentifiableObjectManager identifiableObjectManager;
	private InspectableObjectManager inspectableObjectManager;
	private InventoryItemManager inventoryItemManager;
	private ItemManager itemManager;
	private PersonManager personManager;
	private PlayerManager playerManager;
	private UsableObjectManager usableObjectManager;
	private WayManager wayManager;

	/**
	 * Creates a persistence manager with all its subordinate managers.
	 */
	public PersistenceManager() {
		this.allObjectsManager = new AllObjectsManager(this);
		this.gameManager = new GameManager(this);
		this.identifiableObjectManager = new IdentifiableObjectManager(this);
		this.inspectableObjectManager = new InspectableObjectManager(this);
		this.inventoryItemManager = new InventoryItemManager(this);
		this.itemManager = new ItemManager(this);
		this.personManager = new PersonManager(this);
		this.playerManager = new PlayerManager(this);
		this.usableObjectManager = new UsableObjectManager(this);
		this.wayManager = new WayManager(this);
	}

	/**
	 * Connects to the database.
	 * 
	 * @param dropTables
	 *            if {@code true}, the database contents will be deleted
	 *            entirely, all tables are dropped and recreated.
	 * @param filename
	 *            the file to connect to.
	 */
	public void connect(String filename, boolean dropTables) {
		Logger.getLogger(PersistenceManager.class.getName()).log(Level.INFO,
				"Connecting to database {0}. Dropping tables: {1}", new Object[] { filename, dropTables });

		String ddlGenerationValue = dropTables ? "drop-and-create-tables" : "create-tables";
		// Create objects for database access
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + filename);
		properties.put("eclipselink.ddl-generation", ddlGenerationValue);

		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

		entityManager = entityManagerFactory.createEntityManager();
		criteriaBuilder = entityManager.getCriteriaBuilder();
	}

	/**
	 * Disconnects from the database.
	 */
	public void disconnect() {
		if (entityManagerFactory != null) {
			Logger.getLogger(PersistenceManager.class.getName()).log(Level.INFO, "Disconnecting from database");

			// Reset the game manager, so that it will load the new game after
			// connecting to a new DB
			this.gameManager.reset();

			// Close everything
			entityManager.close();
			entityManagerFactory.close();
		}
	}

	/**
	 * Updates any changes. Should be called after each change of persisted
	 * data.
	 */
	public void updateChanges() {
		if (entityManager != null) {
			entityManager.getTransaction().begin();
			entityManager.getTransaction().commit();
		}
	}

	/**
	 * @return the criteriaBuilder
	 */
	public CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}

	/**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @return the allObjectsManager
	 */
	public AllObjectsManager getAllObjectsManager() {
		return allObjectsManager;
	}

	/**
	 * @return the gameManager
	 */
	public GameManager getGameManager() {
		return gameManager;
	}

	/**
	 * @return the identifiableObjectManager
	 */
	public IdentifiableObjectManager getIdentifiableObjectManager() {
		return identifiableObjectManager;
	}

	/**
	 * @return the inspectableObjectManager
	 */
	public InspectableObjectManager getInspectableObjectManager() {
		return inspectableObjectManager;
	}

	/**
	 * @return the inventoryItemManager
	 */
	public InventoryItemManager getInventoryItemManager() {
		return inventoryItemManager;
	}

	/**
	 * @return the itemManager
	 */
	public ItemManager getItemManager() {
		return itemManager;
	}

	/**
	 * @return the personManager
	 */
	public PersonManager getPersonManager() {
		return personManager;
	}

	/**
	 * @return the playerManager
	 */
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	/**
	 * @return the usableObjectManager
	 */
	public UsableObjectManager getUsableObjectManager() {
		return usableObjectManager;
	}

	/**
	 * @return the wayManager
	 */
	public WayManager getWayManager() {
		return wayManager;
	}

}
