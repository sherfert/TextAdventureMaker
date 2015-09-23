package playing.command;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Game;
import data.interfaces.HasConversation;
import data.interfaces.Inspectable;
import persistence.InspectableObjectManager;
import playing.ConversationPlayer;
import playing.GamePlayer;

/**
 * CommandType to talk to a person.
 * 
 * @author Satia
 */
public class TalkTo extends Command {

	/**
	 * @param gamePlayer the game player
	 */
	public TalkTo(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	public Set<String> getAdditionalCommands() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	@Override
	public void execute(boolean originalCommand, String... identifiers) {
		if (identifiers.length != 1) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Execute: wrong number of identifiers");
			return;
		}
		talkTo(originalCommand, identifiers[0]);
	}
	
	/**
	 * Tries to talk to the person with the given name. The additional actions
	 * will be performed. Either the conversation will be started or a message
	 * informing about failure will be displayed.
	 * 
	 * @param originalCommand
	 *            TODO if the command was original (or else additional). Used to
	 *            test if an additional command really belonged to the chosen
	 *            identifier.
	 * @param identifier
	 *            an identifier of the person
	 */
	private void talkTo(boolean originalCommand, String identifier) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Talk to identifier {0}", identifier);
		
		Game game = gamePlayer.getGame();

		Inspectable object = InspectableObjectManager.getInspectable(identifier);
		// Save identifier
		currentReplacer.setIdentifier(identifier);

		if (object == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINER,
					"Talk to person not found {0}", identifier);

			// There is no such person
			String message = game.getNoSuchPersonText();
			io.println(currentReplacer.replacePlaceholders(message),
					game.getFailedBgColor(), game.getFailedFgColor());
		} else {
			// Save name
			currentReplacer.setName(object.getName());

			if (object instanceof HasConversation) {
				HasConversation person = (HasConversation) object;
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Talk to id {0}", person.getId());
				if (person.isTalkingEnabled()) {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Talk to id {0} enabled",
							person.getId());

					// Start the conversation
					new ConversationPlayer(io, game, person.getConversation(),
							person.getName());
				} else {
					Logger.getLogger(this.getClass().getName()).log(
							Level.FINEST, "Talk to {0} disabled",
							person.getId());

					// Talking disabled
					String message = person.getTalkingToForbiddenText();
					if (message == null) {
						message = game.getNotTalkingToEnabledText();
					}
					io.println(currentReplacer.replacePlaceholders(message),
							game.getFailedBgColor(), game.getFailedFgColor());
				}
				// Effect depends on additional actions
				person.talkTo();
			} else {
				Logger.getLogger(this.getClass().getName()).log(Level.FINER,
						"Talk to person not of type HasConversation {0}",
						identifier);

				// There is something (e.g. an item), but nothing you could
				// talk to.
				String message = game.getInvalidCommandText();
				io.println(currentReplacer.replacePlaceholders(message),
						game.getFailedBgColor(), game.getFailedFgColor());
			}
		}
	}

}