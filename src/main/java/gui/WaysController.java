package gui;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import data.Location;
import data.Way;
import exception.DBClosedException;
import gui.custumui.LocationChooser;
import gui.custumui.LocationRectangle;
import gui.custumui.WayLine;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import logic.CurrentGameManager;

/**
 * Controller for the ways view.
 * 
 * We extend NamedObjectsController instead of NamedDescribedObjectsController,
 * since we do not have a description column.
 * 
 * @author Satia
 */
public class WaysController extends NamedObjectsController<Way> {
	/**
	 * A map with all LocationRectangles.
	 */
	private Map<Location, LocationRectangle> rectangles = new HashMap<>();

	/**
	 * A map with all WayLines. Maps a list of origin and destination to the
	 * line.
	 */
	private Map<List<Location>, WayLine> lines = new HashMap<>();

	@FXML
	private TableColumn<Way, String> originCol;

	@FXML
	private TableColumn<Way, String> destinationCol;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private LocationChooser newOriginChooser;

	@FXML
	private LocationChooser newDestinationChooser;

	@FXML
	private Pane mapPane;

	@FXML
	BorderPane mapBorderPane;

	@FXML
	private Button mapNewWayButton;

	/**
	 * Constructor
	 * 
	 * @param currentGameManager
	 * @param mwController
	 */
	public WaysController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController, "view/Way.fxml");
	}

	@Override
	protected void resetFormValues() {
		super.resetFormValues();
		newDescriptionTA.setText("");
		newOriginChooser.setObjectValue(null);
		newDestinationChooser.setObjectValue(null);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		originCol.setCellValueFactory((p) -> p.getValue().getOrigin().nameProperty());
		destinationCol.setCellValueFactory((p) -> p.getValue().getDestination().nameProperty());

		newOriginChooser.initialize(null, false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
				});
		newDestinationChooser.initialize(null, false, false,
				this.currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
				});

		// Assure save is only enabled if there is a name, origin and
		// destination
		Supplier<Boolean> anyRequiredFieldEmpty = () -> newNameTF.textProperty().get().isEmpty()
				|| newOriginChooser.getObjectValue() == null || newDestinationChooser.getObjectValue() == null;
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newOriginChooser.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newDestinationChooser.textProperty()
				.addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));

		// Initializes everything contained in the Map Tab
		initializeMap();
	}

	@Override
	protected List<Way> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getWayManager().getAllWays();
	}

	@Override
	protected Way createNewObject(String name) {
		Way w = new Way(name, newDescriptionTA.getText(), newOriginChooser.getObjectValue(),
				newDestinationChooser.getObjectValue());

		// A line describing this way needs to be added to the Map
		Line line = createWayNode(w);
		if (line != null) {
			mapPane.getChildren().add(line);
			// Place lines behind rectangles
			line.toBack();
		}

		return w;
	}

	@Override
	protected GameDataController getObjectController(Way selectedObject) {
		return new WayController(currentGameManager, mwController, selectedObject);
	}

	/**
	 * Opens this location for editing.
	 * 
	 * @param l
	 *            the location
	 */
	private void locationSelected(Location l) {
		if (l == null) {
			return;
		}

		GameDataController c = new LocationController(currentGameManager, mwController, l);
		mwController.pushCenterContent(l.getName(), "view/Location.fxml", c, c::controllerFactory);
	}

	/**
	 * Creates a new LocationRectangle, and saves it in the rectangles Map.
	 * 
	 * @param location
	 *            the location
	 * @return the LocationRectangle
	 */
	private LocationRectangle createLocationNode(Location location) {
		LocationRectangle sp = new LocationRectangle(location, this::locationSelected);
		// Save in map
		rectangles.put(location, sp);

		return sp;
	}

	/**
	 * Creates a line to represent a Way or returns {@code null}, if a
	 * previously created line already covers this way
	 * 
	 * @param w
	 *            the Way
	 * @return the Line or {@code null}
	 */
	private WayLine createWayNode(Way w) {
		List<Location> endpoints = Arrays.asList(w.getOrigin(), w.getDestination()).stream()
				.sorted(Comparator.comparing(Location::getId)).collect(Collectors.toList());

		WayLine line;
		if ((line = lines.get(endpoints)) == null) {
			line = new WayLine(w, rectangles.get(w.getOrigin()), rectangles.get(w.getDestination()), this::objectSelected);
			lines.put(endpoints, line);
			return line;
		} else {
			// Save the new way in the lines list
			line.addWay(w);
			return null;
		}
	}

	/**
	 * Initializes the MapView.
	 * 
	 * TODO possibility to add new Ways here.
	 */
	private void initializeMap() {

		// Empty the maps
		rectangles.clear();
		lines.clear();

		// Obtain all locations
		List<Location> locations;
		try {
			locations = currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();
		} catch (DBClosedException e1) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}

		// Create rectangles
		for (Location l : locations) {
			LocationRectangle lr = createLocationNode(l);
			mapPane.getChildren().add(lr);
		}

		// Create lines
		for (Way w : objectsOL) {
			Line line = createWayNode(w);
			if (line != null) {
				mapPane.getChildren().add(line);
				// Place lines behind rectangles
				line.toBack();
			}
		}

		// The add button
		mapNewWayButton.setOnMouseClicked((e) -> {
			//mapBorderPane.setCursor(Cursor.CROSSHAIR);
		});
	}

}
