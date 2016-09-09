package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Item;
import exception.DBClosedException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for the items view.
 * 
 * TODO in this and in similar views: A filter text field
 * 
 * @author Satia
 */
public class ItemsController extends GameDataController {

	/** An observable list with the items. */
	private ObservableList<Item> itemsOL;

	@FXML
	private TableView<Item> table;

	@FXML
	private TableColumn<Item, Integer> idCol;

	@FXML
	private TableColumn<Item, String> nameCol;

	@FXML
	private TableColumn<Item, String> descriptionCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	public ItemsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	private void initialize() {
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());
		descriptionCol.setCellValueFactory((p) -> p.getValue().descriptionProperty());
		
		// A listener for row double-clicks
		table.setRowFactory(tv -> {
			TableRow<Item> row = new TableRow<>();
			row.setOnMouseClicked(event ->  {
				if(event.getClickCount() == 2) {
					itemSelected(row.getItem());
				}
			});
			return row;
		});

		// Get all items and store in observable list, unless the list is
		// already propagated
		if (itemsOL == null) {
			try {
				itemsOL = FXCollections.observableArrayList(
						currentGameManager.getPersistenceManager().getItemManager().getAllItems());
			} catch (DBClosedException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Abort: DB closed");
				return;
			}
		}

		// Fill table
		table.setItems(itemsOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewItem());
	}
	
	@Override
	public void update() {
		if (itemsOL != null) {
			try {
				itemsOL.setAll(currentGameManager.getPersistenceManager().getItemManager().getAllItems());
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Abort: DB closed");
				return;
			}
		}
	}

	/**
	 * Saves a new item to both DB and table.
	 */
	private void saveNewItem() {
		Item i = new Item(newNameTF.getText(), newDescriptionTA.getText());
		// Add item to DB
		try {
			currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(i);
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Abort: DB closed");
			return;
		}
		currentGameManager.getPersistenceManager().updateChanges();
		// Add item to our table
		itemsOL.add(i);

		// Reset the form values
		newNameTF.setText("");
		newDescriptionTA.setText("");
	}
	
	/**
	 * Opens this item for editing.
	 * 
	 * @param i
	 *            the item
	 */
	private void itemSelected(Item i) {
		if (i == null) {
			return;
		}

		// Open the item view
		ItemController itemController = new ItemController(currentGameManager, mwController, i);
		mwController.pushCenterContent(i.getName(),"view/Item.fxml", itemController, itemController::controllerFactory);
	}

}
