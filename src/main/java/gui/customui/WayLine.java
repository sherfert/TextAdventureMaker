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
import javafx.scene.shape.Line;

/**
 * A special Line for displaying ways connecting LocationRectangles.
 * 
 * XXX WayLine does not account for self-loop edges.
 * 
 * @author Satia
 */
public class WayLine extends Line {

	/**
	 * Default row height of cells in a ListView
	 */
	private final int ROW_HEIGHT = 24;

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
		WayLine l = ((WayLine) (t.getSource()));

		if (t.getClickCount() == 2) {
			if (l.ways.size() == 1) {
				wayChosen.accept(l.ways.get(0));
			} else {
				// Create a listView
				ListView<Way> listView = new ListView<>();
				listView.setItems(FXCollections.observableArrayList(ways));
				listView.setPrefHeight(ways.size() * ROW_HEIGHT + 2);

				// Create the popup
				PopOver popOver = new PopOver(listView);

				// On click of listview items
				listView.setOnMouseClicked((e) -> {
					if (e.getClickCount() == 2) {
						wayChosen.accept(listView.getSelectionModel().getSelectedItem());
						// Hide popup
						popOver.hide();
					}
				});

				// Show the popup
				popOver.setDetachable(false);
				popOver.show(l);
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

		startXProperty().bind(originRect.getCenterX());
		startYProperty().bind(originRect.getCenterY());
		endXProperty().bind(destinationRect.getCenterX());
		endYProperty().bind(destinationRect.getCenterY());

		// Thicker lines
		setStrokeWidth(5);

		// Handling clicking
		setOnMouseClicked(clickHandler);

		// Styling
		addHoverStyle();
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
	 * Adds a way to the list of ways managed by this line.
	 * 
	 * @param w
	 */
	public void addWay(Way w) {
		ways.add(w);
	}
}