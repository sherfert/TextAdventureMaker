package playing.command;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.HasConversation;
import exception.DBClosedException;
import playing.ConversationPlayer;
import playing.GamePlayer;
import playing.parser.PatternGenerator;

/**
 * Command to talk to a person.
 * 
 * @author Satia
 */
public class TalkTo extends Command {

	/**
	 * @param gamePlayer
	 *            the game player
	 */
	public TalkTo(GamePlayer gamePlayer) {
		super(gamePlayer, 1);
	}
	
	@Override
	public String getHelpText() {
		return this.gamePlayer.getGame().getTalkToHelpText();
	}

	@Override
	public List<String> getCommands() {
		return this.gamePlayer.getGame().getTalkToCommands();
	}

	@Override
	public Set<String> getAdditionalCommands() throws DBClosedException {
		return persistenceManager.getPersonManager().getAllAdditionalTalkToCommands();
	}

	@Override
	public CommandExecution newExecution(String input) {
		return new TalkToExecution(input);
	}
	
	/**
	 * Execution of the talk to command.
	 * 
	 * @author Satia
	 */
	private class TalkToExecution extends CommandExecution {

		/**
		 * @param input
		 *            the user input
		 */
		public TalkToExecution(String input) {
			super(TalkTo.this, input);
		}

		@Override
		public boolean hasObjects() {
			findInspectableObjects();
			return object1 != null && object1 instanceof HasConversation;
		}

		@Override
		public void execute() {
			configureReplacer();
			Game game = gamePlayer.getGame();
			String identifier = parameters[0].getIdentifier();
			

			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Talk to identifier {0}", identifier);

			if (object1 == null) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Talk to person not found {0}", identifier);

				// There is no such person
				String message = game.getNoSuchPersonText();
				io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(), game.getFailedFgColor());
			} else {
				if (object1 instanceof HasConversation) {
					HasConversation person = (HasConversation) object1;
					Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Talk to id {0}", person.getId());

					if (!originalCommand) {
						// Check if the additional command belongs the the chosen
						// usable
						if (!PatternGenerator.getPattern(person.getAdditionalTalkToCommands())
								.matcher(currentReplacer.getInput()).matches()) {
							// no match
							String message = game.getNotTalkingToEnabledText();
							io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
									game.getFailedFgColor());
							return;
						}
					}

					if (person.isTalkingEnabled()) {
						Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Talk to id {0} enabled",
								person.getId());

						// Start the conversation
						new ConversationPlayer(io, game, person.getConversation(), person.getName(),
								gamePlayer.getMenuShower());
					} else {
						Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Talk to {0} disabled",
								person.getId());

						// Talking disabled
						String message = person.getTalkingToForbiddenText();
						if (message == null) {
							message = game.getNotTalkingToEnabledText();
						}
						io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
								game.getFailedFgColor());
					}
					// Effect depends on additional actions
					person.talkTo(game);
				} else {
					Logger.getLogger(this.getClass().getName()).log(Level.FINER,
							"Talk to person not of type HasConversation {0}", identifier);

					// There is something (e.g. an item), but nothing you could
					// talk to.
					String message = game.getInvalidCommandText();
					io.println(currentReplacer.replacePlaceholders(message), game.getFailedBgColor(),
							game.getFailedFgColor());
				}
			}
		}

	}

}
