package gui.toplevel;

import java.util.List;

import data.Location;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
import logic.CurrentGameManager;

/**
 * Controller for the locations view.
 * 
 * @author Satia
 */
public class LocationsController extends NamedDescribedObjectsController<Location> {

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public LocationsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@Override
	protected List<Location> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();
	}

	@Override
	protected Location createNewObject(String name, String description) {
		return new Location(name, description);
	}

}
