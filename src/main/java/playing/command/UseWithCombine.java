/**
 * 
 */
package playing.command;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.InventoryItem;
import data.interfaces.Combinable;
import data.interfaces.HasLocation;
import data.interfaces.Inspectable;
import data.interfaces.UsableWithHasLocation;
import persistence.InspectableObjectManager;
import persistence.InventoryItemManager;
import playing.GamePlayer;
import playing.PlaceholderReplacer;
import playing.parser.Parameter;
import playing.parser.PatternGenerator;

/**
 * The command to use one object with something or to combine two objects.
 * 
 * @author Satia
 */
public class UseWithCombine extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public UseWithCombine(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// TODO union with the useWith commands
		return InventoryItemManager.getAllAdditionaTakeCommands();
	}

	@Override
	public void execute(boolean originalCommand, Parameter... parameters) {
		if (parameters.length != 2) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of parameters");
			return;
		}
		useWithOrCombine(originalCommand, parameters[0], parameters[1]);
	}

	/**
	 * Tries to use/combine the objects with the given names. The (additional)
	 * actions will be performed. A message informing about success/failure will
	 * be displayed.
	 * 
	 * o0 has to denote a Combinable or UsableWithHasLocation, o1 can be that or
	 * a HasLocation.
	 * 
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param parameter1
	 *            the first parameter.
	 * @param parameter2
	 *            the second parameter.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void useWithOrCombine(boolean originalCommand,
			Parameter parameter1, Parameter parameter2) {
		String identifier1 = parameter1.getIdentifier();
		String identifier2 = parameter2.getIdentifier();

		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Usewith/combine identifiers {0} / {1}",
				new Object[] { identifier1, identifier2 });

		Game game = gamePlayer.getGame();

		Inspectable object1 = InspectableObjectManager
				.getInspectable(identifier1);
		Inspectable object2 = InspectableObjectManager
				.getInspectable(identifier2);
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
				combine(originalCommand, (Combinable) object1, (Combinable) object2, game);
			} else if (object1 instanceof UsableWithHasLocation
					&& object2 instanceof HasLocation) {
				// UseWith
				useWith((UsableWithHasLocation) object1, (HasLocation) object2,
						game);
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

					// Error: Object1 not in inventory
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
	 * @param originalCommand
	 *            if the command was original (or else additional).
	 * @param item1
	 *            the first item
	 * @param item2
	 *            the second item
	 * @param game
	 *            the game
	 */
	private <E> void combine(boolean originalCommand, Combinable<E> item1,
			Combinable<E> item2, Game game) {
		if (!originalCommand) {
			// Check if the additional command belongs the the chosen
			// item1 with item2 (not vice versa!)
			if (!PatternGenerator.getPattern(item1.getAdditionalCombineCommands(item2))
					.matcher(currentReplacer.getInput()).matches()) {
				// no match
				String message = game.getNotUsableWithText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
				return;
			}
		}

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
	 * @param game
	 *            the game
	 */
	private void useWith(UsableWithHasLocation usable, HasLocation object,
			Game game) {
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
}
