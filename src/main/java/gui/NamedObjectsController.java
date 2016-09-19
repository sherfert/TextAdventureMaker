package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.NamedObject;
import exception.DBClosedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the a view with NamedObjects in a table, and the possibility
 * to add them.
 * 
 * @author Satia
 */
public abstract class NamedObjectsController<E extends NamedObject> extends NamedObjectsTableController<E> {

	/**
	 * The FMXL for controllers to edit single objects of type E.
	 */
	private String objectControllerFXML;

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
		super.initialize();
		// Disable buttons at beginning
		saveButton.setDisable(true);
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewObject());
	}

	/**
	 * Saves any named object
	 * 
	 * @param o
	 *            the object
	 * @throws DBClosedException
	 *             if the DB was closed.
	 */
	protected void saveObject(NamedObject o) throws DBClosedException {
		currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(o);
		currentGameManager.getPersistenceManager().updateChanges();
	}

	/**
	 * Saves a new object to both DB and table.
	 */
	private void saveNewObject() {
		E e = createNewObject(newNameTF.getText());
		// Add item to DB
		try {
			saveObject(e);
		} catch (DBClosedException e1) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}
		// Add item to our table
		objectsOL.add(e);

		resetFormValues();
	}

	/**
	 * Reset all form values. Should be overridden to account for additional
	 * ones.
	 */
	protected void resetFormValues() {
		newNameTF.setText("");
	}

	/**
	 * Opens this object for editing.
	 * 
	 * @param e
	 *            the object
	 */
	@Override
	protected void objectSelected(E e) {
		if (e == null) {
			return;
		}

		GameDataController c = getObjectController(e);
		mwController.pushCenterContent(e.getName(), objectControllerFXML, c, c::controllerFactory);
	}

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
