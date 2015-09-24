package playing.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.InventoryItem;
import playing.GamePlayer;
import playing.parser.Parameter;

/**
 * Command to list the contents of the inventory.
 * 
 * @author Satia
 */
public class Inventory extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public Inventory(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands for commands with no parameters
		return new HashSet<>();
	}

	@Override
	public void execute(boolean originalCommand, Parameter... parameters) {
		if (parameters.length != 0) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of parameters");
			return;
		}
		inventory();
	}
	
	/**
	 * Displays the inventory's content to the player.
	 */
	private void inventory() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Displaying inventory");
		
		Game game = gamePlayer.getGame();

		List<InventoryItem> inventory = game.getPlayer().getInventory();
		if (inventory.isEmpty()) {
			// The inventory is empty
			io.println(currentReplacer.replacePlaceholders(game
					.getInventoryEmptyText()), game.getNeutralBgColor(), game
					.getNeutralFgColor());
		} else {
			// The inventory is not empty
			io.println(currentReplacer.replacePlaceholders(game
					.getInventoryText()), game.getNeutralBgColor(), game
					.getNeutralFgColor());
			// Determine longest name for formatting
			int longest = 0;
			for (InventoryItem item : inventory) {
				if (item.getName().length() > longest) {
					longest = item.getName().length();
				}
			}
			// Print names and descriptions
			for (InventoryItem item : inventory) {
				io.println(String.format("%-" + longest + "s - %s",
						item.getName(), item.getDescription()), game
						.getNeutralBgColor(), game.getNeutralFgColor());
			}
		}
	}

}
