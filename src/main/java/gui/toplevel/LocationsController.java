package gui.toplevel;

import java.util.List;

import data.Location;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
import gui.wizards.NewNamedObjectWizard;
import javafx.fxml.FXML;
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
	
	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		newButton.setText("New location");
	}

	@Override
	protected List<Location> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();
	}

	
	@Override
	public boolean isObsolete() {
		return false;
	}

	@Override
	protected void createObject() {
		new NewNamedObjectWizard("New location").showAndGetName().map(name -> new Location(name, ""))
		.ifPresent(this::saveObject);
	}

}
