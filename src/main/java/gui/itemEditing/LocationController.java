package gui.itemEditing;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Item;
import data.Location;
import data.Person;
import data.Way;
import exception.DBClosedException;
import exception.DBIncompatibleException;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectListView;
import gui.include.NamedDescribedObjectController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import logic.CurrentGameManager;
import utility.WindowUtil;

/**
 * Controller for one location.
 * 
 * @author Satia
 */
public class LocationController extends GameDataController {

	/** The location */
	private Location location;
	
	@FXML
	private TabPane tabPane;

	@FXML
	private Button removeButton;

	@FXML
	private NamedObjectListView<Item> itemListView;

	@FXML
	private NamedObjectListView<Person> personListView;

	@FXML
	private NamedObjectListView<Way> waysInListView;

	@FXML
	private NamedObjectListView<Way> waysOutListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
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
				this::objectSelected, (item) -> item.setLocation(location), (item) -> item.setLocation(null));

		personListView.initialize(location.getPersons(),
				this.currentGameManager.getPersistenceManager().getPersonManager()::getAllPersons,
				location::updatePersons, this::objectSelected, (person) -> person.setLocation(location),
				(person) -> person.setLocation(null));

		waysInListView.initialize(location.getWaysIn(),
				this.currentGameManager.getPersistenceManager().getWayManager()::getAllWays, location::updateWaysIn,
				this::objectSelected, (way) -> way.setDestination(location), null);
		
		waysOutListView.initialize(location.getWaysOut(),
				this.currentGameManager.getPersistenceManager().getWayManager()::getAllWays, location::updateWaysOut,
				this::objectSelected, (way) -> way.setOrigin(location), null);
		
		saveTabIndex(tabPane);
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
			WindowUtil.attachIcon((Stage) alert.getDialogPane().getScene().getWindow());
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
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(location);
	}

}
