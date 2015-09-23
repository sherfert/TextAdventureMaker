package playing.command;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.Inspectable;
import persistence.InspectableObjectManager;
import playing.GamePlayer;

/**
 * Command to inspect something.
 * 
 * Although inspect has a parameter, no additional commands are allowed for
 * this. There seems to be only one way in which something can be inspected and
 * since inspecting is never forbidden, having aliases could yield strange
 * results.
 * 
 * @author Satia
 */
public class Inspect extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public Inspect(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands
		return new HashSet<>();
	}

	@Override
	public void execute(boolean originalCommand, String... identifiers) {
		if (identifiers.length != 1) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of identifiers");
			return;
		}
		inspect(identifiers[0]);
	}

	/**
	 * Tries to inspect an object with the given name. The player will look at
	 * it if possible and if not, a meaningful message will be displayed.
	 * @param identifier
	 *            an identifier of the object
	 */
	private void inspect(String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Inspecting identifier {0}", identifier);

		Game game = gamePlayer.getGame();

		Inspectable object = InspectableObjectManager
				.getInspectable(identifier);
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
}
