package playing.command;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.Travelable;
import persistence.WayManager;
import playing.GamePlayer;
import playing.parser.PatternGenerator;

/**
 * Command to move to another location.
 * 
 * @author Satia
 */
public class Move extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public Move(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		return WayManager.getAllAdditionalTravelCommands();
	}

	@Override
	public void execute(boolean originalCommand, String... identifiers) {
		if (identifiers.length != 1) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of identifiers");
			return;
		}
		move(originalCommand, identifiers[0]);
	}
	
	/**
	 * Tries to move to the target with the given name. The player will move
	 * there if possible and if not, a meaningful message will be displayed.
	 * 
	 * @param originalCommand
	 *            if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the object
	 */
	private void move(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Move identifier {0}", identifier);
		
		Game game = gamePlayer.getGame();

		Travelable way = WayManager.getWayOutFromLocation(game.getPlayer()
				.getLocation(), identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (way != null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Move id {0}", way.getId());
			// Save name
			currentReplacer.setName(way.getName());
			
			if (!originalCommand) {
				// Check if the additional command belongs the the chosen
				// usable
				if (!PatternGenerator
						.getPattern(way.getAdditionalTravelCommands())
						.matcher(currentReplacer.getInput()).matches()) {
					// no match
					String message = game.getNotTravelableText();
					io.println(
							currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(),
							game.getFailedFgColor());
					return;
				}
			}
			
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

}
