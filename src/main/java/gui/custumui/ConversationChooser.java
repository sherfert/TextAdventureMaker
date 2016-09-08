package gui.custumui;

import java.util.function.Consumer;

import data.Conversation;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing conversations. Must be initialized with
 * {@link ConversationChooser#initialize(Conversation, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
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
