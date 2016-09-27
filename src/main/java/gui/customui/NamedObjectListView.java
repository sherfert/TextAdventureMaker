package gui.customui;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import data.interfaces.HasName;
import gui.utility.Loader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * A view that combines a list of objects with 4 controls, to add, remove and
 * change the order of the items.
 * 
 * Initialize must be called to enable the functionality.
 * 
 * TODO button sizes Linux
 * 
 * @author Satia
 */
public class NamedObjectListView<E extends HasName> extends BorderPane {

	@FXML
	private ListView<E> listView;

	@FXML
	protected TextField filterTF;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	@FXML
	private Button upButton;

	@FXML
	private Button downButton;

	@FXML
	private NamedObjectChooser<E> addValueChooser;

	/** The items displayed in the list. */
	private ObservableList<E> listItems;

	public NamedObjectListView() {
		Loader.load(this, this, "view/customui/NamedObjectListView.fxml");
	}

	/**
	 * @return the noValueString for the chooser
	 */
	public String getNoValueString() {
		return addValueChooser.getNoValueString();
	}

	/**
	 * @param noValueString
	 *            the noValueString to set for the chooser
	 */
	public void setNoValueString(String noValueString) {
		addValueChooser.setNoValueString(noValueString);
	}

	/**
	 * Initialize this list view. This method allows deletion of values.
	 * 
	 * @param initialList
	 *            the list of values to place in the list initially
	 * @param getAllValues
	 *            a method returning all applicable values
	 * @param orderChanged
	 *            called when the order of list items has changed. If
	 *            {@code null} up/down buttons will be disabled.
	 * @param valueClicked
	 *            called when a value was double-clicked in the list
	 * @param valueAdded
	 *            called when a value was added to the list
	 * @param valueRemoved
	 *            called when a value was removed from the list. If {@code null}
	 *            removal button will be disabled.
	 */
	public void initialize(List<E> initialList, ValuesSupplier<E> getAllValues, Consumer<List<E>> orderChanged,
			Consumer<E> valueClicked, Consumer<E> valueAdded, Consumer<E> valueRemoved) {

		// Predicates to see if the buttons should be enabled
		Supplier<Boolean> addEnabled = () -> (filterTF.getText().isEmpty() && addValueChooser.getObjectValue() != null);
		Supplier<Boolean> removeEnabled = () -> (valueRemoved != null
				&& listView.getSelectionModel().getSelectedItem() != null);
		Supplier<Boolean> upEnabled = () -> (orderChanged != null && filterTF.getText().isEmpty()
				&& listView.getSelectionModel().getSelectedItem() != null
				&& listView.getSelectionModel().getSelectedIndex() != 0);
		Supplier<Boolean> downEnabled = () -> (orderChanged != null && filterTF.getText().isEmpty()
				&& listView.getSelectionModel().getSelectedItem() != null
				&& listView.getSelectionModel().getSelectedIndex() != listItems.size() - 1);

		// Set initial list
		listItems = FXCollections.observableArrayList(initialList);

		// Filter
		FilteredList<E> filteredData = new FilteredList<>(listItems, p -> true);

		filterTF.textProperty().addListener((f, o, n) -> {
			filteredData.setPredicate(obj -> {
				// If filter text is empty, display all objects.
				if (n == null || n.isEmpty()) {
					return true;
				}

				// Compare name of every E with filter text.
				return obj.getName().toLowerCase().contains(n.toLowerCase());
			});
			// Refresh buttons
			upButton.setDisable(!upEnabled.get());
			downButton.setDisable(!downEnabled.get());
			addButton.setDisable(!addEnabled.get());
		});

		listView.setItems(filteredData);

		listView.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2) {
				valueClicked.accept(listView.getSelectionModel().getSelectedItem());
			}
		});

		// Initialize chooser
		addValueChooser.initialize(null, false, true, () -> {
			return getAllValues.get().stream().filter((v) -> !listItems.contains(v)).collect(Collectors.toList());
		} , (v) -> addButton.setDisable(!addEnabled.get()));

		// Disable buttons by default and if no value is chosen
		addButton.setDisable(true);
		removeButton.setDisable(true);
		upButton.setDisable(true);
		downButton.setDisable(true);
		listView.getSelectionModel().selectedItemProperty().addListener((f, o, n) -> {
			upButton.setDisable(!upEnabled.get());
			downButton.setDisable(!downEnabled.get());
			removeButton.setDisable(!removeEnabled.get());
		});

		addButton.setOnMouseClicked((e) -> {
			E selectedItem = addValueChooser.getObjectValue();
			addValueChooser.setObjectValue(null);
			addButton.setDisable(!addEnabled.get());
			listItems.add(selectedItem);
			valueAdded.accept(selectedItem);
		});
		removeButton.setOnMouseClicked((e) -> {
			E selectedItem = listView.getSelectionModel().getSelectedItem();
			listItems.remove(selectedItem);
			valueRemoved.accept(selectedItem);
		});
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
