package playing.command;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import playing.GamePlayer;
import playing.parser.Parameter;

/**
 * Command to look around.
 * 
 * @author Satia
 */
public class LookAround extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public LookAround(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands for commands with no parameters
		return new HashSet<>();
	}

	@Override
	public void execute(boolean originalCommand, Parameter... parameters) {
		if (parameters.length != 0) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of parameters");
			return;
		}
		lookAround();
	}

	/**
	 * Displays the location entered text to the player.
	 */
	private void lookAround() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Looking around");
		
		Game game = gamePlayer.getGame();

		io.println(
				currentReplacer.replacePlaceholders(game.getPlayer()
						.getLocation().getEnteredText()),
				game.getNeutralBgColor(), game.getNeutralFgColor());
	}

}
