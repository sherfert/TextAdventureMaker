package gui;

import java.util.List;
import java.util.function.Supplier;

import data.Way;
import exception.DBClosedException;
import gui.custumui.LocationChooser;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
 * TODO MapView (as another tab)
 * 
 * @author Satia
 */
public class WaysController extends NamedObjectsController<Way> {

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
	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;

	private void initializeMap() {
		Rectangle rectangle = new Rectangle(100, 100, Color.RED);
		rectangle.setTranslateX(70);
		rectangle.setTranslateY(70);
		DoubleBinding rCenterX = rectangle.translateXProperty().add(rectangle.getWidth() / 2);
		DoubleBinding rCenterY = rectangle.translateYProperty().add(rectangle.getHeight() / 2);

		Rectangle rectangle2 = new Rectangle(100, 100, Color.BLUE);
		rectangle2.setTranslateX(100);
		rectangle2.setTranslateY(400);
		DoubleBinding r2CenterX = rectangle2.translateXProperty().add(rectangle2.getWidth() / 2);
		DoubleBinding r2CenterY = rectangle2.translateYProperty().add(rectangle2.getHeight() / 2);
		
		Line line = new Line();
		
		line.startXProperty().bind(rCenterX);
		line.startYProperty().bind(rCenterY);
		line.endXProperty().bind(r2CenterX);
		line.endYProperty().bind(r2CenterY);

		mapPane.getChildren().addAll(line, rectangle, rectangle2);

		EventHandler<MouseEvent> pressHandler = (t) -> {
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgTranslateX = ((Rectangle) (t.getSource())).getTranslateX();
			orgTranslateY = ((Rectangle) (t.getSource())).getTranslateY();
		};

		EventHandler<MouseEvent> dragHandler = (t) -> {
			Rectangle r = ((Rectangle) (t.getSource()));

			double offsetX = t.getSceneX() - orgSceneX;
			double newTranslateX = orgTranslateX + offsetX;
			if (newTranslateX >= 0 && newTranslateX + r.getWidth() <= mapPane.getWidth()) {
				r.setTranslateX(newTranslateX);
			}

			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateY = orgTranslateY + offsetY;
			if (newTranslateY >= 0 && newTranslateY + r.getHeight() <= mapPane.getHeight()) {
				r.setTranslateY(newTranslateY);
			}
		};

		rectangle.setOnMousePressed(pressHandler);
		rectangle.setOnMouseDragged(dragHandler);
		
		rectangle2.setOnMousePressed(pressHandler);
		rectangle2.setOnMouseDragged(dragHandler);
	}

}
