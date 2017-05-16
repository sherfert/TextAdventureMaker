package gui;

import data.NamedDescribedObject;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import logic.CurrentGameManager;

/**
 * Controller for a view with NamedDescribedObjects in a table, and the
 * possibility to add them.
 * 
 * @author Satia
 */
public abstract class NamedDescribedObjectsController<E extends NamedDescribedObject> extends NamedObjectsTableController<E> {

	@FXML
	protected TableColumn<E, String> descriptionCol;

	/**
	 * 
	 * @param currentGameManager
	 *            the current game manager
	 * @param mwController
	 *            the main window controller
	 */
	public NamedDescribedObjectsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		descriptionCol.setCellValueFactory((p) -> p.getValue().descriptionProperty());
	}

}
