package gui;

import data.NamedDescribedObject;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for a view with NamedDescribedObjects in a table, and the
 * possibility to add them.
 * 
 * @author Satia
 */
public abstract class NamedDescribedObjectsController<E extends NamedDescribedObject> extends NamedObjectsController<E> {

	@FXML
	protected TableColumn<E, String> descriptionCol;

	@FXML
	protected TextArea newDescriptionTA;

	/**
	 * 
	 * @param currentGameManager
	 *            the current game manager
	 * @param mwController
	 *            the main window controller
	 * @param objectControllerFXML
	 *            the FMXL for controllers to edit single objects of type E
	 */
	public NamedDescribedObjectsController(CurrentGameManager currentGameManager, MainWindowController mwController,
			String objectControllerFXML) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		descriptionCol.setCellValueFactory((p) -> p.getValue().descriptionProperty());
		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
	}
	
	@Override
	protected void resetFormValues() {
		super.resetFormValues();
		newDescriptionTA.setText("");
	}
	
	@Override
	protected E createNewObject(String name) {
		return createNewObject(name, newDescriptionTA.getText());
	}

	/**
	 * Create a new Object of type E.
	 * 
	 * @param name
	 *            the name for the new object
	 * @param description
	 *            the description for the new object
	 * @return the new object.
	 */
	protected abstract E createNewObject(String name, String description);

}
