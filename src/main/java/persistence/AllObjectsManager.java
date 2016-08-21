package persistence;

import data.Game;
import data.Player;
import data.interfaces.HasId;
import exception.DBClosedException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managing access to all objects in the database.
 *
 * @author Satia
 */
public class AllObjectsManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public AllObjectsManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * Obtain any object in the DB by ID.
	 * 
	 * @param clazz
	 *            the expected class of the object
	 * @param id
	 *            the id
	 * @return the object, or null, if not found or unexpected type
	 * @throws DBClosedException
	 */
	public <E extends HasId> E getObject(Class<E> clazz, int id) throws DBClosedException {
		return persistenceManager.getEntityManager().find(clazz, id);
	}

	/**
	 * Removes an object from the database.
	 *
	 * @param object
	 *            the object
	 * @throws DBClosedException
	 */
	public void removeObject(HasId object) throws DBClosedException {

		// It is not permitted to delete Game or Player objects
		if (object instanceof Player || object instanceof Game) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"It is not permitted to delete an object of type {0}", object.getClass().getName());
			return;
		}

		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				"Deleting object with ID {0} type {1} from database",
				new Object[] { object.getId(), object.getClass().getName() });
		persistenceManager.getEntityManager().remove(object);
	}

	/**
	 * Adds an object to the database.
	 * 
	 * @param object
	 *            the object
	 * @throws DBClosedException
	 */
	public void addObject(HasId object) throws DBClosedException {
		// It is not permitted to add Game or Player objects
		if (object instanceof Player || object instanceof Game) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"It is not permitted to add an object of type {0}", object.getClass().getName());
			return;
		}

		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding object of type {0} to database",
				new Object[] { object.getClass().getName() });
		persistenceManager.getEntityManager().persist(object);
	}
}
