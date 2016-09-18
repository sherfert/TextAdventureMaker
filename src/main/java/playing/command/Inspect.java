package playing.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
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
		super(gamePlayer, 1);
	}

	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getInspectHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getInspectCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// There are no additional commands
		return new HashSet<>();
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new InspectExecution(input);
	}

	/**
	 * Execution of the inspect command.
	 * 
	 * @author Satia
	 */
	private class InspectExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public InspectExecution(String input) {
			super(Inspect.this, input);
		}

		@Override
		public boolean hasObjects() {
			findInspectableObjects();
			return object1 != null;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();
			String identifier = parameters[0].getIdentifier();

			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Inspecting identifier {0}", identifier);

			if (object1 != null) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Inspecting id {0}", object1.getId());

				String message = object1.getInspectionText();
				if (message == null) {
					message = game.getInspectionDefaultText();
				}
				io.println(currentReplacer.replacePlaceholders(message), game.getNeutralBgColor(),
						game.getNeutralFgColor());
				// Effect depends on additional actions
				object1.inspect(game);
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Inspect object not found {0}",
						identifier);

				// There is no such thing
				String message = game.getNoSuchItemText();
				io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
						game.getFailedFgColor());
			}
		}

	}
}
