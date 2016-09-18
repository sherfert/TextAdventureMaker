package playing.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.Usable;
import exception.DBClosedException;
import playing.GamePlayer;
import playing.parser.PatternGenerator;

/**
 * The command to use one object.
 * 
 * @author Satia
 */
public class Use extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public Use(GamePlayer gamePlayer) {
		super(gamePlayer, 1);
	}

	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getUseHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getUseCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() throws DBClosedException {
		return persistenceManager.getUsableObjectManager().getAllAdditionalUseCommands();
	}
	
	@Override
	public CommandExecution newExecution(String input) {
		return new UseExecution(input);
	}

	/**
	 * Execution of the use command.
	 * 
	 * @author Satia
	 */
	private class UseExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public UseExecution(String input) {
			super(Use.this, input);
		}

		@Override
		public boolean hasObjects() {
			findInspectableObjects();
			return object1 != null && object1 instanceof Usable;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();
			String identifier = parameters[0].getIdentifier();

			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Use identifier {0}", identifier);

			if (object1 == null) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Use item not found {0}", identifier);
				// There is no such object and you have no such object
				String message = game.getNoSuchItemText() + " " + game.getNoSuchInventoryItemText();
				io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
						game.getFailedFgColor());
			} else {
				if (object1 instanceof Usable) {
					Usable object = (Usable) object1;

					Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Use id {0}", object.getId());

					if (!originalCommand) {
						// Check if the additional command belongs to the
						// chosen usable
						if (!PatternGenerator.getPattern(object.getAdditionalUseCommands())
								.matcher(currentReplacer.getInput()).matches()) {
							// no match
							String message = game.getNotUsableText();
							io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
									game.getFailedFgColor());
							return;
						}
					}

					if (object.getUsingEnabled()) {
						Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Use id {0} enabled",
								object.getId());

						// The object was used
						String message = object.getUseSuccessfulText();
						if (message == null) {
							message = game.getUsedText();
						}
						io.println(currentReplacer.replacePlaceholders(message), game.getSuccessfullBgColor(),
								game.getSuccessfullFgColor());
					} else {
						Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Use id {0} disabled",
								object.getId());

						// The object was not used
						String message = object.getUseForbiddenText();
						if (message == null) {
							message = game.getNotUsableText();
						}
						io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
								game.getFailedFgColor());
					}
					// Effect depends on additional actions
					object.use(game);
				} else {
					Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Use item not of type Useable {0}",
							identifier);
					// There is something (e.g. a person), but nothing you could
					// use.
					String message = game.getInvalidCommandText();
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());
				}
			}
		}

	}

}
