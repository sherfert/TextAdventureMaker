package persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Game;
import data.Player;
import data.UsableObject;
import data.interfaces.Inspectable;
import data.interfaces.Usable;
import data.interfaces.UsableOrPassivelyUsable;

/**
 * Managing access to the player in a database.
 * 
 * TODO Player as a parameter here unnecessary TODO methods should be in other
 * classes!?
 * 
 * @author Satia
 */
public class PlayerManager {
	/**
	 * 
	 * @return the player
	 */
	public static Player getPlayer() {
		// Return the game's player
		return GameManager.getGame().getPlayer();
	}

	/**
	 * Gets the Inspectable object in the location or in the inventory the given
	 * identifier or {@code null} , if there is none.
	 * 
	 * @param player
	 *            the player
	 * @param identifier
	 *            an identifier of the item
	 * @return the corresponding item or {@code null}.
	 */
	public static Inspectable getInspectable(Player player, String identifier) {
		List<Inspectable> inspectables = new ArrayList<>();
		// Anything in the room
		inspectables.addAll(player.getLocation().getInspectables());
		// Anything in the inventory
		inspectables.addAll(player.getInventory());

		return InspectableObjectManager.getIdentifiableWithIdentifier(
				inspectables, identifier);
	}

	// XXX getUsable, getUsableOr... and ItemManager.getItemFromLocation,
	// PersonManager.getPersonFromLocation, ...?
	// probably not used any more?
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
		List<Usable> usables = getAllUsables(player);
		return InspectableObjectManager.getIdentifiableWithIdentifier(usables,
				identifier);
	}

	/**
	 * @param player
	 *            the player
	 * @return a list of all usable objects in the location the player is or in
	 *         the inventory.
	 */
	public static List<Usable> getAllUsables(Player player) {
		List<Usable> usables = new ArrayList<>();
		// Items in the room
		usables.addAll(player.getLocation().getItems());
		// Anything in the inventory
		usables.addAll(player.getInventory());
		return usables;
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
	public static UsableOrPassivelyUsable getUsableOrPassivelyUsable(
			Player player, String identifier) {
		List<UsableOrPassivelyUsable> usables = new ArrayList<>();
		// Items and Persons in the room
		usables.addAll(player.getLocation().getHasLocations());
		// Anything in the inventory
		usables.addAll(player.getInventory());

		return InspectableObjectManager.getIdentifiableWithIdentifier(usables,
				identifier);
	}

	// FIXME move into other class
	public static Set<String> getAllAdditionalUseCommands() {

		List<String> resultList = PersistenceManager
				.getEntityManager()
				.createNativeQuery(
						"SELECT DISTINCT c.ADDITIONALUSECOMMANDS FROM UsableObject_ADDITIONALUSECOMMANDS c").getResultList();
		
		return new HashSet<>(resultList);
	}
}