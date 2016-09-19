package gui.customui;

import data.Conversation;

/**
 * Custom TextField for choosing conversations.
 * 
 * @author satia
 */
public class ConversationChooser extends NamedObjectChooser<Conversation> {

	/**
	 * Create a new ConversationChooser
	 */
	public ConversationChooser() {
		super("(no conversation)");
	}

}
