package gui.customui;

import java.util.function.Consumer;

import data.interfaces.PassivelyUsable;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing PassivelyUsables. Must be initialized with
 * {@link NamedDescribedObjectChooser#initialize(PassivelyUsables, boolean, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
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
