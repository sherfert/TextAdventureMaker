package gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Location;
import data.Way;
import exception.DBClosedException;
import gui.custumui.LocationChooser;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
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
	 * A special StackPane for displaying locations as a rectangle with their
	 * name inside.
	 * 
	 * @author Satia
	 */
	private class LocationRectangle extends StackPane {
		/**
		 * The location of this rectangle
		 */
		Location location;

		/**
		 * The center on the X axis.
		 */
		DoubleBinding centerX = layoutXProperty().add(rectDim / 2);

		/**
		 * The center on the Y axis.
		 */
		DoubleBinding centerY = layoutYProperty().add(rectDim / 2);
	}

	/**
	 * The height and width of LocationRectangles.
	 */
	private static final double rectDim = 120;

	// These values are used by the dragHandlers to position rectangles
	// correctly
	private double orgSceneX;
	private double orgSceneY;
	private double orgX;
	private double orgY;

	/**
	 * A map with all LocationRectangles.
	 */
	private Map<Location, LocationRectangle> rectangles = new HashMap<>();

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

	/**
	 * Handler for pressing (start drag) on a LocationRectangle.
	 */
	private EventHandler<MouseEvent> pressHandler = (t) -> {
		orgSceneX = t.getSceneX();
		orgSceneY = t.getSceneY();
		orgX = ((LocationRectangle) (t.getSource())).getLayoutX();
		orgY = ((LocationRectangle) (t.getSource())).getLayoutY();
	};

	/**
	 * Handler for dragging a LocationRectangle.
	 */
	private EventHandler<MouseEvent> dragHandler = (t) -> {
		LocationRectangle r = ((LocationRectangle) (t.getSource()));

		double offsetX = t.getSceneX() - orgSceneX;
		double newX = orgX + offsetX;
		if (newX >= 0) {
			r.setLayoutX(newX);
			r.location.setxCoordinate(newX);
		}

		double offsetY = t.getSceneY() - orgSceneY;
		double newY = orgY + offsetY;
		if (newY >= 0) {
			r.setLayoutY(newY);
			r.location.setyCoordinate(newY);
		}
	};

	/**
	 * Handler for clicking a LocationRectangle.
	 */
	private EventHandler<MouseEvent> clickHandler = (t) -> {
		LocationRectangle r = ((LocationRectangle) (t.getSource()));
		if (t.getClickCount() == 2) {
			locationSelected(r.location);
		}
	};

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

		initializeMap();
	}

	@Override
	protected List<Way> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getWayManager().getAllWays();
	}

	@Override
	protected Way createNewObject(String name) {
		return new Way(name, newDescriptionTA.getText(), newOriginChooser.getObjectValue(),
				newDestinationChooser.getObjectValue());
	}

	@Override
	protected GameDataController getObjectController(Way selectedObject) {
		return new WayController(currentGameManager, mwController, selectedObject);
	}

	// TODO

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
		LocationRectangle sp = new LocationRectangle();

		// The rectangle
		Rectangle rectangle = new Rectangle(rectDim, rectDim);
		rectangle.setFill(Color.LIGHTGRAY);
		rectangle.setStroke(Color.BLACK);

		// The label
		Label label = new Label(location.toString());

		// Placing the node
		sp.setLayoutX(location.getxCoordinate());
		sp.setLayoutY(location.getyCoordinate());
		sp.setMaxSize(rectDim, rectDim);

		// Handling dragging
		sp.setOnMousePressed(pressHandler);
		sp.setOnMouseDragged(dragHandler);

		// Handling clicking
		sp.setOnMouseClicked(clickHandler);

		sp.location = location;
		sp.getChildren().addAll(rectangle, label);

		// Save in map
		rectangles.put(location, sp);

		return sp;
	}

	/**
	 * Creates a line Line to represent a Way
	 * 
	 * TODO special class for WayLine, collection to manage, no dups returned
	 * here.
	 * 
	 * TODO on click line, display a popup (if more than one) that lets
	 * you select which way to edit.
	 * 
	 * @param w
	 *            the Way
	 * @return the Line
	 */
	private Line createWayNode(Way w) {
		// TODO no dups
		Line line = new Line();

		line.startXProperty().bind(rectangles.get(w.getOrigin()).centerX);
		line.startYProperty().bind(rectangles.get(w.getOrigin()).centerY);
		line.endXProperty().bind(rectangles.get(w.getDestination()).centerX);
		line.endYProperty().bind(rectangles.get(w.getDestination()).centerY);

		return line;
	}

	/**
	 * Initializes the MapView.
	 * 
	 * TODO possibility to add new Ways here.
	 */
	private void initializeMap() {

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

			mapPane.getChildren().add(line);
			line.toBack();
		}
	}

}
