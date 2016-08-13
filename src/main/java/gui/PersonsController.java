package gui;

import data.Person;
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
 * Controller for the persons view.
 * 
 * @author Satia
 */
public class PersonsController extends GameDataController {

	/** An observable list with the persons. */
	private ObservableList<Person> personsOL;

	@FXML
	private TableView<Person> table;

	@FXML
	private TableColumn<Person, Integer> idCol;

	@FXML
	private TableColumn<Person, String> nameCol;

	@FXML
	private TableColumn<Person, String> descriptionCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private Button saveButton;

	public PersonsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
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
			TableRow<Person> row = new TableRow<>();
			row.setOnMouseClicked(event ->  {
				if(event.getClickCount() == 2) {
					personSelected(row.getItem());
				}
			});
			return row;
		});

		// Get all items and store in observable list, unless the list is
		// already propagated
		if (personsOL == null) {
			personsOL = FXCollections.observableArrayList(
					currentGameManager.getPersistenceManager().getPersonManager().getAllPersons());
		}

		// Fill table
		table.setItems(personsOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewPerson());
	}
	
	@Override
	public void update() {
		if (personsOL != null) {
			personsOL.setAll(currentGameManager.getPersistenceManager().getPersonManager().getAllPersons());
		}
	}

	/**
	 * Saves a new person to both DB and table.
	 */
	private void saveNewPerson() {
		Person p = new Person(newNameTF.getText(), newDescriptionTA.getText());
		// Add item to DB
		currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(p);
		currentGameManager.getPersistenceManager().updateChanges();
		// Add location to our table
		personsOL.add(p);

		// Reset the form values
		newNameTF.setText("");
		newDescriptionTA.setText("");
	}
	
	/**
	 * Opens this person for editing.
	 * 
	 * @param i
	 *            the item
	 */
	private void personSelected(Person p) {
		if (p == null) {
			return;
		}

		// TODOOpen the person view 
		//ItemController itemController = new ItemController(currentGameManager, mwController, i);
		//mwController.pushCenterContent(i.getName(),"view/Item.fxml", itemController, itemController::controllerFactory);
	}

}
