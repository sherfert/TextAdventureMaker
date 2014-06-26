package persistence;

import java.util.ArrayList;
import java.util.List;

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

		return InspectableObjectManager.getIdentifiableWithIdentifier(inspectables,
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
		List<UsableObject> usables = new ArrayList<>();
		// Items in the room
		usables.addAll(player.getLocation().getItems());
		// Anything in the inventory
		usables.addAll(player.getInventory());

		return InspectableObjectManager.getIdentifiableWithIdentifier(usables,
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
}