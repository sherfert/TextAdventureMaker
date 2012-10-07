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
import data.interfaces.Inspectable;
import data.interfaces.Takeable;
import data.interfaces.Travelable;
import data.interfaces.Usable;
import data.interfaces.UsableWithItem;

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
	 * The IO object.
	 */
	private InputOutput io;

	/**
	 * The parser
	 */
	private GeneralParser parser;

	/**
	 * The player object.
	 */
	private Player player;

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
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
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
			// There is no such object and you have no such object
			io.println(game.getNoSuchItemText()
					.replaceAll("<identifier>", name)
					+ " "
					+ game.getNoSuchInventoryItemText().replaceAll(
							"<identifier>", name));
		}
	}

	/**
	 * Tries to use/combine the objects with the given names. The (additional)
	 * actions will be performed. A message informing about success/failure will
	 * be displayed.
	 * 
	 * @param obj1
	 *            the first object's name
	 * @param obj2
	 *            the second object's name
	 */
	public void useWithOrCombine(String obj1, String obj2) {
		Usable object1 = PlayerManager.getUsable(player, obj1);
		Usable object2 = PlayerManager.getUsable(player, obj2);

		// Check types of both objects (which can be null)
		if (object1 instanceof InventoryItem) {
			if (object2 instanceof InventoryItem) {
				// Combine
				combine((InventoryItem) object1, (InventoryItem) object2);
			} else if (object2 instanceof Item) {
				// UseWith
				useWith((InventoryItem) object1, obj1, (Item) object2, obj2);
			} else {
				// Error: Object2 neither in inventory nor in location
				io.println(game.getNoSuchItemText().replaceAll("<identifier>",
						obj2)
						+ " "
						+ game.getNoSuchInventoryItemText().replaceAll(
								"<identifier>", obj2));
			}
		} else if (object1 instanceof Item) {
			if (object2 instanceof InventoryItem) {
				// UseWith
				useWith((InventoryItem) object2, obj2, (Item) object1, obj1);
			} else {
				// Error: Neither Object1 nor Object2 in inventory
				io.println(game.getNoSuchInventoryItemText().replaceAll(
						"<identifier>", obj1)
						+ " "
						+ game.getNoSuchInventoryItemText().replaceAll(
								"<identifier>", obj2));
			}
		} else {
			if (object2 instanceof InventoryItem) {
				// Error: Object1 neither in inventory nor in location
				io.println(game.getNoSuchItemText().replaceAll("<identifier>",
						obj1)
						+ " "
						+ game.getNoSuchInventoryItemText().replaceAll(
								"<identifier>", obj1));
			} else {
				// Error: Neither Object1 nor Object2 in inventory
				io.println(game.getNoSuchInventoryItemText().replaceAll(
						"<identifier>", obj1)
						+ " "
						+ game.getNoSuchInventoryItemText().replaceAll(
								"<identifier>", obj2));
			}
		}
	}

	private void combine(InventoryItem item1, InventoryItem item2) {
		// TODO also use Interface
		io.println("You would now combine " + item1.getName() + " and "
				+ item2.getName() + ".");
	}

	/**
	 * Uses an {@link UsableWithItem} with an {@link Item}.
	 * 
	 * @param usable
	 *            the {@link UsableWithItem}
	 * @param usableIdentifier
	 *            the used identifer for the usable
	 * @param item
	 *            the item
	 * @param itemIdentiferthe
	 *            used identifer for the item
	 */
	private void useWith(UsableWithItem usable, String usableIdentifier,
			Item item, String itemIdentifer) {
		// Effect depends on additional actions
		usable.useWith(item);
		if (usable.isUsingEnabledWith(item)) {
			// Using was successful
			io.println(usable.getUseWithSuccessfulText(item) != null ? usable
					.getUseWithSuccessfulText(item) : game.getUsedWithText()
					.replaceAll("<invIdentifier>", usableIdentifier)
					.replaceAll("<itemIdentifier>", itemIdentifer));
		} else {
			// Using was not successful
			io.println(usable.getUseWithForbiddenText(item) != null ? usable
					.getUseWithForbiddenText(item) : game
					.getNotUsableWithText()
					.replaceAll("<invIdentifier>", usableIdentifier)
					.replaceAll("<itemIdentifier>", itemIdentifer));
		}
	}
}