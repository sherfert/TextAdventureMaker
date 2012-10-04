package persistence;

import java.util.List;

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
	 * TODO no RuntimeExp
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
			throw new RuntimeException("There are " + resultListGame.size()
					+ " games.");
		}

		return resultListGame.get(0);
	}
}