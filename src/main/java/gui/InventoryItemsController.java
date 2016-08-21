package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.InventoryItem;
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
 * Controller for the inventory items view.
 * 
 * @author Satia
 */
public class InventoryItemsController extends GameDataController {

	/** An observable list with the inventory items. */
	private ObservableList<InventoryItem> invitemsOL;

	@FXML
	private TableView<InventoryItem> table;

	@FXML
	private TableColumn<InventoryItem, Integer> idCol;

	@FXML
	private TableColumn<InventoryItem, String> nameCol;

	@FXML
	private TableColumn<InventoryItem, String> descriptionCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	public InventoryItemsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
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
			TableRow<InventoryItem> row = new TableRow<>();
			row.setOnMouseClicked(event ->  {
				if(event.getClickCount() == 2) {
					invitemSelected(row.getItem());
				}
			});
			return row;
		});

		// Get all items and store in observable list, unless the list is
		// already propagated
		if (invitemsOL == null) {
			try {
				invitemsOL = FXCollections.observableArrayList(
						currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems());
			} catch (DBClosedException e1) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Abort: DB closed");
				return;
			}
		}

		// Fill table
		table.setItems(invitemsOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewInvItem());
	}
	
	@Override
	public void update() {
		if (invitemsOL != null) {
			try {
				invitemsOL.setAll(currentGameManager.getPersistenceManager().getInventoryItemManager().getAllInventoryItems());
			} catch (DBClosedException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Abort: DB closed");
				return;
			}
		}
	}

	/**
	 * Saves a new inventory item to both DB and table.
	 */
	private void saveNewInvItem() {
		InventoryItem i = new InventoryItem(newNameTF.getText(), newDescriptionTA.getText());
		// Add item to DB
		try {
			currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(i);
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Abort: DB closed");
			return;
		}
		currentGameManager.getPersistenceManager().updateChanges();
		// Add location to our table
		invitemsOL.add(i);

		// Reset the form values
		newNameTF.setText("");
		newDescriptionTA.setText("");
	}
	
	/**
	 * Opens this inventory item for editing.
	 * 
	 * @param i
	 *            the inventory item
	 */
	private void invitemSelected(InventoryItem i) {
		if (i == null) {
			return;
		}

		// Open the inventory item view
		InventoryItemController invitemController = new InventoryItemController(currentGameManager, mwController, i);
		mwController.pushCenterContent(i.getName(),"view/InventoryItem.fxml", invitemController, invitemController::controllerFactory);
	}

}
