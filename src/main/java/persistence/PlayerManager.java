package persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Player;

/**
 * Managing access to the player in a database.
 * 
 * @author Satia
 */
public class PlayerManager {
	/**
	 * TODO no RuntimeExp
	 * 
	 * @return the player
	 */
	public static Player getPlayer() {

		// Find all players (hopefully only one)
		CriteriaQuery<Player> criteriaQueryPlayer = Main.getCriteriaBuilder()
				.createQuery(Player.class);
		Root<Player> playerRoot = criteriaQueryPlayer.from(Player.class);
		criteriaQueryPlayer.select(playerRoot);
		List<Player> resultListPlayer = Main.getEntityManager()
				.createQuery(criteriaQueryPlayer).getResultList();

		// There should be exactly 1 player
		if (resultListPlayer.size() != 1) {
			throw new RuntimeException("There are " + resultListPlayer.size()
					+ " players.");
		}

		return resultListPlayer.get(0);
	}
}