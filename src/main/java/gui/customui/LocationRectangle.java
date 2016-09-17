package gui.customui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import data.Location;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
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
	private static final double rectDim = 120;

	// These values are used by the dragHandlers to position rectangles
	// correctly
	private double orgSceneX;
	private double orgSceneY;
	private double orgX;
	private double orgY;

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
	private DoubleBinding centerX = layoutXProperty().add(rectDim / 2);

	/**
	 * The center on the Y axis.
	 */
	private DoubleBinding centerY = layoutYProperty().add(rectDim / 2);

	/**
	 * Handler for pressing (start drag) on a LocationRectangle.
	 */
	private EventHandler<MouseEvent> pressHandler = (t) -> {
		orgSceneX = t.getSceneX();
		orgSceneY = t.getSceneY();
		orgX = getLayoutX();
		orgY = getLayoutY();
	};

	/**
	 * Handler for dragging a LocationRectangle.
	 */
	private EventHandler<MouseEvent> dragHandler = (t) -> {
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
	};

	/**
	 * Handler for clicking a LocationRectangle.
	 */
	private EventHandler<MouseEvent> clickHandler = (t) -> {
		if (t.getClickCount() == 2) {
			locationChosen.accept(location);
		}
	};

	/**
	 * Handler for clicking a LocationRectangle when creating a way.
	 */
	private EventHandler<MouseEvent> chooseOriginClickHandler = (t) -> {
		addShadowStyle();
		Line line = new Line();
		line.setStartX(centerX.get());
		line.setStartY(centerY.get());
		line.setEndX(centerX.get());
		line.setEndY(centerY.get());
		line.setStrokeWidth(5);
		rectangleAsOrigin.accept(location, line);
		t.consume();
	};

	/**
	 * Handler for clicking a LocationRectangle when creating a way.
	 */
	private EventHandler<MouseEvent> chooseDestinationClickHandler = (t) -> {
		addShadowStyle();
		rectangleAsDestination.accept(location);
		t.consume();
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

		enterRearrangeMode();

		// The rectangle
		Rectangle rectangle = new Rectangle(rectDim, rectDim);
		rectangle.setFill(Color.LIGHTGRAY);
		rectangle.setStroke(Color.BLACK);

		// The label
		Label label = new Label(location.toString());

		// Placing the node
		setLayoutX(location.getxCoordinate());
		setLayoutY(location.getyCoordinate());
		setMaxSize(rectDim, rectDim);

		getChildren().addAll(rectangle, label);

		// Styling
		addHoverStyle();
	}

	/**
	 * In this mode, the rectangles cannot be rearranged, but by clicking them a
	 * new Way can be created. For choosing the origin.
	 */
	public void enterOriginChooseMode() {
		setOnMousePressed(null);
		setOnMouseDragged(null);
		setOnMouseClicked(chooseOriginClickHandler);
	}

	/**
	 * In this mode, the rectangles cannot be rearranged, but by clicking them a
	 * new Way can be created. For choosing the destination.
	 */
	public void enterDestinationChooseMode() {
		setOnMousePressed(null);
		setOnMouseDragged(null);
		setOnMouseClicked(chooseDestinationClickHandler);
	}

	/**
	 * This is the default mode.
	 */
	public void enterRearrangeMode() {
		setOnMousePressed(pressHandler);
		setOnMouseDragged(dragHandler);
		setOnMouseClicked(clickHandler);
		removeShadowStyle();
	}

	/**
	 * Adds the hover shadow effect.
	 */
	public void addHoverStyle() {
		getStyleClass().add("mapelement");
	}

	/**
	 * Removes the hover shadow effect.
	 */
	public void removeHoverStyle() {
		getStyleClass().remove("mapelement");
	}

	/**
	 * Adds the permanent shadow effect.
	 */
	public void addShadowStyle() {
		if (!getStyleClass().contains("selectedmapelement")) {
			getStyleClass().add("selectedmapelement");
		}
	}

	/**
	 * Removes the permanent shadow effect.
	 */
	public void removeShadowStyle() {
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