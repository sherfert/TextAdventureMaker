package playing;

import java.util.List;

import persistence.ItemManager;
import persistence.PlayerManager;
import persistence.WayManager;
import playing.parser.GeneralParser;
import data.Game;
import data.InventoryItem;
import data.Player;
import data.interfaces.Inspectable;
import data.interfaces.Takeable;
import data.interfaces.Travelable;
import data.interfaces.Usable;

/**
 * Any methods for actually playing a game.
 * 
 * @author Satia
 */
public class GamePlayer {

	/**
	 * The game that is being played.
	 */
	private Game game;

	/**
	 * The player object.
	 */
	private Player player;

	/**
	 * The IO object.
	 */
	private InputOutput io;

	/**
	 * The parser
	 */
	private GeneralParser parser;

	/**
	 * @param game
	 *            the game
	 * @param player
	 *            the player
	 */
	public GamePlayer(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.io = new InputOutput(this);
		this.parser = new GeneralParser(this);
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the io
	 */
	public InputOutput getIo() {
		return io;
	}

	/**
	 * @return the parser
	 */
	public GeneralParser getParser() {
		return parser;
	}

	/**
	 * Tries to inspect an object with the given name. The player will look at
	 * it if possible and if not, a meaningful message will be displayed.
	 * 
	 * @param name
	 *            the object's name
	 */
	public void inspect(String name) {
		// TODO general "around" for the current location
		Inspectable object = PlayerManager.getInspectable(player, name);

		if (object != null) {
			// Effect depends on additional actions
			object.inspect();
			io.println(object.getInspectionText() != null ? object
					.getInspectionText() : game.getInspectionDefaultText()
					.replaceAll("<identifier>", name));
		} else {
			// There is no such thing
			io.println(game.getNoSuchItemText()
					.replaceAll("<identifier>", name));
		}
	}

	/**
	 * Displays the inventory's content to the player.
	 */
	public void inventory() {
		List<InventoryItem> inventory = player.getInventory();
		if (inventory.isEmpty()) {
			// The inventory is empty
			io.println(game.getInventoryEmptyText());
		} else {
			// The inventory is not empty
			io.println(game.getInventoryText());
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
						item.getName(), item.getDescription()));
			}
		}
	}

	/**
	 * Tries to move to the target with the given name. The player will move
	 * there if possible and if not, a meaningful message will be displayed.
	 * 
	 * @param target
	 *            the target's name
	 */
	public void move(String target) {
		Travelable way = WayManager.getWayOutFromLocation(player.getLocation(),
				target);

		if (way != null) {
			// Effect depends on enabled status and additional actions
			way.travel();
			if (way.isMovingEnabled()) {
				// The location changed
				if (way.getMoveSuccessfulText() != null) {
					io.println(way.getMoveSuccessfulText());
				}
				io.println(way.getDestination().getEnteredText());
			} else {
				// The location did not change
				io.println(way.getMoveForbiddenText() != null ? way
						.getMoveForbiddenText() : game.getNotTravelableText()
						.replaceAll("<identifier>", target));
			}
		} else {
			// There is no such way
			io.println(game.getNoSuchWayText().replaceAll("<identifier>",
					target));
		}
	}

	/**
	 * Displays an error message to the player if his input was not
	 * recognizeable.
	 */
	public void noCommand() {
		io.println(game.getNoCommandText());
	}

	/**
	 * Starts playing the game.
	 */
	public void start() {
		io.println(game.getStartText());
		io.println(game.getStartLocation().getEnteredText());

		io.startListeningForInput();
	}

	/**
	 * Tries to take the object with the given name. The connected actions will
	 * be performed if the item is takeable (additional actions will be
	 * performed even if not). If not, a meaningful message will be displayed.
	 * 
	 * @param object
	 *            the object's name
	 */
	public void take(String object) {
		Takeable item = ItemManager.getItemFromLocation(player.getLocation(),
				object);

		if (item != null) {
			// Effect depends on enabled status and additional actions
			item.take();
			if (item.isTakingEnabled()) {
				// FIXME replacement rules
				// The item was taken
				io.println(item.getTakeSuccessfulText() != null ? item
						.getTakeSuccessfulText() : game.getTakenText()
						.replaceAll("<identifier>", object));
			} else {
				// The item was not taken
				io.println(item.getTakeForbiddenText() != null ? item
						.getTakeForbiddenText() : game.getNotTakeableText()
						.replaceAll("<identifier>", object));
			}
		} else {
			// There is no such item
			io.println(game.getNoSuchItemText().replaceAll("<identifier>",
					object));
		}
	}

	/**
	 * Tries to use the object with the given name. The additional actions will
	 * be performed. A message informing about success/failure will be
	 * displayed.
	 * 
	 * @param name
	 *            the object's name
	 */
	public void use(String name) {
		Usable object = PlayerManager.getUsable(player, name);

		if (object != null) {
			// Effect depends on additional actions
			object.use();
			if (object.isUsingEnabled()) {
				// The object was used
				io.println(object.getUseSuccessfulText() != null ? object
						.getUseSuccessfulText() : game.getUsedText()
						.replaceAll("<identifier>", name));
			} else {
				// The object was not used
				io.println(object.getUseForbiddenText() != null ? object
						.getUseForbiddenText() : game.getNotUsableText()
						.replaceAll("<identifier>", name));
			}
		} else {
			// There is no such object
			io.println(game.getNoSuchItemText()
					.replaceAll("<identifier>", name));
		}
	}
}