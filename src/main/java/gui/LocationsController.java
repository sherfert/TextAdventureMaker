package gui;

import java.util.List;

import data.Location;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.CurrentGameManager;

/**
 * Controller for the locations view.
 * 
 * @author Satia
 *
 */
public class LocationsController extends GameDataController {
	
	@FXML
	private TableView<Location> table;
	
	@FXML
	private TableColumn<Location, Integer> idCol;
	
	@FXML
	private TableColumn<Location, String> nameCol;
	
	@FXML
	private TableColumn<Location, String> descriptionCol;

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
		
		// Get all locations
		List<Location> locations = currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();
		
		// Create an observable list
		// TODO deal properly with it
		ObservableList<Location> locationsOL = FXCollections.observableArrayList(locations);
		
		// Fill table
		table.setItems(locationsOL);
	}

}
