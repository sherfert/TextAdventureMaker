package gui;

import data.Location;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the locations view.
 * 
 * @author Satia
 */
public class LocationsController extends GameDataController {

	/** An observable list with the locations. */
	private ObservableList<Location> locationsOL;

	@FXML
	private TableView<Location> table;

	@FXML
	private TableColumn<Location, Integer> idCol;

	@FXML
	private TableColumn<Location, String> nameCol;

	@FXML
	private TableColumn<Location, String> descriptionCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	public LocationsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	private void initialize() {
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());
		descriptionCol.setCellValueFactory((p) -> p.getValue().descriptionProperty());

		// A listener for row double-clicks
		table.setRowFactory(tv -> {
			TableRow<Location> row = new TableRow<>();
			row.setOnMouseClicked(event ->  {
				if(event.getClickCount() == 2) {
					locationSelected(row.getItem());
				}
			});
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

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewLocation());
	}

	@Override
	public void update() {
		if (locationsOL != null) {
			locationsOL.setAll(currentGameManager.getPersistenceManager().getLocationManager().getAllLocations());
		}
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
	 * Opens this location for editing
	 * 
	 * @param l
	 *            the location
	 */
	private void locationSelected(Location l) {
		if (l == null) {
			return;
		}

		// Open the location view
		LocationController locationController = new LocationController(currentGameManager, mwController, l);
		mwController.pushCenterContent(l.getName(),"view/Location.fxml", locationController, locationController::controllerFactory);
	}
}
