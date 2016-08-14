package gui;

import data.Way;
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
 * Controller for the ways view.
 * 
 * TODO Adding ways.
 * TODO MapView (as another tab)
 * 
 * @author Satia
 */
public class WaysController extends GameDataController {

	/** An observable list with the ways. */
	private ObservableList<Way> waysOL;

	@FXML
	private TableView<Way> table;

	@FXML
	private TableColumn<Way, Integer> idCol;

	@FXML
	private TableColumn<Way, String> nameCol;

	@FXML
	private TableColumn<Way, String> originCol;
	
	@FXML
	private TableColumn<Way, String> destinationCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	public WaysController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	private void initialize() {
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());
		originCol.setCellValueFactory((p) -> p.getValue().getOrigin().nameProperty());
		destinationCol.setCellValueFactory((p) -> p.getValue().getDestination().nameProperty());
		
		// A listener for row double-clicks
		table.setRowFactory(tv -> {
			TableRow<Way> row = new TableRow<>();
			row.setOnMouseClicked(event ->  {
				if(event.getClickCount() == 2) {
					waySelected(row.getItem());
				}
			});
			return row;
		});

		// Get all ways and store in observable list, unless the list is
		// already propagated
		if (waysOL == null) {
			waysOL = FXCollections.observableArrayList(
					currentGameManager.getPersistenceManager().getWayManager().getAllWays());
		}

		// Fill table
		table.setItems(waysOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewWay());
	}
	
	@Override
	public void update() {
		if (waysOL != null) {
			waysOL.setAll(currentGameManager.getPersistenceManager().getWayManager().getAllWays());
		}
	}

	/**
	 * Saves a new way to both DB and table.
	 */
	private void saveNewWay() {
//		Way w = new Item(newNameTF.getText(), newDescriptionTA.getText());
//		// Add item to DB
//		currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(w);
//		currentGameManager.getPersistenceManager().updateChanges();
//		// Add location to our table
//		waysOL.add(w);
//
//		// Reset the form values
//		newNameTF.setText("");
//		newDescriptionTA.setText("");
	}
	
	/**
	 * Opens this way for editing.
	 * 
	 * @param w
	 *            the way
	 */
	private void waySelected(Way w) {
		if (w == null) {
			return;
		}

		// TODO Open the way view
		//ItemController itemController = new ItemController(currentGameManager, mwController, i);
		//mwController.pushCenterContent(i.getName(),"view/Item.fxml", itemController, itemController::controllerFactory);
	}

}
