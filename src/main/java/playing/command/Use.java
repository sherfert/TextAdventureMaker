package playing.command;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.Inspectable;
import data.interfaces.Usable;
import playing.GamePlayer;
import playing.parser.Parameter;
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
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		return persistenceManager.getUsableObjectManager().getAllAdditionalUseCommands();
	}

	@Override
	public void execute(boolean originalCommand, Parameter... parameters) {
		if (parameters.length != 1) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of parameters");
			return;
		}
		use(originalCommand, parameters[0].getIdentifier());
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
	private void use(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Use identifier {0}", identifier);
		
		Game game = gamePlayer.getGame();

		// Collect all objects, whether they are usable or not.
		Inspectable objectI = persistenceManager.getInspectableObjectManager()
				.getInspectable(identifier);
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
						io.println(
								currentReplacer.replacePlaceholders(message),
								game.getFailedBgColor(),
								game.getFailedFgColor());
						return;
					}
				}

				// Save name
				currentReplacer.setName(object.getName());
				if (object.getUsingEnabled()) {
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
				object.use(game);
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

}
