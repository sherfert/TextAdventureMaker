package persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;

import exception.DBClosedException;

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
	private LocationManager locationManager;
	private PersonManager personManager;
	private PlayerManager playerManager;
	private UsableObjectManager usableObjectManager;
	private WayManager wayManager;
	private ConversationManager conversationManager;
	private ConversationOptionManager conversationOptionManager;
	private ActionManager actionManager;

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
		this.locationManager = new LocationManager(this);
		this.personManager = new PersonManager(this);
		this.playerManager = new PlayerManager(this);
		this.usableObjectManager = new UsableObjectManager(this);
		this.wayManager = new WayManager(this);
		this.conversationManager = new ConversationManager(this);
		this.conversationOptionManager = new ConversationOptionManager(this);
		this.actionManager = new ActionManager(this);
	}

	/**
	 * Connects to the database.
	 * 
	 * TODO some backwards compatibility stuff
	 * TODO verify that the model is the same!
	 * 
	 * @param dropTables
	 *            if {@code true}, the database contents will be deleted
	 *            entirely, all tables are dropped and recreated.
	 * @param filename
	 *            the file to connect to.
	 * @throws IOException
	 *             if connecting does not work. Usually because the file is in
	 *             use.
	 */
	public void connect(String filename, boolean dropTables) throws IOException {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connecting to database {0}. Dropping tables: {1}",
				new Object[] { filename, dropTables });

		String ddlGenerationValue = dropTables ? "drop-and-create-tables" : "create-tables";
		// Create objects for database access
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + filename);
		properties.put("eclipselink.ddl-generation", ddlGenerationValue);

		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

		try {
			entityManager = entityManagerFactory.createEntityManager();
		} catch (PersistenceException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not connect to database.", e);
			throw new IOException(e);
		}

		criteriaBuilder = entityManager.getCriteriaBuilder();
	}

	/**
	 * Disconnects from the database.
	 */
	public void disconnect() {
		if (entityManagerFactory != null) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Disconnecting from database");

			// Reset the game manager, so that it will load the new game after
			// connecting to a new DB
			this.gameManager.reset();

			// Close everything
			if (entityManager != null) {
				entityManager.close();
			}
			entityManagerFactory.close();
		}
	}

	/**
	 * Updates any changes. Should be called after each change of persisted
	 * data.
	 */
	public void updateChanges() {
		if (entityManager != null && entityManager.isOpen()) {
			entityManager.getTransaction().begin();
			entityManager.getTransaction().commit();
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Trying to update changes on a closed entity manager.");
		}
	}

	/**
	 * @return the criteriaBuilder
	 */
	public CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}

	/**
	 * @return the entityManager. Only classes in the persistence package have
	 *         access.
	 */
	EntityManager getEntityManager() throws DBClosedException {
		if(entityManager.isOpen()) {
		return entityManager;
		} else {
			throw new DBClosedException();
		}
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
	 * @return the locationManager
	 */
	public LocationManager getLocationManager() {
		return locationManager;
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

	/**
	 * @return the conversationManager
	 */
	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	/**
	 * @return the conversationOptionManager
	 */
	public ConversationOptionManager getConversationOptionManager() {
		return conversationOptionManager;
	}

	/**
	 * @return the actionManager
	 */
	public ActionManager getActionManager() {
		return actionManager;
	}

}
