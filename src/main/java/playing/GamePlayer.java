package playing;

import java.util.List;

import persistence.ItemManager;
import persistence.PlayerManager;
import persistence.WayManager;
import playing.parser.GeneralParser;
import data.Game;
import data.InventoryItem;
import data.Item;
import data.Player;
import data.Way;
import data.action.TakeAction;
import data.interfaces.Inspectable;

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
		// TODO generel "around" for the current location
		Inspectable object = PlayerManager.getInspectable(player, name);

		if (object != null) {
			// Effect depends on additional actions
			object.inspect();
			io.println(object.getLongDescription());

		} else {
			// There is no such thing
			io.println(game.getNoSuchItemText().replaceAll(
					"<identifier>", name));
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
			for (InventoryItem item : inventory) {
				io.println(item.getName());
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
		Way way = WayManager
				.getWayOutFromLocation(player.getLocation(), target);

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
						.replaceAll("<identifier>", way.getName()));
			}
		} else {
			// There is no such way
			io.println(game.getNoSuchWayText().replaceAll(
					"<identifier>", target));
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
	 * Tries to take the object with the given name. The connected
	 * {@link TakeAction} will be performed if the item is takeable. If not, a
	 * meaningful message will be displayed.
	 * 
	 * @param object
	 *            the object's name
	 */
	public void take(String object) {
		Item item = ItemManager.getItemFromLocation(player.getLocation(),
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
			io.println(game.getNoSuchItemText().replaceAll(
					"<identifier>", object));
		}
	}
}