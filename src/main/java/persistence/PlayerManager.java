package persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Player;
import data.UsableObject;
import data.interfaces.Inspectable;
import data.interfaces.Usable;
import data.interfaces.UsableOrPassivelyUsable;

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

	/**
	 * Gets the inspectable object in the location or in the inventory the given
	 * identifier or {@code null} , if there is none.
	 * 
	 * @param player
	 *            the player
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Inspectable getInspectable(Player player, String identifier) {
		List<Inspectable> inspectables = new ArrayList<Inspectable>();
		// Anything in the room
		inspectables.addAll(player.getLocation().getInspectables());
		// Anything in the inventory
		inspectables.addAll(player.getInventory());

		return NamedObjectManager.getIdentifiableWithIdentifier(inspectables,
				identifier);
	}

	/**
	 * Gets the usable object in the location or in the inventory with the given
	 * identifier or {@code null} , if there is none.
	 * 
	 * @param player
	 *            the player
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Usable getUsable(Player player, String identifier) {
		List<UsableObject> usables = new ArrayList<UsableObject>();
		// Items in the room
		usables.addAll(player.getLocation().getItems());
		// Anything in the inventory
		usables.addAll(player.getInventory());

		return NamedObjectManager.getIdentifiableWithIdentifier(usables,
				identifier);
	}

	/**
	 * Gets the {@link UsableOrPassivelyUsable} in the location or in the
	 * inventory with the given identifier or {@code null} , if there is none.
	 * 
	 * @param player
	 *            the player
	 * @param identifier
	 *            an identifier of the object
	 * @return the corresponding object or {@code null}.
	 */
	public static UsableOrPassivelyUsable getUsableOrPassivelyUsable(Player player,
			String identifier) {
		List<UsableOrPassivelyUsable> usables = new ArrayList<UsableOrPassivelyUsable>();
		// Items ans Persons in the room
		usables.addAll(player.getLocation().getHasLocations());
		// Anything in the inventory
		usables.addAll(player.getInventory());

		return NamedObjectManager.getIdentifiableWithIdentifier(usables,
				identifier);
	}
}