package playing;

import java.util.List;

import persistence.GameManager;
import persistence.ItemManager;
import persistence.PlayerManager;
import data.Game;
import data.InventoryItem;
import data.Item;
import data.action.TakeAction;

/**
 * Any methods for actually playing a game.
 * 
 * @author Satia
 */
public class GamePlayer {

	/**
	 * Starts playing the game.
	 */
	public static void start() {
		Game game = GameManager.getGame();

		InputOutput.println(game.getStartText());
		// FIXME
		InputOutput.println(game.getStartLocation().getDescription());

		InputOutput.startListeningForInput();
	}

	/**
	 * Tries to move to the target with the given name. The player will move
	 * there if possible and if not, a meaningful message will be displayed.
	 * 
	 * @param target
	 *            the target's name
	 */
	public static void move(String target) {
		// TODO
		InputOutput.println("You would go " + target + " now.");
	}

	/**
	 * Tries to take the object with the given name. The connected
	 * {@link TakeAction} will be performed if the item is takeable. If not, a
	 * meaningful message will be displayed.
	 * 
	 * @param object
	 *            the object's name
	 */
	public static void take(String object) {
		Item item = ItemManager.getItemFromLocation(PlayerManager.getPlayer()
				.getLocation(), object);

		if (item != null) {
			item.triggerActions();
			if (item.isPrimaryActionEnabled()) {
				// FIXME replacement rules
				// The item was taken
				InputOutput.println(item.getSuccessfulText() != null ? item
						.getSuccessfulText() : GameManager.getGame()
						.getTakenText().replaceAll("<item>", item.getName()));
			} else {
				// The item was not taken
				InputOutput.println(item.getForbiddenText() != null ? item
						.getForbiddenText() : GameManager.getGame()
						.getNotTakeableText()
						.replaceAll("<item>", item.getName()));
			}
		} else {
			// There is no such item
			InputOutput.println(GameManager.getGame().getNoSuchItemText()
					.replaceAll("<item>", object.toLowerCase()));
		}
	}

	/**
	 * Displays the inventory's content to the player.
	 */
	public static void inventory() {
		List<InventoryItem> inventory = PlayerManager.getPlayer()
				.getInventory();
		if (inventory.isEmpty()) {
			// The inventory is empty
			InputOutput.println(GameManager.getGame().getInventoryEmptyText());
		} else {
			// The inventory is not empty
			// TODO inv text
			for (InventoryItem item : inventory) {
				InputOutput.println(item.getName());
			}
		}
	}

	/**
	 * Display's an error message to the player if his input was not
	 * recognizeable.
	 */
	public static void noCommand() {
		InputOutput.println(GameManager.getGame().getNoCommandText());
	}
}