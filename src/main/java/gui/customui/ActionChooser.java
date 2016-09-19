package gui.customui;

import data.action.AbstractAction;

/**
 * Custom TextField for choosing actions.
 * 
 * @author satia
 */
public class ActionChooser extends NamedObjectChooser<AbstractAction> {

	/**
	 * Create a new chooser
	 */
	public ActionChooser() {
		super("(no action)");
	}
}
