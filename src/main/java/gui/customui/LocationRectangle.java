package gui.customui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import data.Location;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * A special StackPane for displaying locations as a rectangle with their name
 * inside.
 * 
 * @author Satia
 */
public class LocationRectangle extends StackPane {

	/**
	 * The height and width of LocationRectangles.
	 */
	private static final double RECT_DIM = 120;

	// These values are used by the dragHandlers to position rectangles
	// correctly
	private double orgSceneX;
	private double orgSceneY;
	private double orgX;
	private double orgY;

	// If the rectangle is currently being dragged
	private boolean dragging = false;

	/**
	 * Executed when a location is chosen for editing
	 */
	private Consumer<Location> locationChosen;

	/**
	 * Executed when the rectangle is clicked as the origin when creating a new
	 * way.
	 */
	private BiConsumer<Location, Line> rectangleAsOrigin;

	/**
	 * Executed when the rectangle is clicked as the destination when creating a
	 * new way.
	 */
	private Consumer<Location> rectangleAsDestination;

	/**
	 * The location of this rectangle
	 */
	private Location location;

	/**
	 * The center on the X axis.
	 */
	private DoubleBinding centerX = layoutXProperty().add(RECT_DIM / 2);

	/**
	 * The center on the Y axis.
	 */
	private DoubleBinding centerY = layoutYProperty().add(RECT_DIM / 2);

	/**
	 * Handler for clicking or dragging a LocationRectangle.
	 */
	private EventHandler<MouseEvent> clickHandler = (t) -> {
		if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
			dragging = false;

			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgX = getLayoutX();
			orgY = getLayoutY();
		} else if (t.getEventType() == MouseEvent.DRAG_DETECTED) {
			dragging = true;
		} else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			double offsetX = t.getSceneX() - orgSceneX;
			double newX = orgX + offsetX;
			if (newX >= 0) {
				setLayoutX(newX);
				location.setxCoordinate(newX);
			}

			double offsetY = t.getSceneY() - orgSceneY;
			double newY = orgY + offsetY;
			if (newY >= 0) {
				setLayoutY(newY);
				location.setyCoordinate(newY);
			}
		} else if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (!dragging) {
				locationChosen.accept(location);
			}
		}
	};

	/**
	 * Handler for clicking a LocationRectangle when creating a way.
	 */
	private EventHandler<MouseEvent> chooseOriginClickHandler = (t) -> {
		if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
			addShadowStyle();
			Line line = new Line();
			line.setStartX(centerX.get());
			line.setStartY(centerY.get());
			line.setEndX(centerX.get());
			line.setEndY(centerY.get());
			line.setStrokeWidth(5);
			rectangleAsOrigin.accept(location, line);
			t.consume();
		}
	};

	/**
	 * Handler for clicking a LocationRectangle when creating a way.
	 */
	private EventHandler<MouseEvent> chooseDestinationClickHandler = (t) -> {
		if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
			addShadowStyle();
			rectangleAsDestination.accept(location);
			t.consume();
		}
	};

	/**
	 * Create a new LocationRectangle
	 * 
	 * @param location
	 *            the location
	 * @param locationChosen
	 *            executed when a location is chosen for editing
	 * @param rectangleAsOrigin
	 *            executed when the rectangle is clicked as the origin when
	 *            creating a new way.
	 * @param rectangleAsDestination
	 *            executed when the rectangle is clicked as the destination when
	 *            creating a new way.
	 */
	public LocationRectangle(Location location, Consumer<Location> locationChosen,
			BiConsumer<Location, Line> rectangleAsOrigin, Consumer<Location> rectangleAsDestination) {
		this.location = location;
		this.locationChosen = locationChosen;
		this.rectangleAsOrigin = rectangleAsOrigin;
		this.rectangleAsDestination = rectangleAsDestination;

		// The rectangle
		Rectangle rectangle = new Rectangle(RECT_DIM, RECT_DIM);
		rectangle.setFill(Color.LIGHTGRAY);
		rectangle.setStroke(Color.BLACK);

		// The label
		Label label = new Label(location.toString());

		// Placing the node
		setLayoutX(location.getxCoordinate());
		setLayoutY(location.getyCoordinate());
		setMaxSize(RECT_DIM, RECT_DIM);

		getChildren().addAll(rectangle, label);

		enterRearrangeMode();
	}

	/**
	 * In this mode, the rectangles cannot be rearranged, but by clicking them a
	 * new Way can be created. For choosing the origin.
	 */
	public void enterOriginChooseMode() {
		setEventHandler(MouseEvent.ANY, chooseOriginClickHandler);
		setCursor(Cursor.CROSSHAIR);
	}

	/**
	 * In this mode, the rectangles cannot be rearranged, but by clicking them a
	 * new Way can be created. For choosing the destination.
	 */
	public void enterDestinationChooseMode() {
		setEventHandler(MouseEvent.ANY, chooseDestinationClickHandler);
	}

	/**
	 * This is the default mode.
	 */
	public void enterRearrangeMode() {
		setEventHandler(MouseEvent.ANY, clickHandler);
		setCursor(Cursor.DEFAULT);
		removeShadowStyle();
		addHoverStyle();
	}

	/**
	 * This is the mode when clicks do not have an effect on LocationRectangles.
	 */
	public void enterInactiveMode() {
		setEventHandler(MouseEvent.ANY, null);
		removeHoverStyle();
	}

	/**
	 * Adds the hover shadow effect.
	 */
	private void addHoverStyle() {
		if (!getStyleClass().contains("mapelement")) {
			getStyleClass().add("mapelement");
		}
	}

	/**
	 * Removes the hover shadow effect.
	 */
	private void removeHoverStyle() {
		getStyleClass().remove("mapelement");
	}

	/**
	 * Adds the permanent shadow effect.
	 */
	private void addShadowStyle() {
		if (!getStyleClass().contains("selectedmapelement")) {
			getStyleClass().add("selectedmapelement");
		}
	}

	/**
	 * Removes the permanent shadow effect.
	 */
	private void removeShadowStyle() {
		getStyleClass().remove("selectedmapelement");
	}

	/**
	 * @return the centerX
	 */
	public DoubleBinding getCenterX() {
		return centerX;
	}

	/**
	 * @return the centerY
	 */
	public DoubleBinding getCenterY() {
		return centerY;
	}
}