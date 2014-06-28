package playing;

import data.Conversation;
import data.ConversationLayer;
import data.ConversationOption;
import data.Game;

/**
 * Plays a conversation. The conversation is started immediately upon
 * construction.
 * 
 * 
 * @author Satia
 * 
 */
public class ConversationPlayer {

	/**
	 * The IO object.
	 */
	private final InputOutput io;

	/**
	 * The game object.
	 */
	private final Game game;

	/**
	 * The conversation to play.
	 */
	private final Conversation conversation;

	/**
	 * The name of the person we're talking to
	 */
	private final String personName;

	/**
	 * The current layer of the conversation.
	 */
	private ConversationLayer currentLayer;

	/**
	 * Construct a conversation player and start the conversation.
	 * 
	 * @param io
	 *            the io
	 * @param game
	 *            the game
	 * @param conversation
	 *            the conversation to play
	 * @param personName
	 *            the name of the person
	 */
	public ConversationPlayer(InputOutput io, Game game,
			Conversation conversation, String personName) {
		this.io = io;
		this.game = game;
		this.conversation = conversation;
		this.personName = personName;
		this.currentLayer = conversation.getStartLayer();
		startConversation();
	}

	/**
	 * Choose the option with the given index. No index checks!
	 * 
	 * @param index
	 *            the index of the option to choose
	 */
	public void chooseOption(int index) {
		ConversationOption chosenOption = currentLayer.getOptions().get(index);
		// Trigger additional actions
		chosenOption.choose();
		// Switch to target layer
		currentLayer = chosenOption.getTarget();

		if (hasEnded()) {
			io.exitConversationMode();
		} else {
			// Print text and answer
			playerSays(chosenOption.getText());
			personSays(chosenOption.getAnswer());
			// Display new options
			io.setOptions(currentLayer.getOptionTexts());
		}
	}

	/**
	 * Starts the conversation.
	 */
	private void startConversation() {
		// Check if the conversation has ended before it began
		if (hasEnded()) {
			// Print the greeting without going into conversation mode
			personSays(conversation.getGreeting());
		} else {
			io.enterConversationMode(this);
			// Print the greeting after going into conversation mode
			personSays(conversation.getGreeting());
			// Display the options
			io.setOptions(currentLayer.getOptionTexts());
		}
	}

	/**
	 * Prints the text the person says.
	 * 
	 * @param text
	 *            the text
	 */
	private void personSays(String text) {
		io.println(personName + ": " + text, game.getNeutralBgColor(),
				game.getNeutralFgColor());
	}

	/**
	 * Prints the text the player says.
	 * 
	 * @param text
	 *            the text
	 */
	private void playerSays(String text) {
		io.println(text);
	}

	/**
	 * There are no options left and the conversation has ended if there is no
	 * current layer or the current layer has no options.
	 * 
	 * @return if the conversation has ended.
	 */
	private boolean hasEnded() {
		return currentLayer == null || !currentLayer.isPlayable();
	}

}
