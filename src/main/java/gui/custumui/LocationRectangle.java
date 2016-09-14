package gui.custumui;

import java.util.function.Consumer;

import data.Location;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
			locationChosen.accept(r.location);
		}
	};

	/**
	 * Create a new LocationRectangle
	 * 
	 * @param location
	 *            the location
	 * 
	 * @param locationChosen
	 *            executed when a location is chosen for editing
	 */
	public LocationRectangle(Location location, Consumer<Location> locationChosen) {
		this.location = location;
		this.locationChosen = locationChosen;

		// Handling dragging
		setOnMousePressed(pressHandler);
		setOnMouseDragged(dragHandler);

		// Handling clicking
		setOnMouseClicked(clickHandler);

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
		getStyleClass().add("mapelement");
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