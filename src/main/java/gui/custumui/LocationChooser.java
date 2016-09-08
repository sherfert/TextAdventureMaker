package gui.custumui;

import java.util.List;
import java.util.function.Consumer;

import data.Location;
import exception.DBClosedException;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing locations. Must be initialized with
 * {@link LocationChooser#initialize(Location, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * @author satia
 */
public class LocationChooser extends NamedObjectChooser<Location> {

	/**
	 * Create a new LocationChooser
	 */
	public LocationChooser() {
		super(Location.class, "(no location)");
	}

	@Override
	protected List<Location> getAvailableValues() throws DBClosedException {
		return this.currentGameManager.getPersistenceManager().getLocationManager()
				.getAllLocations();
	}



}
