package gui.customui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.controlsfx.control.PopOver;

import data.Way;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * A special Line for displaying ways connecting LocationRectangles.
 * 
 * @author Satia
 */
public class WayLine extends Path {

	/**
	 * Default row height of cells in a ListView
	 */
	private final int ROW_HEIGHT = 24;

	/*
	 * The distance for self-loop-edges
	 */
	private static final double SELFLOOP_EDGE_DIST = 100;

	/**
	 * The ways represented by this line
	 */
	private List<Way> ways;

	/**
	 * Executed when a way is chosen for editing
	 */
	private Consumer<Way> wayChosen;

	/**
	 * Handler for clicking a WayLine.
	 */
	private EventHandler<MouseEvent> clickHandler = (t) -> {
		if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (ways.size() == 1) {
				wayChosen.accept(ways.get(0));
			} else {
				// Create a listView
				ListView<Way> listView = new ListView<>();
				listView.setItems(FXCollections.observableArrayList(ways));
				listView.setPrefHeight(ways.size() * ROW_HEIGHT + 2);

				// Create the popup
				PopOver popOver = new PopOver(listView);

				// On click of listview items
				listView.setOnMouseClicked((e) -> {
					wayChosen.accept(listView.getSelectionModel().getSelectedItem());
					// Hide popup
					popOver.hide();
				});

				// Show the popup
				popOver.setDetachable(false);
				popOver.show(this);
			}
		}
	};

	/**
	 * Creates a new WayLine
	 * 
	 * @param w
	 *            the way
	 * @param originRect
	 *            the rectangle for the origin of the passed way
	 * @param destinationRect
	 *            the rectangle for the destination of the passed way
	 * @param wayChosen
	 *            executed when a way is chosen for editing
	 */
	public WayLine(Way w, LocationRectangle originRect, LocationRectangle destinationRect, Consumer<Way> wayChosen) {
		this.wayChosen = wayChosen;
		ways = new ArrayList<>();
		ways.add(w);

		if (originRect == destinationRect) {
			// Self-loop edge
			MoveTo startPoint = new MoveTo();
			startPoint.xProperty().bind(originRect.getCenterX());
			startPoint.yProperty().bind(originRect.getCenterY());

			LineTo up = new LineTo();
			up.xProperty().bind(originRect.getCenterX());
			up.yProperty().bind(originRect.getCenterY().subtract(SELFLOOP_EDGE_DIST));

			LineTo right = new LineTo();
			right.xProperty().bind(originRect.getCenterX().add(SELFLOOP_EDGE_DIST));
			right.yProperty().bind(originRect.getCenterY().subtract(SELFLOOP_EDGE_DIST));

			LineTo down = new LineTo();
			down.xProperty().bind(originRect.getCenterX().add(SELFLOOP_EDGE_DIST));
			down.yProperty().bind(originRect.getCenterY());

			LineTo end = new LineTo();
			end.xProperty().bind(originRect.getCenterX());
			end.yProperty().bind(originRect.getCenterY());

			getElements().add(startPoint);
			getElements().add(up);
			getElements().add(right);
			getElements().add(down);
			getElements().add(end);
		} else {
			MoveTo startPoint = new MoveTo();
			startPoint.xProperty().bind(originRect.getCenterX());
			startPoint.yProperty().bind(originRect.getCenterY());
			LineTo endPoint = new LineTo();
			endPoint.xProperty().bind(destinationRect.getCenterX());
			endPoint.yProperty().bind(destinationRect.getCenterY());

			getElements().add(startPoint);
			getElements().add(endPoint);
		}

		// Thicker lines
		setStrokeWidth(5);

		enterStandardMode();
	}
	
	/**
	 * The standard mode where clicking opens the way details.
	 */
	public void enterStandardMode() {
		setEventHandler(MouseEvent.ANY, clickHandler);
		addHoverStyle();
	}

	/**
	 * This is the mode when clicks do not have an effect.
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
	 * Adds a way to the list of ways managed by this line.
	 * 
	 * @param w
	 */
	public void addWay(Way w) {
		ways.add(w);
	}
}