package playing.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.Takeable;
import exception.DBClosedException;
import playing.GamePlayer;
import playing.parser.PatternGenerator;

/**
 * Command to take an item.
 * 
 * @author Satia
 */
public class Take extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public Take(GamePlayer gamePlayer) {
		super(gamePlayer, 1);
	}
	
	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getTakeHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getTakeCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() throws DBClosedException {
		return persistenceManager.getItemManager().getAllAdditionaTakeCommands();
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new TakeExecution(input);
	}
	
	/**
	 * Execution of the take command.
	 * 
	 * @author Satia
	 */
	private class TakeExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public TakeExecution(String input) {
			super(Take.this, input);
		}

		@Override
		public boolean hasObjects() {
			findInspectableObjects();
			return object1 != null && object1 instanceof Takeable;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();
			String identifier = parameters[0].getIdentifier();
			
			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Take identifier {0}", identifier);

			if (object1 == null) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Take item not found {0}", identifier);

				// There is no such item
				String message = game.getNoSuchItemText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			} else {
				if (object1 instanceof Takeable) {
					Takeable item = (Takeable) object1;

					Logger.getLogger(this.getClass().getName()).log(Level.FINER,
							"Take id {0}", item.getId());

					if (!originalCommand) {
						// Check if the additional command belongs the the chosen
						// usable
						if (!PatternGenerator
								.getPattern(item.getAdditionalTakeCommands())
								.matcher(currentReplacer.getInput()).matches()) {
							// no match
							String message = game.getNotTakeableText();
							io.println(
									currentReplacer.replacePlaceholders(message),
									game.getFailedBgColor(),
									game.getFailedFgColor());
							return;
						}
					}

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
					item.take(game);
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

	}

}
