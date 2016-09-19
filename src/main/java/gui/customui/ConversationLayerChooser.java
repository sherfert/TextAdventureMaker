package gui.customui;

import data.ConversationLayer;

/**
 * Custom TextField for choosing conversations layers.
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
