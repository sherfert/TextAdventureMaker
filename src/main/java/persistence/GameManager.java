package persistence;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Game;

/**
 * Managing access to the game in a database.
 *
 * @author Satia
 */
public class GameManager {

	/**
	 * The game. Is retrieved from the DB once and kept in memory until reset is
	 * called.
	 */
	private static Game game;

	/**
	 * Resets the state of the GameManager by deleting its reference to the
	 * Game.
	 */
	public static void reset() {
		game = null;
	}

	/**
	 * Retrieved the game. Uses the im-memory reference if not {@code null} or
	 * retrieves the game from the DB otherwise.
	 * 
	 * @return the game
	 * 
	 * @throws Exception
	 *             (unchecked) if the database is not compatible with the model.
	 */
	public static Game getGame() {
		if(game != null) {
			return game;
		}
		
		// Find all games (hopefully only one)
		CriteriaQuery<Game> criteriaQueryGame = PersistenceManager
				.getCriteriaBuilder().createQuery(Game.class);
		Root<Game> gameRoot = criteriaQueryGame.from(Game.class);
		criteriaQueryGame.select(gameRoot);
		List<Game> resultListGame = PersistenceManager.getEntityManager()
				.createQuery(criteriaQueryGame).getResultList();

		// There should be exactly 1 game
		if (resultListGame.size() != 1) {
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE,
					"There are {0} games.", resultListGame.size());
		}

		return game = resultListGame.get(0);
	}
}
