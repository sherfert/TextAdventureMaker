package playing.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.InventoryItem;
import playing.GamePlayer;

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
		super(gamePlayer, 0);
	}
	
	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getInventoryHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getInventoryCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands for commands with no parameters
		return new HashSet<>();
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new InventoryExecution(input);
	}
	/**
	 * Execution of the inventory command.
	 * 
	 * @author Satia
	 */
	private class InventoryExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public InventoryExecution(String input) {
			super(Inventory.this, input);
		}

		@Override
		public boolean hasObjects() {
			return true;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();

			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Displaying inventory");

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

}
