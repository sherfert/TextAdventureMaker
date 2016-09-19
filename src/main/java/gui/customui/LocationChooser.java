package gui.customui;

import data.Location;

/**
 * Custom TextField for choosing locations.
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
