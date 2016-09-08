package gui.custumui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.NamedObject;
import gui.MainWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * TODO
 * 
 * @author Satia
 */
public abstract class NamedObjectListView<E extends NamedObject> extends BorderPane {

	@FXML
	private ListView<E> listView;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	@FXML
	private Button upButton;

	@FXML
	private Button downButton;
	
	@FXML
	private HBox addHBox;

	private ObservableList<E> listItems;
	
	private NamedObjectChooser<E> addValueChooser;

	protected NamedObjectListView(NamedObjectChooser<E> addValueChooser) {
		try {
			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource("view/NamedObjectListView.fxml"));

			loader.setController(this);
			loader.setRoot(this);

			// Load the view
			loader.load();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not set NamedObjectListView content.",
					e);
		}
		// Add the chooser
		addHBox.getChildren().add(addValueChooser);
		HBox.setHgrow(addValueChooser, Priority.ALWAYS);
		this.addValueChooser = addValueChooser;
	}

	public void initialize(List<E> initialList, Consumer<List<E>> orderChanged, Consumer<E> valueAdded,
			Consumer<E> valueRemoved) {
		// Set initial list
		listItems = FXCollections.observableArrayList(initialList);
		listView.setItems(listItems);
		
		// Initialize chooser
		//addValueChooser.initialize(null, false, getAvailableValues, newValueChosenAction);

		// Disable buttons by default and if no value is chosen
		addButton.setDisable(true);
		removeButton.setDisable(true);
		upButton.setDisable(true);
		downButton.setDisable(true);
		listView.getSelectionModel().selectedItemProperty().addListener((f, o, n) -> {
			removeButton.setDisable(n == null);
			upButton.setDisable(n == null || listView.getSelectionModel().getSelectedIndex() == 0);
			downButton.setDisable(n == null || listView.getSelectionModel().getSelectedIndex() == listItems.size() - 1);
		});

		// TODO enable "+ button" if a value is chosen in NamedObjectChooser
		// testTF.textProperty().addListener((f, o, n) ->
		// saveButton.setDisable(n.isEmpty()));

		// Button handlers
		removeButton.setOnMouseClicked((e) -> {
			E selectedItem = listView.getSelectionModel().getSelectedItem();
			listItems.remove(selectedItem);
			valueRemoved.accept(selectedItem);
		});
		upButton.setOnMouseClicked((e) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			Collections.swap(listItems, index, index - 1);
			orderChanged.accept(listItems);
		});
		downButton.setOnMouseClicked((e) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			Collections.swap(listItems, index, index + 1);
			orderChanged.accept(listItems);
		});

		// TODO + handler
	}

}
