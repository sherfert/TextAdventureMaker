package gui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.NamedObject;
import exception.DBClosedException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the a view with NamedObjects in a table, and the
 * possibility to add them.
 * 
 * TODO in this and in similar views: A filter text field
 * 
 * @author Satia
 */
public abstract class NamedObjectsController<E extends NamedObject> extends GameDataController {

	/**
	 * The FMXL for controllers to edit single objects of type E.
	 */
	private String objectControllerFXML;

	/** An observable list with the objects. */
	protected ObservableList<E> objectsOL;

	@FXML
	protected TableView<E> table;

	@FXML
	protected TableColumn<E, Integer> idCol;

	@FXML
	protected TableColumn<E, String> nameCol;

	@FXML
	protected TextField newNameTF;

	@FXML
	protected Button saveButton;

	/**
	 * 
	 * @param currentGameManager
	 *            the current game manager
	 * @param mwController
	 *            the main window controller
	 * @param objectControllerFXML
	 *            the FMXL for controllers to edit single objects of type E
	 */
	public NamedObjectsController(CurrentGameManager currentGameManager, MainWindowController mwController,
			String objectControllerFXML) {
		super(currentGameManager, mwController);
		this.objectControllerFXML = objectControllerFXML;
	}

	@FXML
	protected void initialize() {
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());

		// A listener for row double-clicks
		table.setRowFactory(tv -> {
			TableRow<E> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					objectSelected(row.getItem());
				}
			});
			return row;
		});

		// Get all objects and store in observable list, unless the list is
		// already propagated
		if (objectsOL == null) {
			try {
				objectsOL = FXCollections.observableArrayList(getAllObjects());
			} catch (DBClosedException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
				return;
			}
		}

		// Fill table
		table.setItems(objectsOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);
		
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewObject());
	}

	@Override
	public void update() {
		if (objectsOL != null) {
			try {
				objectsOL.setAll(getAllObjects());
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
				return;
			}
		}
	}
	
	

	/**
	 * Saves a new object to both DB and table.
	 */
	private void saveNewObject() {
		E e = createNewObject(newNameTF.getText());
		// Add item to DB
		try {
			currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(e);
		} catch (DBClosedException e1) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}
		currentGameManager.getPersistenceManager().updateChanges();
		// Add item to our table
		objectsOL.add(e);

		resetFormValues();
	}
	
	/**
	 * Reset all form values. Should be overridden to account for additional ones.
	 */
	protected void resetFormValues() {
		newNameTF.setText("");
	}

	/**
	 * Opens this object for editing.
	 * 
	 * @param o
	 *            the object
	 */
	private void objectSelected(E e) {
		if (e == null) {
			return;
		}

		GameDataController c = getObjectController(e);
		mwController.pushCenterContent(e.getName(), objectControllerFXML, c, c::controllerFactory);
	}

	/**
	 * Get a list of all objects to display in the table
	 * 
	 * @return the objects.
	 * @throws DBClosedException
	 *             if the DB was closed.
	 */
	protected abstract List<E> getAllObjects() throws DBClosedException;

	/**
	 * Create a new Object of type E.
	 * 
	 * @param name
	 *            the name for the new object
	 * @return the new object.
	 */
	protected abstract E createNewObject(String name);

	/**
	 * Create a controller to edit the single given object of type E.
	 * 
	 * @param selectedObject
	 *            the object to edit.
	 * @return the controller.
	 */
	protected abstract GameDataController getObjectController(E selectedObject);

}
