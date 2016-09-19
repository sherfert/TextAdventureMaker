package gui.toplevel;

import java.util.List;

import data.action.AbstractAction;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsTableController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import logic.CurrentGameManager;

/**
 * Controller for the actions view.
 * 
 * @author Satia
 */
public class ActionsController extends NamedObjectsTableController<AbstractAction> {

	@FXML
	private TableColumn<AbstractAction, String> typeCol;

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
		typeCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<String>(p.getValue().getClass().getSimpleName()));
		summaryCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<String>(p.getValue().actionDescription()));

		// Allow wrapped text for the summary column
		summaryCol.setCellFactory((p) -> {
			TableCell<AbstractAction, String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			text.wrappingWidthProperty().bind(cell.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
	}

	@Override
	protected List<AbstractAction> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getActionManager().getAllActions();
	}

}
