package playing;

import java.util.List;

import persistence.ItemManager;
import persistence.PlayerManager;
import persistence.WayManager;
import playing.parser.GeneralParser;
import data.Game;
import data.InventoryItem;
import data.Player;
import data.interfaces.HasLocation;
import data.interfaces.Inspectable;
import data.interfaces.Takeable;
import data.interfaces.Travelable;
import data.interfaces.Usable;
import data.interfaces.UsableOrPassivelyUsable;
import data.interfaces.UsableWithHasLocation;

/**
 * Any methods for actually playing a game.
 * 
 * @author Satia
 */
public class GamePlayer {

	/**
	 * A placeholder replacer for the currently used command.
	 */
	private PlaceholderReplacer currentReplacer;

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
		this.currentReplacer = new PlaceholderReplacer();
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
	 * @param identifier
	 *            an identifier of the object
	 */
	public void inspect(String identifier) {
		// TODO general "around" for the current location
		Inspectable object = PlayerManager.getInspectable(player, identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (object != null) {
			// Save name
			currentReplacer.setName(object.getName());

			String message = object.getInspectionText();
			if (message == null) {
				message = game.getInspectionDefaultText();
			}
			io.println(currentReplacer.replacePlaceholders(message));
			// Effect depends on additional actions
			object.inspect();
		} else {
			// There is no such thing
			String message = game.getNoSuchItemText();
			io.println(currentReplacer.replacePlaceholders(message));
		}
	}

	/**
	 * Displays the inventory's content to the player.
	 */
	public void inventory() {
		List<InventoryItem> inventory = player.getInventory();
		if (inventory.isEmpty()) {
			// The inventory is empty
			io.println(currentReplacer.replacePlaceholders(game
					.getInventoryEmptyText()));
		} else {
			// The inventory is not empty
			io.println(currentReplacer.replacePlaceholders(game
					.getInventoryText()));
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
	 * @param identifier
	 *            an identifier of the object
	 */
	public void move(String identifier) {
		Travelable way = WayManager.getWayOutFromLocation(player.getLocation(),
				identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (way != null) {
			// Save name
			currentReplacer.setName(way.getName());
			if (way.isMovingEnabled()) {
				// The location changed
				String message = way.getMoveSuccessfulText();
				if (message != null) {
					io.println(currentReplacer.replacePlaceholders(message));
				}
				io.println(way.getDestination().getEnteredText());
			} else {
				// The location did not change
				String message = way.getMoveForbiddenText();
				if (message == null) {
					message = game.getNotTravelableText();
				}
				io.println(currentReplacer.replacePlaceholders(message));
			}
			// Effect depends on enabled status and additional actions
			way.travel();
		} else {
			// There is no such way
			String message = game.getNoSuchWayText();
			io.println(currentReplacer.replacePlaceholders(message));
		}
	}

	/**
	 * Displays an error message to the player if his input was not
	 * recognizeable.
	 */
	public void noCommand() {
		io.println(currentReplacer.replacePlaceholders(game.getNoCommandText()));
	}

	/**
	 * Sets the input for the current replacer. The other fields will be reset.
	 * 
	 * @param input
	 *            the typed input
	 */
	public void setInput(String input) {
		currentReplacer.reset();
		currentReplacer.setInput(input);
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
	 * @param identifier
	 *            an identifier of the object
	 */
	public void take(String identifier) {
		Takeable item = ItemManager.getItemFromLocation(player.getLocation(),
				identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (item != null) {
			// Save name
			currentReplacer.setName(item.getName());
			if (item.isTakingEnabled()) {
				// The item was taken
				String message = item.getTakeSuccessfulText();
				if (message == null) {
					message = game.getTakenText();
				}
				io.println(currentReplacer.replacePlaceholders(message));
			} else {
				// The item was not taken
				String message = item.getTakeForbiddenText();
				if (message == null) {
					message = game.getNotTakeableText();
				}
				io.println(currentReplacer.replacePlaceholders(message));
			}
			// Effect depends on enabled status and additional actions
			item.take();
		} else {
			// There is no such item
			String message = game.getNoSuchItemText();
			io.println(currentReplacer.replacePlaceholders(message));
		}
	}

	/**
	 * Tries to use the object with the given name. The additional actions will
	 * be performed. A message informing about success/failure will be
	 * displayed.
	 * 
	 * @param identifier
	 *            an identifier of the object
	 */
	public void use(String identifier) {
		Usable object = PlayerManager.getUsable(player, identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (object != null) {
			// Save name
			currentReplacer.setName(object.getName());
			if (object.isUsingEnabled()) {
				// The object was used
				String message = object.getUseSuccessfulText();
				if (message == null) {
					message = game.getUsedText();
				}
				io.println(currentReplacer.replacePlaceholders(message));
			} else {
				// The object was not used
				String message = object.getUseForbiddenText();
				if (message == null) {
					message = game.getNotUsableText();
				}
				io.println(currentReplacer.replacePlaceholders(message));
			}
			// Effect depends on additional actions
			object.use();
		} else {
			// There is no such object and you have no such object
			String message = game.getNoSuchItemText() + " "
					+ game.getNoSuchInventoryItemText();
			io.println(currentReplacer.replacePlaceholders(message));
		}
	}

	/**
	 * Tries to use/combine the objects with the given names. The (additional)
	 * actions will be performed. A message informing about success/failure will
	 * be displayed.
	 * 
	 * @param identifier1
	 *            an identifier of the first object
	 * @param identifier2
	 *            an identifier of the second object
	 */
	public void useWithOrCombine(String identifier1, String identifier2) {
		// FIXME modify:
		// Item -> HasLocation

		UsableOrPassivelyUsable object1 = PlayerManager
				.getUsableOrPassivelyUsable(player, identifier1);
		UsableOrPassivelyUsable object2 = PlayerManager
				.getUsableOrPassivelyUsable(player, identifier2);
		// Save identifiers
		currentReplacer.setIdentifier(identifier1);
		currentReplacer.setIdentifier2(identifier2);

		// Check types of both objects (which can be null)
		if (object1 instanceof InventoryItem) {
			if (object2 instanceof InventoryItem) {
				// Combine
				combine((InventoryItem) object1, (InventoryItem) object2);
			} else if (object2 instanceof HasLocation) {
				// UseWith
				useWith((InventoryItem) object1, (HasLocation) object2);
			} else {
				// Error: Object2 neither in inventory nor in location
				String message = PlaceholderReplacer
						.convertFirstToSecondPlaceholders(game
								.getNoSuchItemText())
						+ " "
						+ PlaceholderReplacer
								.convertFirstToSecondPlaceholders(game
										.getNoSuchInventoryItemText());
				io.println(currentReplacer.replacePlaceholders(message));
			}
		} else if (object1 instanceof HasLocation) {
			if (object2 instanceof InventoryItem) {
				// UseWith
				useWith((InventoryItem) object2, (HasLocation) object1);
			} else {
				// Error: Neither Object1 nor Object2 in inventory
				String message = game.getNoSuchInventoryItemText()
						+ " "
						+ PlaceholderReplacer
								.convertFirstToSecondPlaceholders(game
										.getNoSuchInventoryItemText());
				io.println(currentReplacer.replacePlaceholders(message));
			}
		} else {
			if (object2 instanceof InventoryItem) {
				// Error: Object1 neither in inventory nor in location
				String message = game.getNoSuchItemText() + " "
						+ game.getNoSuchInventoryItemText();
				io.println(currentReplacer.replacePlaceholders(message));
			} else {
				// Error: Neither Object1 nor Object2 in inventory
				String message = game.getNoSuchInventoryItemText()
						+ " "
						+ PlaceholderReplacer
								.convertFirstToSecondPlaceholders(game
										.getNoSuchInventoryItemText());
				io.println(currentReplacer.replacePlaceholders(message));
			}
		}
	}

	/**
	 * Combines two {@link InventoryItem}s.
	 * 
	 * @param item1
	 *            the first item
	 * @param item2
	 *            the second item
	 */
	private void combine(InventoryItem item1, InventoryItem item2) {
		// Save names
		currentReplacer.setName(item1.getName());
		currentReplacer.setName2(item2.getName());

		if (item1.isCombiningEnabledWith(item2)) {
			// Combining was successful
			String message = item1.getCombineWithSuccessfulText(item2);
			if (message == null) {
				message = game.getUsedWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message));
		} else {
			// Combining was not successful
			String message = item1.getCombineWithForbiddenText(item2);
			if (message == null) {
				message = game.getNotUsableWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message));
		}
		// Effect depends on enabled status and additional actions
		item1.combineWith(item2);
	}

	/**
	 * Uses an {@link UsableWithHasLocation} with an {@link HasLocation}.
	 * 
	 * @param usable
	 *            the {@link UsableWithHasLocation}
	 * @param object
	 *            the object
	 */
	private void useWith(UsableWithHasLocation usable, HasLocation object) {
		// Save names
		currentReplacer.setName(usable.getName());
		currentReplacer.setName2(object.getName());

		if (usable.isUsingEnabledWith(object)) {
			// Using was successful
			String message = usable.getUseWithSuccessfulText(object);
			if (message == null) {
				message = game.getUsedWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message));
		} else {
			// Using was not successful
			String message = usable.getUseWithForbiddenText(object);
			if (message == null) {
				message = game.getNotUsableWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message));
		}
		// Effect depends on additional actions
		usable.useWith(object);
	}
}