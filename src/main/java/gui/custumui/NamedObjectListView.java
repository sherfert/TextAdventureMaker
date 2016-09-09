package gui.custumui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
 * A view that combines a list of objects with 4 controls, to add, remove and
 * change the order of the items. It must be extended by a concrete class that
 * provides a concrete implementation of an NamedObjectChooser matching the
 * generic type.
 * 
 * Initialize must be called to enable the functionality.
 * 
 * TODO on doubleclick: edit NamedObject
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

	/** The items displayed in the list. */
	private ObservableList<E> listItems;

	/** The chooser for adding values. */
	private NamedObjectChooser<E> addValueChooser;

	/**
	 * @param addValueChooser
	 *            a concrete value chooser
	 */
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

	/**
	 * Initialize this list view. This method does not allow deletion of values.
	 * 
	 * @param initialList
	 *            the list of values to place in the list initially
	 * @param getAllValues
	 *            a method returning all applicable values
	 * @param orderChanged
	 *            called when the order of list items has changed
	 * @param valueClicked
	 *            called when a value was double-clicked in the list
	 * @param valueAdded
	 *            called when a value was added to the list
	 */
	public void initialize(List<E> initialList, ValuesSupplier<E> getAllValues, Consumer<List<E>> orderChanged,
			Consumer<E> valueClicked, Consumer<E> valueAdded) {
		initialize(initialList, getAllValues, orderChanged, valueClicked, valueAdded, null);
	}

	/**
	 * Initialize this list view. This method allows deletion of values.
	 * 
	 * @param initialList
	 *            the list of values to place in the list initially
	 * @param getAllValues
	 *            a method returning all applicable values
	 * @param orderChanged
	 *            called when the order of list items has changed
	 * @param valueClicked
	 *            called when a value was double-clicked in the list
	 * @param valueAdded
	 *            called when a value was added to the list
	 * @param valueRemoved
	 *            called when a value was removed from the list
	 */
	public void initialize(List<E> initialList, ValuesSupplier<E> getAllValues, Consumer<List<E>> orderChanged,
			Consumer<E> valueClicked, Consumer<E> valueAdded, Consumer<E> valueRemoved) {
		// Set initial list
		listItems = FXCollections.observableArrayList(initialList);
		listView.setItems(listItems);

		listView.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2) {
				valueClicked.accept(listView.getSelectionModel().getSelectedItem());
			}
		});

		// Initialize chooser
		addValueChooser.initialize(null, false, true, () -> {
			return getAllValues.get().stream().filter((v) -> !listItems.contains(v)).collect(Collectors.toList());
		} , (v) -> addButton.setDisable(false));

		// Disable buttons by default and if no value is chosen
		addButton.setDisable(true);
		removeButton.setDisable(true);
		upButton.setDisable(true);
		downButton.setDisable(true);
		listView.getSelectionModel().selectedItemProperty().addListener((f, o, n) -> {
			upButton.setDisable(n == null || listView.getSelectionModel().getSelectedIndex() == 0);
			downButton.setDisable(n == null || listView.getSelectionModel().getSelectedIndex() == listItems.size() - 1);
		});
		

		addButton.setOnMouseClicked((e) -> {
			E selectedItem = addValueChooser.getObjectValue();
			addValueChooser.setObjectValue(null);
			listItems.add(selectedItem);
			valueAdded.accept(selectedItem);
		});
		// Only if deletion is allowed
		if (valueRemoved != null) {
			listView.getSelectionModel().selectedItemProperty().addListener((f, o, n) -> {
				removeButton.setDisable(n == null);
			});
			
			removeButton.setOnMouseClicked((e) -> {
				E selectedItem = listView.getSelectionModel().getSelectedItem();
				listItems.remove(selectedItem);
				valueRemoved.accept(selectedItem);
			});
		}
		upButton.setOnMouseClicked((e) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			Collections.swap(listItems, index, index - 1);
			listView.getSelectionModel().selectPrevious();
			orderChanged.accept(listItems);
		});
		downButton.setOnMouseClicked((e) -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			Collections.swap(listItems, index, index + 1);
			listView.getSelectionModel().selectNext();
			orderChanged.accept(listItems);
		});
	}

}
