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
	 * 
	 * @return the game
	 */
	public static Game getGame() {
		// Find all games (hopefully only one)
		CriteriaQuery<Game> criteriaQueryGame = Main.getCriteriaBuilder()
				.createQuery(Game.class);
		Root<Game> gameRoot = criteriaQueryGame.from(Game.class);
		criteriaQueryGame.select(gameRoot);
		List<Game> resultListGame = Main.getEntityManager()
				.createQuery(criteriaQueryGame).getResultList();

		// There should be exactly 1 game
		if (resultListGame.size() != 1) {
			Logger.getLogger(GameManager.class.getName())
			.log(Level.SEVERE,
					"There are " + resultListGame.size()
					+ " games.");
		}

		return resultListGame.get(0);
	}
}