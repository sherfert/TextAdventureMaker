package gui.toplevel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import data.Location;
import data.Way;
import exception.DBClosedException;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.LocationRectangle;
import gui.customui.WayLine;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import logic.CurrentGameManager;

/**
 * A graphical overview of the entities in the game in form of a map.
 * 
 * TODO Include location creation in map. Better way/location creation in map.
 * 
 * @author satia
 *
 */
public class OverviewController extends GameDataController {

	/**
	 * A map with all LocationRectangles.
	 */
	private Map<Location, LocationRectangle> rectangles = new HashMap<>();

	/**
	 * A map with all WayLines. Maps a list of origin and destination to the
	 * line.
	 */
	private Map<List<Location>, WayLine> lines = new HashMap<>();

	/**
	 * During way creation in the map, the origin.
	 */
	private Location creationOrigin;

	/**
	 * During way creation in the map, the destination.
	 */
	private Location creationDestination;

	/**
	 * During way creation in the map, the line.
	 */
	private Line creationLine;

	@FXML
	private Pane mapPane;

	@FXML
	private BorderPane mapBorderPane;

	@FXML
	private Button mapNewWayButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public OverviewController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	protected void initialize() {
		// Empty the maps
		rectangles.clear();
		lines.clear();

		// Obtain all objects to display
		List<Location> locations;
		List<Way> ways;
		try {
			locations = currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();
			ways = currentGameManager.getPersistenceManager().getWayManager().getAllWays();
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
		for (Way w : ways) {
			addWayToMap(w);
		}

		// The add way button
		mapNewWayButton.setOnMouseClicked((e) -> {
			enableWayCreation();
		});
	}

	@Override
	public boolean isObsolete() {
		return false;
	}

	/**
	 * Creates a new LocationRectangle, and saves it in the rectangles Map.
	 * 
	 * @param location
	 *            the location
	 * @return the LocationRectangle
	 */
	private LocationRectangle createLocationNode(Location location) {
		LocationRectangle sp = new LocationRectangle(location, this::objectSelected, this::originChosen,
				this::destinationChosen);
		// Save in map
		rectangles.put(location, sp);

		return sp;
	}
	
	/**
	 * Adds a new way to the Map
	 * 
	 * @param way
	 *            the way
	 */
	private void addWayToMap(Way way) {
		WayLine line = createWayNode(way);
		if (line != null) {
			mapPane.getChildren().add(line);
			// Place lines behind rectangles
			line.toBack();
		}
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
			line = new WayLine(w, rectangles.get(w.getOrigin()), rectangles.get(w.getDestination()),
					this::objectSelected);
			lines.put(endpoints, line);
			return line;
		} else {
			// Save the new way in the lines list
			line.addWay(w);
			return null;
		}
	}

	/**
	 * Enables the creation of a way in the MapView. This changes the behavior
	 * of the rectangles.
	 */
	private void enableWayCreation() {
		// Disable the button.
		mapNewWayButton.setDisable(true);
		// Change the cursors
		mapBorderPane.setCursor(Cursor.HAND);
		for (LocationRectangle lr : rectangles.values()) {
			lr.setCursor(Cursor.CROSSHAIR);
			// the rectangles enter origin choose mode
			lr.enterOriginChooseMode();
		}
		// Disable the hover styles
		for (WayLine wl : lines.values()) {
			wl.removeHoverStyle();
		}
		// If clicked outside a rectangle, disable the creation
		mapBorderPane.setOnMouseClicked((e) -> {
			disableWayCreation();
		});
	}

	/**
	 * Called when the first rectangle was clicked as part of the way creation
	 * process in the MapView.
	 * 
	 * @param origin
	 *            the origin of the way being created
	 * @param line
	 *            the line that serves as visual feedback
	 */
	private void originChosen(Location origin, Line line) {
		this.creationOrigin = origin;
		this.creationLine = line;

		// Display the line
		mapPane.getChildren().add(line);
		line.toBack();

		// Make the line follow the mouse
		mapBorderPane.setOnMouseMoved((e) -> {
			line.setEndX(e.getX());
			line.setEndY(e.getY());
		});

		// The rectangles enter destination choose mode
		for (LocationRectangle lr : rectangles.values()) {
			lr.enterDestinationChooseMode();
		}
	}

	/**
	 * Called when the second rectangle was clicked as part of the way creation
	 * process in the MapView.
	 * 
	 * @param destination
	 *            the destination of the way being created
	 */
	private void destinationChosen(Location destination) {
		this.creationDestination = destination;
		// Create the way with a default name
		Way way = new Way(this.creationOrigin.getName() + "->" + this.creationDestination.getName(), "",
				this.creationOrigin, this.creationDestination);
		// Save way
		try {
			saveHasId(way);
			addWayToMap(way);
			// Open new way for editing
			objectSelected(way);
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed", e);
		}
		
	}

	/**
	 * Disables the creation of a way in the MapView again and returns to normal
	 * behavior.
	 */
	private void disableWayCreation() {
		// Undo everything
		mapNewWayButton.setDisable(false);
		mapBorderPane.setCursor(Cursor.DEFAULT);
		for (LocationRectangle lr : rectangles.values()) {
			lr.setCursor(Cursor.DEFAULT);
			lr.enterRearrangeMode();
		}
		for (WayLine wl : lines.values()) {
			wl.addHoverStyle();
		}
		mapBorderPane.setOnMouseClicked(null);
		if (creationLine != null) {
			mapPane.getChildren().remove(creationLine);
		}
	}

}
