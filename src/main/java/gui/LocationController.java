package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Location;
import exception.DBClosedException;
import exception.DBIncompatibleException;
import gui.custumui.ItemListView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import logic.CurrentGameManager;

/**
 * Controller for one location.
 * 
 * TODO Support to change list of persons/waysIn/waysOut.
 * 
 * @author Satia
 */
public class LocationController extends GameDataController {

	/** The location */
	private Location location;

	@FXML
	private Button removeButton;

	@FXML
	private ItemListView itemListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param location
	 *            the location to edit
	 */
	public LocationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			Location location) {
		super(currentGameManager, mwController);
		this.location = location;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		removeButton.setOnMouseClicked((e) -> removeLocation());

		itemListView.initialize(location.getItems(),
				this.currentGameManager.getPersistenceManager().getItemManager()::getAllItems, location::updateItems,
				(item) -> item.setLocation(location), (item) -> item.setLocation(null));
	}

	/**
	 * Controller factory that initializes named object controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, location);
		} else if (type == NamedDescribedObjectController.class) {
			return new NamedDescribedObjectController(currentGameManager, mwController, location);
		} else {
			return super.controllerFactory(type);
		}
	}

	/**
	 * Removes a location from the DB.
	 */
	private void removeLocation() {
		if (isStartLocation(location)) {
			// Do not allow removal
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("This is the start location");
			alert.setHeaderText("The start location of a game cannot be removed.");
			alert.showAndWait();
			return;
		}

		removeObject(location, "Deleting a location", "Do you really want to delete this location?",
				"This will delete the location, the ways connected to this location, "
						+ "and actions associated with any of the deleted entities.");
	}

	/**
	 * Checks if a location is the start location.
	 * 
	 * @param l
	 *            the location
	 * @return if it is the start location.
	 */
	private boolean isStartLocation(Location l) {
		try {
			return currentGameManager.getPersistenceManager().getGameManager().getGame().getStartLocation().getId() == l
					.getId();
		} catch (DBIncompatibleException | DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not get Game.", e);
		}
		return false;
	}

}
