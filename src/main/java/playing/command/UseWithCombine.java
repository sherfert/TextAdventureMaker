package playing.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.InventoryItem;
import data.interfaces.Combinable;
import data.interfaces.PassivelyUsable;
import data.interfaces.UsableWithSomething;
import exception.DBClosedException;
import persistence.InventoryItemManager;
import playing.GamePlayer;
import playing.PlaceholderReplacer;
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
		super(gamePlayer, 2);
	}

	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getUseWithCombineHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getUseWithCombineCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() throws DBClosedException {
		InventoryItemManager iim = persistenceManager.getInventoryItemManager();

		Set<String> result = iim.getAllAdditionaCombineCommands();
		result.addAll(iim.getAllAdditionaUseWithCommands());
		return result;
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new UseWithCombineExecution(input);
	}

	/**
	 * Execution of the use with/combine command.
	 * 
	 * @author Satia
	 */
	private class UseWithCombineExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public UseWithCombineExecution(String input) {
			super(UseWithCombine.this, input);
		}

		@Override
		public boolean hasObjects() {
			findInspectableObjects();
			return object1 != null && object2 != null
					&& ((object1 instanceof Combinable && object2 instanceof Combinable
							&& object1.getClass() == object2.getClass())
							|| (object1 instanceof UsableWithSomething && object2 instanceof PassivelyUsable));
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();
			String identifier1 = parameters[0].getIdentifier();
			String identifier2 = parameters[1].getIdentifier();
			
			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Usewith/combine identifiers {0} / {1}",
					new Object[] { identifier1, identifier2 });
			
			// Check types of both objects (which can be null)
			if (object1 instanceof UsableWithSomething || object1 instanceof Combinable) {
				/*
				 * The classes must be the same, since the generic type information
				 * cannot be inferred at runtime.
				 */
				if (object1 instanceof Combinable && object2 instanceof Combinable
						&& object1.getClass() == object2.getClass()) {
					// The rawtype can be used since we know they're the same type

					// Combine
					combine(originalCommand, (Combinable) object1, (Combinable) object2, game);
				} else if (object1 instanceof UsableWithSomething && object2 instanceof PassivelyUsable) {
					// UseWith
					useWith(originalCommand, (UsableWithSomething) object1, (PassivelyUsable) object2, game);
				} else if (object2 != null) {
					// Error: Object2 not of correct type
					Logger.getLogger(this.getClass().getName()).log(Level.FINER,
							"Usewith/combine object not of type UsableWithSomething/Combinable {0}", identifier2);

					String message = game.getInvalidCommandText();
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());

				} else {
					Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Usewith/combine object not found {0}",
							identifier2);

					// Error: Object2 neither in inventory nor in location
					String message = PlaceholderReplacer.convertFirstToSecondPlaceholders(game.getNoSuchItemText()) + " "
							+ PlaceholderReplacer.convertFirstToSecondPlaceholders(game.getNoSuchInventoryItemText());
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());
				}
			} else {
				if (object2 instanceof UsableWithSomething) {
					if (object1 != null) {
						// Error: Object1 not of correct type
						Logger.getLogger(this.getClass().getName()).log(Level.FINER,
								"Usewith/combine object not of type UsableWithSomething/Combinable {0}", identifier1);

						String message = game.getInvalidCommandText();
						io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
								game.getFailedFgColor());
					} else {
						Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Usewith/combine object not found {0}",
								identifier1);

						// Error: Object1 not in inventory
						String message = game.getNoSuchItemText() + " " + game.getNoSuchInventoryItemText();
						io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
								game.getFailedFgColor());
					}
				} else {
					Logger.getLogger(this.getClass().getName()).log(Level.FINER,
							"Usewith/combine objects not found {0} / {1}", new Object[] { identifier1, identifier2 });

					// Error: Neither Object1 nor Object2 in inventory
					String message = game.getNoSuchInventoryItemText() + " "
							+ PlaceholderReplacer.convertFirstToSecondPlaceholders(game.getNoSuchInventoryItemText());
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());
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
		private <E> void combine(boolean originalCommand, Combinable<E> item1, Combinable<E> item2, Game game) {
			if (!originalCommand) {
				// Check if the additional command belongs the the chosen
				// item1 with item2 (not vice versa!)
				if (!PatternGenerator.getPattern(item1.getAdditionalCombineCommands(item2))
						.matcher(currentReplacer.getInput()).matches()) {
					// no match
					String message = game.getNotUsableWithText();
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());
					return;
				}
			}

			if (item1.isCombiningEnabledWith(item2)) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Combine enabled id {0} with {1}",
						new Object[] { item1.getId(), item2.getId() });

				// Combining was successful
				String message = item1.getCombineWithSuccessfulText(item2);
				if (message == null) {
					message = game.getUsedWithText();
				}
				io.println(currentReplacer.replacePlaceholders(message), game.getSuccessfullBgColor(),
						game.getSuccessfullFgColor());
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Combine disabled id {0} with {1}",
						new Object[] { item1.getId(), item2.getId() });

				// Combining was not successful
				String message = item1.getCombineWithForbiddenText(item2);
				if (message == null) {
					message = game.getNotUsableWithText();
				}
				io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(), game.getFailedFgColor());
			}
			// Effect depends on enabled status and additional actions
			item1.combineWith(item2, game);
		}

		/**
		 * Uses an {@link UsableWithSomething} with an {@link PassivelyUsable}.
		 * 
		 * @param originalCommand
		 *            if the command was original (or else additional).
		 * @param usable
		 *            the {@link UsableWithSomething}
		 * @param object
		 *            the object
		 * @param game
		 *            the game
		 */
		private void useWith(boolean originalCommand, UsableWithSomething usable, PassivelyUsable object, Game game) {
			if (!originalCommand) {
				// Check if the additional command belongs the the chosen
				// usable with object.
				if (!PatternGenerator.getPattern(usable.getAdditionalUseWithCommands(object))
						.matcher(currentReplacer.getInput()).matches()) {
					// no match
					String message = game.getNotUsableWithText();
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());
					return;
				}
			}

			if (usable.isUsingEnabledWith(object)) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Usewith enabled id {0} with {1}",
						new Object[] { usable.getId(), object.getId() });

				// Using was successful
				String message = usable.getUseWithSuccessfulText(object);
				if (message == null) {
					message = game.getUsedWithText();
				}
				io.println(currentReplacer.replacePlaceholders(message), game.getSuccessfullBgColor(),
						game.getSuccessfullFgColor());
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Usewith disabled id {0} with {1}",
						new Object[] { usable.getId(), object.getId() });

				// Using was not successful
				String message = usable.getUseWithForbiddenText(object);
				if (message == null) {
					message = game.getNotUsableWithText();
				}
				io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(), game.getFailedFgColor());
			}
			// Effect depends on additional actions
			usable.useWith(object, game);
		}

	}
}
