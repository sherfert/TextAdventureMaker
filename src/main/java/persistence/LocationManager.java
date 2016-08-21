package persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Location;
import exception.DBClosedException;

/**
 * Managing access to the locations in the database.
 * 
 * @author Satia
 */
public class LocationManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * @param persistenceManager
	 */
	public LocationManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}
	
	/**
	 * @return all locations in the game.
	 * @throws DBClosedException 
	 */
	public List<Location> getAllLocations() throws DBClosedException {
		// Find all locations
		CriteriaQuery<Location> query = persistenceManager
				.getCriteriaBuilder().createQuery(Location.class);
		Root<Location> root = query.from(Location.class);
		query.select(root);
		List<Location> resultList = persistenceManager.getEntityManager()
				.createQuery(query).getResultList();
		return resultList;
	}

	
}