package gui;

import java.util.List;

import data.Location;
import exception.DBClosedException;
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
		super(currentGameManager, mwController, "view/Location.fxml");
	}

	@Override
	protected List<Location> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();
	}

	@Override
	protected Location createNewObject(String name, String description) {
		return new Location(name, description);
	}

	@Override
	protected GameDataController getObjectController(Location selectedObject) {
		return new LocationController(currentGameManager, mwController, selectedObject);
	}

}
