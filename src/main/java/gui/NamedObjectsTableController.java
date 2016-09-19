package gui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.NamedObject;
import exception.DBClosedException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the a view with NamedObjects in a table.
 * 
 * @author Satia
 */
public abstract class NamedObjectsTableController<E extends NamedObject> extends GameDataController {

	/** An observable list with the objects. */
	protected ObservableList<E> objectsOL;
	
	@FXML
	protected TableView<E> table;
	
	@FXML
	protected TableColumn<E, Integer> idCol;
	
	@FXML
	protected TableColumn<E, String> nameCol;
	
	@FXML
	protected TextField filterTF;

	public NamedObjectsTableController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
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

		try {
			if (objectsOL == null) {
				objectsOL = FXCollections.observableArrayList(getAllObjects());
			} else {
				objectsOL.setAll(getAllObjects());
			}
		} catch (DBClosedException e1) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}

		// Filter
		FilteredList<E> filteredData = new FilteredList<>(objectsOL, p -> true);

		filterTF.textProperty().addListener((f, o, n) -> {
			filteredData.setPredicate(obj -> {
				// If filter text is empty, display all objects.
				if (n == null || n.isEmpty()) {
					return true;
				}

				// Compare name of every E with filter text.
				return obj.getName().toLowerCase().contains(n.toLowerCase());
			});
		});

		// Sort filtered data
		SortedList<E> sortedData = new SortedList<>(filteredData);

		// Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(table.comparatorProperty());

		// Fill table
		table.setItems(sortedData);
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
	 * Opens this object for editing.
	 * 
	 * @param e
	 *            the object
	 */
	protected abstract void objectSelected(E e);

}