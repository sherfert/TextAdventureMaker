package gui.customui;

import java.util.function.Consumer;

import data.Conversation;
import data.ConversationLayer;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing conversations layers. Must be initialized with
 * {@link ConversationLayerChooser#initialize(Conversation, boolean, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * @author satia
 */
public class ConversationLayerChooser extends NamedObjectChooser<ConversationLayer> {

	/**
	 * Create a new ConversationChooser
	 */
	public ConversationLayerChooser() {
		super("(ends conversation)");
	}

}
