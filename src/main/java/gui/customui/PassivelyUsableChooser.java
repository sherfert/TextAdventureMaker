package gui.customui;

import data.interfaces.PassivelyUsable;

/**
 * Custom TextField for choosing PassivelyUsables.
 * 
 * @author satia
 */
public class PassivelyUsableChooser extends NamedObjectChooser<PassivelyUsable> {

	/**
	 * Create a new chooser
	 */
	public PassivelyUsableChooser() {
		super("(no object)");
	}
}
