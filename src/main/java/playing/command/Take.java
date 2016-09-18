package playing.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import playing.GamePlayer;
import playing.parser.Parameter;
import playing.parser.PatternGenerator;
import data.Game;
import data.interfaces.Inspectable;
import data.interfaces.Takeable;
import exception.DBClosedException;
import exception.DBIncompatibleException;

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
	public void execute(boolean originalCommand, Parameter... parameters) {
		if (parameters.length != numberOfParameters) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of parameters");
			return;
		}
		take(originalCommand, parameters[0].getIdentifier());
	}

	/**
	 * Tries to take the object with the given name. The connected actions will
	 * be performed if the item is takeable (additional actions will be
	 * performed even if it is not). If not, a meaningful message will be displayed.
	 * 
	 * @param originalCommand
	 *            if the command was original (or else additional). Used to test
	 *            if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the object
	 */
	private void take(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Take identifier {0}", identifier);

		Game game = gamePlayer.getGame();

		// Collect all objects, whether they are takeable or not.
		Inspectable object;
		try {
			object = persistenceManager.getInspectableObjectManager()
					.getInspectable(identifier);
		} catch (DBClosedException | DBIncompatibleException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Operating on a closed/incompatible DB", e);
			return;
		}

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
