package playing;

import java.util.List;

import persistence.GameManager;
import persistence.ItemManager;
import persistence.PlayerManager;
import data.Game;
import data.InventoryItem;
import data.Item;

public class GamePlayer {

	public static void start() {
		Game game = GameManager.getGame();

		InputOutput.println(game.getStartText());
		// FIXME
		InputOutput.println(game.getStartLocation().getDescription());

		InputOutput.startListeningForInput();
	}

	public static void move(String target) {
		// TODO
		InputOutput.println("You would go " + target + " now.");
	}

	public static void take(String object) {
		Item item = ItemManager.getItemFromLocation(PlayerManager.getPlayer()
				.getLocation(), object);

		if (item != null) {
			item.triggerActions();
			if (item.isPrimaryActionEnabled()) {
				// FIXME replacement rules
				// TODO enabled text!
				InputOutput.println(GameManager.getGame().getTakenText()
						.replaceAll("<item>", item.getName()));
			} else {
				InputOutput.println(item.getForbiddenText() != null ? item
						.getForbiddenText() : GameManager.getGame()
						.getNotTakeableText()
						.replaceAll("<item>", item.getName()));
			}
		} else {
			InputOutput.println(GameManager.getGame().getNoSuchItemText()
					.replaceAll("<item>", object.toLowerCase()));
		}
	}

	public static void inventory() {
		List<InventoryItem> inventory = PlayerManager.getPlayer()
				.getInventory();
		if (inventory.isEmpty()) {
			InputOutput.println(GameManager.getGame().getInventoryEmptyText());
		} else {
			for (InventoryItem item : inventory) {
				InputOutput.println(item.getName());
			}
		}
	}

	public static void noCommand() {
		InputOutput.println(GameManager.getGame().getNoCommandText());
	}
}