package playing.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import playing.GamePlayer;

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
		super(gamePlayer, 0);
	}
	
	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getLookAroundHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getLookAroundCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands for commands with no parameters
		return new HashSet<>();
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new LookAroundExecution(input);
	}
	
	/**
	 * Execution of the look around command.
	 * 
	 * @author Satia
	 */
	private class LookAroundExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public LookAroundExecution(String input) {
			super(LookAround.this, input);
		}

		@Override
		public boolean hasObjects() {
			return true;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();

			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Looking around");

			io.println(
					currentReplacer.replacePlaceholders(game.getPlayer()
							.getLocation().getEnteredText()),
					game.getNeutralBgColor(), game.getNeutralFgColor());
		}

	}

}
