package playing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import persistence.PersistenceManager;
import persistence.PlayerManager;
import persistence.WayManager;
import playing.InputOutput.GeneralIOManager;
import playing.menu.LoadSaveManager;
import playing.parser.GeneralParser;
import playing.parser.GeneralParser.CommandRecExec;
import playing.parser.PatternGenerator;
import data.Game;
import data.InventoryItem;
import data.interfaces.Combinable;
import data.interfaces.HasConversation;
import data.interfaces.HasLocation;
import data.interfaces.Inspectable;
import data.interfaces.Takeable;
import data.interfaces.Travelable;
import data.interfaces.Usable;
import data.interfaces.UsableWithHasLocation;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Any methods for actually playing a game.
 * 
 * TODO one class per command.
 * 
 * @author Satia
 */
public class GamePlayer implements GeneralIOManager {
	/**
	 * A placeholder replacer for the currently used command.
	 */
	private final PlaceholderReplacer currentReplacer;

	/**
	 * The game that is being played.
	 */
	private Game game;

	/**
	 * The IO object.
	 */
	private final InputOutput io;

	/**
	 * The parser
	 */
	private GeneralParser parser;

	/**
	 * Creates a new game player. Can only be used properly after
	 * {@link #setGame(Game)} has been called.
	 */
	public GamePlayer() {
		this.io = new InputOutput(this);
		this.currentReplacer = new PlaceholderReplacer();
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Sets the game and the parser
	 * 
	 * @param game
	 *            the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
		this.parser = new GeneralParser(this);
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
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the object
	 */
	public void inspect(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Inspecting identifier {0}", identifier);

		Inspectable object = PlayerManager.getInspectable(game.getPlayer(),
				identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (object != null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Inspecting id {0}", object.getId());

			// Save name
			currentReplacer.setName(object.getName());

			String message = object.getInspectionText();
			if (message == null) {
				message = game.getInspectionDefaultText();
			}
			io.println(currentReplacer.replacePlaceholders(message),
					game.getNeutralBgColor(), game.getNeutralFgColor());
			// Effect depends on additional actions
			object.inspect();
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Inspect object not found {0}", identifier);

			// There is no such thing
			String message = game.getNoSuchItemText();
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
		}
	}

	/**
	 * Displays the inventory's content to the player.
	 * 
	 * @param originalCommand
	 *            ignored in this method, since it has no parameters
	 */
	public void inventory(boolean originalCommand) {
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

	/**
	 * Displays the location entered text to the player.
	 * 
	 * @param originalCommand
	 *            ignored in this method, since it has no parameters
	 */
	public void lookAround(boolean originalCommand) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Looking around");

		io.println(
				currentReplacer.replacePlaceholders(game.getPlayer()
						.getLocation().getEnteredText()),
				game.getNeutralBgColor(), game.getNeutralFgColor());
	}

	/**
	 * Displays the help text to the player.
	 * 
	 * @param originalCommand
	 *            ignored in this method, since it has no parameters
	 */
	public void help(boolean originalCommand) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Displaying help");

		// Iterate through all commands and all their textual commands
		// Also include exit
		for (CommandRecExec command : parser.getCommandRecExecs()) {
			printCommandHelp(command.getCommandHelpText(),
					command.getTextualCommands());
		}

		printCommandHelp(game.getExitCommandHelpText(), game.getExitCommands());
	}

	/**
	 * Prints the help for a command
	 * 
	 * @param commandHelpText
	 *            the help text specified for that command
	 * @param textualCommands
	 *            all the textual commands triggering that command
	 */
	private void printCommandHelp(String commandHelpText,
			List<String> textualCommands) {
		io.println(commandHelpText, game.getNeutralBgColor(),
				game.getNeutralFgColor());
		for (String textualCommand : textualCommands) {
			io.println(" " + textualCommand, game.getNeutralBgColor(),
					game.getNeutralFgColor());
		}
	}

	/**
	 * Tries to move to the target with the given name. The player will move
	 * there if possible and if not, a meaningful message will be displayed.
	 * 
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the object
	 */
	public void move(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Move identifier {0}", identifier);

		Travelable way = WayManager.getWayOutFromLocation(game.getPlayer()
				.getLocation(), identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (way != null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Move id {0}", way.getId());
			// Save name
			currentReplacer.setName(way.getName());
			if (way.isMovingEnabled()) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
						"Move id {0} enabled", way.getId());

				// The location changed
				String message = way.getMoveSuccessfulText();
				if (message != null) {
					io.println(currentReplacer.replacePlaceholders(message),
							game.getSuccessfullBgColor(),
							game.getSuccessfullFgColor());
				}
				io.println(currentReplacer.replacePlaceholders(way
						.getDestination().getEnteredText()), game
						.getNeutralBgColor(), game.getNeutralFgColor());
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
						"Move id {0} disabled", way.getId());

				// The location did not change
				String message = way.getMoveForbiddenText();
				if (message == null) {
					message = game.getNotTravelableText();
				}
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			}
			// Effect depends on enabled status and additional actions
			way.travel();
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Move way not found {0}", identifier);

			// There is no such way
			String message = game.getNoSuchWayText();
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
		}
	}

	/**
	 * Displays an error message to the player if his input was not
	 * recognizable.
	 */
	public void noCommand() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Command not identifiable");

		io.println(
				currentReplacer.replacePlaceholders(game.getNoCommandText()),
				game.getFailedBgColor(), game.getFailedFgColor());
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
	 * Starts playing the game. Returns immediately.
	 */
	public void start() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				"Starting the game");
		// clear the screen
		io.clear();

		// If the player has no location, this is a new game.
		if (game.getPlayer().getLocation() == null) {
			// Transfer him to the start location and start a new game.
			game.getPlayer().setLocation(game.getStartLocation());
			io.println(game.getStartText(), game.getNeutralBgColor(),
					game.getNeutralFgColor());
		}
		// Continue by printing the locations's text.
		io.println(game.getPlayer().getLocation().getEnteredText(),
				game.getNeutralBgColor(), game.getNeutralFgColor());
	}

	/**
	 * Exits the game.
	 */
	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO,
				"Stopping the game");
		io.exitIO();
		PersistenceManager.disconnect();
		// FIXME at the moment close the vm
		System.exit(0);
	}

	/**
	 * Tries to take the object with the given name. The connected actions will
	 * be performed if the item is takeable (additional actions will be
	 * performed even if not). If not, a meaningful message will be displayed.
	 * 
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the object
	 */
	public void take(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Take identifier {0}", identifier);

		// Collect all objects, whether they are takeable or not.
		Inspectable object = PlayerManager.getInspectable(game.getPlayer(),
				identifier);

		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (object == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Take item not found {0}", identifier);

			// There is no such item
			String message = game.getNoSuchItemText();
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
		} else {
			// Save name
			currentReplacer.setName(object.getName());

			if (object instanceof Takeable) {
				Takeable item = (Takeable) object;

				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Take id {0}", item.getId());

				if (item.isTakingEnabled()) {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Take id {0} enabled", item.getId());

					// The item was taken
					String message = item.getTakeSuccessfulText();
					if (message == null) {
						message = game.getTakenText();
					}
					io.println(currentReplacer.replacePlaceholders(message),
							game.getSuccessfullBgColor(),
							game.getSuccessfullFgColor());
				} else {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Take id {0} disabled", item.getId());

					// The item was not taken
					String message = item.getTakeForbiddenText();
					if (message == null) {
						message = game.getNotTakeableText();
					}
					io.println(currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(), game.getFailedFgColor());
				}
				// Effect depends on enabled status and additional actions
				item.take();
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Take item not of type Takeable {0}", identifier);

				// There is something (e.g. a person), but nothing you could
				// take.
				String message = game.getInvalidCommandText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			}
		}
	}

	/**
	 * Tries to use the object with the given name. The additional actions will
	 * be performed. A message informing about success/failure will be
	 * displayed.
	 * 
	 * @param originalCommand
	 *            if the command was original (or else additional). Used to test
	 *            if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the object
	 */
	public void use(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Use identifier {0}", identifier);

		// Collect all objects, whether they are usable or not.
		Inspectable objectI = PlayerManager.getInspectable(game.getPlayer(),
				identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (objectI == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Use item not found {0}", identifier);
			// There is no such object and you have no such object
			String message = game.getNoSuchItemText() + " "
					+ game.getNoSuchInventoryItemText();
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());

		} else {
			// Save name
			currentReplacer.setName(objectI.getName());

			if (objectI instanceof Usable) {
				Usable object = (Usable) objectI;

				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Use id {0}", object.getId());

				if (!originalCommand) {
					// Check if the additional command belongs the the chosen
					// usable
					if (!PatternGenerator
							.getPattern(object.getAdditionalUseCommands())
							.matcher(currentReplacer.getInput()).matches()) {
						// no match
						String message = game.getNotUsableText();
						io.println(currentReplacer.replacePlaceholders(message),
								game.getFailedBgColor(), game.getFailedFgColor());
						return;
					}
				}

				// Save name
				currentReplacer.setName(object.getName());
				if (object.isUsingEnabled()) {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Use id {0} enabled", object.getId());

					// The object was used
					String message = object.getUseSuccessfulText();
					if (message == null) {
						message = game.getUsedText();
					}
					io.println(currentReplacer.replacePlaceholders(message),
							game.getSuccessfullBgColor(),
							game.getSuccessfullFgColor());
				} else {
					Logger.getLogger(this.getClass().getName())
							.log(Level.FINEST, "Use id {0} disabled",
									object.getId());

					// The object was not used
					String message = object.getUseForbiddenText();
					if (message == null) {
						message = game.getNotUsableText();
					}
					io.println(currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(), game.getFailedFgColor());
				}
				// Effect depends on additional actions
				object.use();
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Use item not of type Useable {0}", identifier);
				// There is something (e.g. a person), but nothing you could
				// use.
				String message = game.getInvalidCommandText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());

			}
		}
	}

	/**
	 * Checks if the game has ended. If so, the main menu is shown, but one can
	 * not continue the game.
	 */
	public void checkGameEnded() {
		if (game.isHasEnded()) {
			LoadSaveManager.showMenu(false);
		}
	}

	/**
	 * Tries to use/combine the objects with the given names. The (additional)
	 * actions will be performed. A message informing about success/failure will
	 * be displayed.
	 * 
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier1
	 *            an identifier of the first object
	 * @param identifier2
	 *            an identifier of the second object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void useWithOrCombine(boolean originalCommand, String identifier1,
			String identifier2) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Usewith/combine identifiers {0} / {1}",
				new Object[] { identifier1, identifier2 });

		Inspectable object1 = PlayerManager.getInspectable(game.getPlayer(),
				identifier1);
		// .getUsableOrPassivelyUsable(game.getPlayer(), identifier1);
		Inspectable object2 = PlayerManager.getInspectable(game.getPlayer(),
				identifier2);
		// .getUsableOrPassivelyUsable(game.getPlayer(), identifier2);
		// Save identifiers
		currentReplacer.setIdentifier(identifier1);
		currentReplacer.setIdentifier2(identifier2);

		if (object1 != null) {
			currentReplacer.setName(object1.getName());
		}
		if (object2 != null) {
			currentReplacer.setName2(object2.getName());
		}

		// Check types of both objects (which can be null)
		if (object1 instanceof UsableWithHasLocation
				|| object1 instanceof Combinable) {
			/*
			 * The classes must be the same, since the generic type information
			 * cannot be inferred at runtime.
			 */
			if (object1 instanceof Combinable && object2 instanceof Combinable
					&& object1.getClass() == object2.getClass()) {
				// The rawtype can be used since we know they're the same type

				// Combine
				combine((Combinable) object1, (Combinable) object2);
			} else if (object1 instanceof UsableWithHasLocation
					&& object2 instanceof HasLocation) {
				// UseWith
				useWith((UsableWithHasLocation) object1, (HasLocation) object2);
			} else if (object2 != null) {
				// Error: Object2 not of correct type
				Logger.getLogger(this.getClass().getName())
						.log(Level.FINER,
								"Usewith/combine object not of type UsableWithHasLocation/Combinable {0}",
								identifier2);

				String message = game.getInvalidCommandText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());

			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Usewith/combine object not found {0}", identifier2);

				// Error: Object2 neither in inventory nor in location
				String message = PlaceholderReplacer
						.convertFirstToSecondPlaceholders(game
								.getNoSuchItemText())
						+ " "
						+ PlaceholderReplacer
								.convertFirstToSecondPlaceholders(game
										.getNoSuchInventoryItemText());
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			}
		} else if (object1 instanceof HasLocation) {
			if (object2 instanceof UsableWithHasLocation) {
				// UseWith
				useWith((UsableWithHasLocation) object2, (HasLocation) object1);
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Usewith/combine objects not found {0} / {1}",
						new Object[] { identifier1, identifier2 });

				// Error: Neither Object1 nor Object2 in inventory
				String message = game.getNoSuchInventoryItemText()
						+ " "
						+ PlaceholderReplacer
								.convertFirstToSecondPlaceholders(game
										.getNoSuchInventoryItemText());
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			}
		} else {
			if (object2 instanceof UsableWithHasLocation) {
				if (object1 != null) {
					// Error: Object1 not of correct type
					Logger.getLogger(this.getClass().getName())
							.log(Level.FINER,
									"Usewith/combine object not of type UsableWithHasLocation/Combinable {0}",
									identifier1);

					String message = game.getInvalidCommandText();
					io.println(currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(), game.getFailedFgColor());
				} else {
					Logger.getLogger(this.getClass().getName())
							.log(Level.FINER,
									"Usewith/combine object not found {0}",
									identifier1);

					// Error: Object1 neither in inventory nor in location
					String message = game.getNoSuchItemText() + " "
							+ game.getNoSuchInventoryItemText();
					io.println(currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(), game.getFailedFgColor());
				}
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Usewith/combine objects not found {0} / {1}",
						new Object[] { identifier1, identifier2 });

				// Error: Neither Object1 nor Object2 in inventory
				String message = game.getNoSuchInventoryItemText()
						+ " "
						+ PlaceholderReplacer
								.convertFirstToSecondPlaceholders(game
										.getNoSuchInventoryItemText());
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
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
	private <E> void combine(Combinable<E> item1, Combinable<E> item2) {
		if (item1.isCombiningEnabledWith(item2)) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
					"Combine enabled id {0} with {1}",
					new Object[] { item1.getId(), item2.getId() });

			// Combining was successful
			String message = item1.getCombineWithSuccessfulText(item2);
			if (message == null) {
				message = game.getUsedWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message),
					game.getSuccessfullBgColor(), game.getSuccessfullFgColor());
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
					"Combine disabled id {0} with {1}",
					new Object[] { item1.getId(), item2.getId() });

			// Combining was not successful
			String message = item1.getCombineWithForbiddenText(item2);
			if (message == null) {
				message = game.getNotUsableWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
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
		if (usable.isUsingEnabledWith(object)) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
					"Uswith enabled id {0} with {1}",
					new Object[] { usable.getId(), object.getId() });

			// Using was successful
			String message = usable.getUseWithSuccessfulText(object);
			if (message == null) {
				message = game.getUsedWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message),
					game.getSuccessfullBgColor(), game.getSuccessfullFgColor());
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
					"Uswith disabled id {0} with {1}",
					new Object[] { usable.getId(), object.getId() });

			// Using was not successful
			String message = usable.getUseWithForbiddenText(object);
			if (message == null) {
				message = game.getNotUsableWithText();
			}
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
		}
		// Effect depends on additional actions
		usable.useWith(object);
	}

	/**
	 * Tries to talk to the person with the given name. The additional actions
	 * will be performed. Either the conversation will be started or a message
	 * informing about failure will be displayed.
	 * 
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the person
	 */
	public void talkTo(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Talk to identifier {0}", identifier);

		Inspectable object = PlayerManager.getInspectable(game.getPlayer(),
				identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (object == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Talk to person not found {0}", identifier);

			// There is no such person
			String message = game.getNoSuchPersonText();
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
		} else {
			// Save name
			currentReplacer.setName(object.getName());

			if (object instanceof HasConversation) {
				HasConversation person = (HasConversation) object;
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Talk to id {0}", person.getId());
				if (person.isTalkingEnabled()) {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Talk to id {0} enabled",
							person.getId());

					// Start the conversation
					new ConversationPlayer(io, game, person.getConversation(),
							person.getName());
				} else {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Talk to {0} disabled",
							person.getId());

					// Talking disabled
					String message = person.getTalkingToForbiddenText();
					if (message == null) {
						message = game.getNotTalkingToEnabledText();
					}
					io.println(currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(), game.getFailedFgColor());
				}
				// Effect depends on additional actions
				person.talkTo();
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Talk to person not of type HasConversation {0}",
						identifier);

				// There is something (e.g. an item), but nothing you could
				// talk to.
				String message = game.getInvalidCommandText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			}
		}
	}

	/**
	 * {@inheritDoc} Parses the input. Stops the game if necessary.
	 */
	@Override
	public void handleText(String text) {
		if (!parser.parse(text)) {
			stop();
		}
	}

	/**
	 * @return all additional use commands from items in this location and in
	 *         the inventory.
	 */
	public Set<String> getAdditionalUseCommands() {
		return PlayerManager.getAllAdditionalUseCommands();
	}

	/**
	 * TODO remove this method
	 */
	public Set<String> getNoAdditionalCommands() {
		return new HashSet<>();
	}
}
