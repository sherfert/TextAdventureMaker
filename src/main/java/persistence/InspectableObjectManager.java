package persistence;

import java.util.ArrayList;
import java.util.List;

import data.Player;
import data.interfaces.Inspectable;

/**
 * Managing access to the inspectable objects in the database.
 * 
 * @author Satia
 */
public class InspectableObjectManager {

	/**
	 * Gets the Inspectable object in the location of the player or in the
	 * inventory the given identifier or {@code null} , if there is none.
	 * 
	 * @param identifier
	 *            an identifier of the item
	 * 
	 * @return the corresponding item or {@code null}.
	 */
	public static Inspectable getInspectable(String identifier) {
		Player player = PlayerManager.getPlayer();

		List<Inspectable> inspectables = new ArrayList<>();
		// Anything in the room
		inspectables.addAll(player.getLocation().getInspectables());
		// Anything in the inventory
		inspectables.addAll(player.getInventory());

		return IdentifiableObjectManager.getIdentifiableWithIdentifier(
				inspectables, identifier);
	}

}
