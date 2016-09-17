package gui.customui;

import java.util.function.Consumer;

import data.Location;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing locations. Must be initialized with
 * {@link LocationChooser#initialize(Location, boolean, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * @author satia
 */
public class LocationChooser extends NamedObjectChooser<Location> {

	/**
	 * Create a new LocationChooser
	 */
	public LocationChooser() {
		super("(no location)");
	}
}
