package gui;

import data.Location;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the locations view.
 * 
 * TODO Support to change list of items/persons/waysIn/waysOut. TODO Removing
 * locations
 * 
 * @author Satia
 *
 */
public class LocationsController extends GameDataController {

	/** An observable list with the locations. */
	private ObservableList<Location> locationsOL;

	// The currently edited location
	private Location editedLocation;

	@FXML
	private TableView<Location> table;

	@FXML
	private TableColumn<Location, Integer> idCol;

	@FXML
	private TableColumn<Location, String> nameCol;

	@FXML
	private TableColumn<Location, String> descriptionCol;

	@FXML
	private TabPane newEditTabs;

	@FXML
	private Tab newTab;

	@FXML
	private Tab editTab;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	@FXML
	private TextField editNameTF;

	@FXML
	private TextArea editDescriptionTA;

	@FXML
	private Button removeButton;

	/**
	 * @param currentGameManager
	 */
	public LocationsController(CurrentGameManager currentGameManager) {
		super(currentGameManager);
	}

	@FXML
	private void initialize() {
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());
		descriptionCol.setCellValueFactory((p) -> p.getValue().descriptionProperty());

		// A listener for row clicks
		table.setRowFactory(tv -> {
			TableRow<Location> row = new TableRow<>();
			row.setOnMouseClicked(event -> locationSelected(row.getItem()));
			return row;
		});

		// Get all locations and store in observable list, unless the list is
		// already propagated
		if (locationsOL == null) {
			locationsOL = FXCollections.observableArrayList(
					currentGameManager.getPersistenceManager().getLocationManager().getAllLocations());
		}

		// Fill table
		table.setItems(locationsOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);
		removeButton.setDisable(true);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewLocation());

		removeButton.setOnMouseClicked((e) -> removeLocation());
	}

	/**
	 * Saves a new location to both DB and table.
	 */
	private void saveNewLocation() {
		Location l = new Location(newNameTF.getText(), newDescriptionTA.getText());
		// Add location to DB
		currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(l);
		currentGameManager.getPersistenceManager().updateChanges();
		// Add location to our table
		locationsOL.add(l);

		// Reset the form values
		newNameTF.setText("");
		newDescriptionTA.setText("");
	}

	/**
	 * Places the selected location in the edit area of the tab pane.
	 * 
	 * @param l
	 *            the location
	 */
	private void locationSelected(Location l) {
		if (l == null) {
			return;
		}

		// Select the edit tab
		newEditTabs.getSelectionModel().select(editTab);
		// Activate the remove button
		removeButton.setDisable(false);

		// Remove previous bindings
		finishEditLocation();

		// Set new edited location
		editedLocation = l;

		// Create new bindings
		editNameTF.textProperty().bindBidirectional(l.nameProperty());
		editDescriptionTA.textProperty().bindBidirectional(l.descriptionProperty());
	}

	/**
	 * Called when the editing of a location was finished.
	 */
	private void finishEditLocation() {
		if (editedLocation != null) {
			editNameTF.textProperty().unbindBidirectional(editedLocation.nameProperty());
			editDescriptionTA.textProperty().unbindBidirectional(editedLocation.descriptionProperty());
			editedLocation = null;
		}
	}

	/**
	 * Removes a location from both DB and table.
	 */
	private void removeLocation() {
		// Remove location from DB
		currentGameManager.getPersistenceManager().getAllObjectsManager().removeObject(editedLocation);
		currentGameManager.getPersistenceManager().updateChanges();
		// Remove location from our table
		locationsOL.remove(editedLocation);

		// Stop editing the location
		finishEditLocation();
		// Disable the remove button
		removeButton.setDisable(true);
	}

}
