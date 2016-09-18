package playing.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.Travelable;
import exception.DBClosedException;
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
		super(gamePlayer, 1);
	}
	
	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getMoveHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getMoveCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() throws DBClosedException {
		return persistenceManager.getWayManager().getAllAdditionalTravelCommands();
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new MoveExecution(input);
	}
	
	/**
	 * Execution of the move command.
	 * 
	 * @author Satia
	 */
	private class MoveExecution extends CommandExecution {
		
		/** The way to move by */
		private Travelable way;

		/**
		 * @param input
		 *            the user input
		 */
		public MoveExecution(String input) {
			super(Move.this, input);
		}

		@Override
		public boolean hasObjects() {
			Game game = gamePlayer.getGame();
			String identifier = parameters[0].getIdentifier();
			way = persistenceManager.getWayManager().getWayOutFromLocation(game.getPlayer()
					.getLocation(), identifier);
			return way != null;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();

			String identifier = parameters[0].getIdentifier();
			currentReplacer.setIdentifier(identifier);
			
			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Move identifier {0}", identifier);

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
				way.travel(game);
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

}
