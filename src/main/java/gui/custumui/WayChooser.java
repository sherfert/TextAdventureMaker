package gui.custumui;

import java.util.function.Consumer;

import data.Way;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing ways. Must be initialized with
 * {@link WayChooser#initialize(Way, boolean, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * @author satia
 */
public class WayChooser extends NamedObjectChooser<Way> {

	/**
	 * Create a new LocationChooser
	 */
	public WayChooser() {
		super("(no way)");
	}
}
