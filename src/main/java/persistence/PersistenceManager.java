package persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Model;
import exception.DBClosedException;
import exception.DBIncompatibleException;

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
	 * Version numbers. Adjust in the first commit after a tag with a version
	 * number. These numbers are used to check DB compatibility.
	 */
	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 0;

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
	private VersioningManager versioningManager;

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
		this.versioningManager = new VersioningManager(this);
	}

	/**
	 * Connects to the database.
	 * 
	 * @param dropTables
	 *            if {@code true}, the database contents will be deleted
	 *            entirely, all tables are dropped and recreated.
	 * @param filename
	 *            the file to connect to.
	 * @throws IOException
	 *             if connecting does not work. Usually because the file is in
	 *             use.
	 * @throws DBIncompatibleException
	 *             if the loaded DB is too new.
	 */
	public void connect(String filename, boolean dropTables) throws IOException, DBIncompatibleException {
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

		Model model = null;
		try {
			model = getModel();
		} catch (DBClosedException e) {
			// Should never happen
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "DB closed unexpectedly.", e);
			disconnect();
			throw new IOException(e);
		}
		if (model.getMajorVersion() != MAJOR_VERSION || model.getMinorVersion() != MINOR_VERSION) {
			try {
				versioningManager.updateDB(model.getMajorVersion(), model.getMinorVersion(), MAJOR_VERSION,
						MINOR_VERSION);
			} catch (DBIncompatibleException e) {
				// Disconnect if this happens
				disconnect();
				throw e;
			}
		}
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
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.close();
			}
			if (entityManagerFactory.isOpen()) {
				entityManagerFactory.close();
			}
		}
	}

	/**
	 * Invkoes refresh on the EntityManager with the passes object
	 * 
	 * @param o
	 */
	public void refreshEntity(Object o) {
		updateChanges();		
		entityManager.refresh(o);
	}

	/**
	 * Checks if an object is managed by the persistence context.
	 * 
	 * @param o
	 * @return
	 */
	public boolean isManaged(Object o) {
		try {
			refreshEntity(o);
			return true;
		} catch(EntityNotFoundException e) {
			return false;
		}
	}

	/**
	 * This method obtains the model of the DB. If there is none, one is
	 * created.
	 * 
	 * @return the model of the database
	 * @throws DBClosedException
	 *             if the DB is closed
	 */
	Model getModel() throws DBClosedException {
		CriteriaQuery<Model> criteriaQuery = getCriteriaBuilder().createQuery(Model.class);
		Root<Model> root = criteriaQuery.from(Model.class);
		criteriaQuery.select(root);

		Model model;
		try {
			model = getEntityManager().createQuery(criteriaQuery).getSingleResult();
		} catch (NoResultException e) {
			// Create a new one
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating model entry in empty DB");
			model = new Model();
			model.setMajorVersion(PersistenceManager.MAJOR_VERSION);
			model.setMinorVersion(PersistenceManager.MINOR_VERSION);
			// Save
			getEntityManager().persist(model);
			updateChanges();
		}

		return model;
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
		if (entityManager.isOpen()) {
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
