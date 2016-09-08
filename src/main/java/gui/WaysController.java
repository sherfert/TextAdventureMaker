package gui;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Way;
import exception.DBClosedException;
import gui.custumui.LocationChooser;
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
	private LocationChooser newOriginChooser;

	@FXML
	private LocationChooser newDestinationChooser;

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
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					waySelected(row.getItem());
				}
			});
			return row;
		});

		// Get all ways and store in observable list, unless the list is
		// already propagated
		if (waysOL == null) {
			try {
				waysOL = FXCollections
						.observableArrayList(currentGameManager.getPersistenceManager().getWayManager().getAllWays());
			} catch (DBClosedException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
				return;
			}
		}

		// Fill table
		table.setItems(waysOL);

		newOriginChooser.initialize(null, false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
				});
		newDestinationChooser.initialize(null, false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
				});

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name, origin and
		// destination
		Supplier<Boolean> anyRequiredFieldEmpty = () -> newNameTF.textProperty().get().isEmpty()
				|| newOriginChooser.getObjectValue() == null || newDestinationChooser.getObjectValue() == null;
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newOriginChooser.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newDestinationChooser.textProperty()
				.addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewWay());
	}

	@Override
	public void update() {
		if (waysOL != null) {
			try {
				waysOL.setAll(currentGameManager.getPersistenceManager().getWayManager().getAllWays());
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
				return;
			}
		}
	}

	/**
	 * Saves a new way to both DB and table.
	 */
	private void saveNewWay() {
		Way w = new Way(newNameTF.getText(), newDescriptionTA.getText(), newOriginChooser.getObjectValue(),
				newDestinationChooser.getObjectValue());
		// Add item to DB
		try {
			currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(w);
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}
		currentGameManager.getPersistenceManager().updateChanges();
		// Add location to our table
		waysOL.add(w);

		// Reset the form values
		newNameTF.setText("");
		newDescriptionTA.setText("");
		newOriginChooser.setObjectValue(null);
		newDestinationChooser.setObjectValue(null);
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

		// Open the way view
		WayController wayController = new WayController(currentGameManager, mwController, w);
		mwController.pushCenterContent(w.getName(), "view/Way.fxml", wayController, wayController::controllerFactory);
	}

}
