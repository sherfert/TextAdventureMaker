package persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

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
		persistenceManager.updateChanges();
		
		CriteriaQuery<Location> query = persistenceManager.getCriteriaBuilder().createQuery(Location.class);
		query.from(Location.class);
		List<Location> resultList = persistenceManager.getEntityManager().createQuery(query).getResultList();
		// Lists with cascade definitions are not refreshed automatically
		for (Location l : resultList) {
			persistenceManager.getEntityManager().refresh(l);
		}
		return resultList;
	}

}