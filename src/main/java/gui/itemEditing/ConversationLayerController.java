package gui.itemEditing;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import data.ConversationLayer;
import data.ConversationOption;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsController;
import gui.customui.ConversationLayerChooser;
import gui.include.NamedObjectController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one conversation layer.
 * 
 * TODO delete this layer
 * 
 * @author Satia
 */
public class ConversationLayerController extends NamedObjectsController<ConversationOption> {

	/** The conversation layer */
	private ConversationLayer layer;

	@FXML
	private TabPane tabPane;

	@FXML
	private TableColumn<ConversationOption, String> targetCol;

	@FXML
	private TableColumn<ConversationOption, String> textCol;

	@FXML
	private Button removeButton;

	@FXML
	private Button upButton;

	@FXML
	private Button downButton;

	@FXML
	private TextField newTextTF;

	@FXML
	private TextArea newAnswerTA;

	@FXML
	private TextArea newEventTA;

	@FXML
	private ConversationLayerChooser targetChooser;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param layer
	 *            the layer to edit
	 */
	public ConversationLayerController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ConversationLayer layer) {
		super(currentGameManager, mwController);
		this.layer = layer;
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		// Create new bindings
		targetCol.setCellValueFactory((p) -> {
			ConversationLayer target = p.getValue().getTarget();
			if (target != null) {
				return target.nameProperty();
			} else {
				return new ReadOnlyObjectWrapper<String>("(ends conversation)");
			}

		});
		textCol.setCellValueFactory((p) -> p.getValue().textProperty());

		removeButton.setOnMouseClicked((e) -> removeObject(layer, "Deleting a conversation layer",
				"Do you really want to delete this conversation layer?",
				"This will delete the conversation layer and options of the layer, "
						+ "and actions associated with any of the deleted entities."));

		targetChooser.initialize(layer, true, false, () -> layer.getConversation().getLayers(), (l) -> {
		});

		// Assure save is only enabled if there is a name, text and answer
		Supplier<Boolean> anyRequiredFieldEmpty = () -> newNameTF.textProperty().get().isEmpty()
				|| newTextTF.textProperty().get().isEmpty() || newAnswerTA.textProperty().get().isEmpty();
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newTextTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newAnswerTA.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));

		// Disable buttons by default and if no value is chosen
		upButton.setDisable(true);
		downButton.setDisable(true);
		table.getSelectionModel().selectedItemProperty().addListener((f, o, n) -> {
			upButton.setDisable(n == null || table.getSelectionModel().getSelectedIndex() == 0);
			downButton.setDisable(
					n == null || table.getSelectionModel().getSelectedIndex() == table.getItems().size() - 1);
		});

		upButton.setOnMouseClicked((e) -> {
			int index = table.getSelectionModel().getSelectedIndex();
			Collections.swap(objectsOL, index, index - 1);
			table.getSelectionModel().selectPrevious();
			layer.updateOptions(objectsOL);
		});
		downButton.setOnMouseClicked((e) -> {
			int index = table.getSelectionModel().getSelectedIndex();
			Collections.swap(objectsOL, index, index + 1);
			table.getSelectionModel().selectNext();
			layer.updateOptions(objectsOL);
		});

		saveTabIndex(tabPane);
	}

	@Override
	protected void resetFormValues() {
		super.resetFormValues();
		newAnswerTA.setText("");
		newEventTA.setText("");
		newTextTF.setText("");
		targetChooser.setObjectValue(null);
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, layer);
		} else {
			return super.controllerFactory(type);
		}
	}

	@Override
	protected List<ConversationOption> getAllObjects() throws DBClosedException {
		return layer.getOptions();
	}

	@Override
	protected ConversationOption createNewObject(String name) {
		ConversationOption result = new ConversationOption(name, newTextTF.getText(), newAnswerTA.getText(),
				newEventTA.getText(), targetChooser.getObjectValue());
		// Also add the option to this layer
		layer.addOption(result);
		return result;
	}
}
