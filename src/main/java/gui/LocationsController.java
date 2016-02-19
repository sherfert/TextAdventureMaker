package gui;

import data.Location;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the locations view.
 * 
 * @author Satia
 *
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
	private Tab newTab;

	@FXML
	private Tab editTab;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	/**
	 * @param currentGameManager
	 */
	public LocationsController(CurrentGameManager currentGameManager) {
		super(currentGameManager);
	}

	@FXML
	private void initialize() {
		// Set cell value factories for the columns
		// TODO use properties
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<String>(p.getValue().getName()));
		descriptionCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<String>(p.getValue().getDescription()));

		// Get all locations and store in observable list
		locationsOL = FXCollections
				.observableArrayList(currentGameManager.getPersistenceManager().getLocationManager().getAllLocations());

		// Fill table
		table.setItems(locationsOL);
		
		// Disable save button at beginning
		saveButton.setDisable(true);
		
		// TODO
		// Handlers
		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		saveButton.setOnMouseClicked((e) -> saveNewLocation()); 
	}
	
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

}
