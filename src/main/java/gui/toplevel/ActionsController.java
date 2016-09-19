package gui.toplevel;

import java.util.List;

import data.action.AbstractAction;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsTableController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import logic.CurrentGameManager;

/**
 * Controller for the actions view.
 * 
 * @author Satia
 */
public class ActionsController extends NamedObjectsTableController<AbstractAction> {
	
	@FXML
	private TableColumn<AbstractAction, String> summaryCol;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public ActionsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		summaryCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<String>(p.getValue().actionDescription()));
	}

	@Override
	protected List<AbstractAction> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getActionManager().getAllActions();
	}

	@Override
	protected void objectSelected(AbstractAction e) {
		// TODO Auto-generated method stub
	}

}
