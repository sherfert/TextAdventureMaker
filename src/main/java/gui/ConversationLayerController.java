package gui;

import java.util.List;
import java.util.function.Supplier;

import data.ConversationLayer;
import data.ConversationOption;
import exception.DBClosedException;
import gui.custumui.ConversationLayerChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one conversation layer.
 * 
 * TODO Support to reorder options.
 * 
 * @author Satia
 */
public class ConversationLayerController extends NamedObjectsController<ConversationOption> {

	/** The conversation layer */
	private ConversationLayer layer;

	@FXML
	private Button removeButton;

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
	 * @param layer
	 *            the layer to edit
	 */
	public ConversationLayerController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ConversationLayer layer) {
		super(currentGameManager, mwController, "view/ConversationOption.fxml");
		this.layer = layer;
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		// Create new bindings
		removeButton.setOnMouseClicked((e) -> removeObject(layer, "Deleting a conversation layer",
				"Do you really want to delete this conversation layer?",
				"This will delete the conversation layer and options of the layer, "
						+ "and actions associated with any of the deleted entities."));

		targetChooser.initialize(null, true, false, () -> layer.getConversation().getLayers(), (l) -> {
		});

		// Assure save is only enabled if there is a name, text and answer
		Supplier<Boolean> anyRequiredFieldEmpty = () -> newNameTF.textProperty().get().isEmpty()
				|| newTextTF.textProperty().get().isEmpty() || newAnswerTA.textProperty().get().isEmpty();
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newTextTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
		newAnswerTA.textProperty().addListener((f, o, n) -> saveButton.setDisable(anyRequiredFieldEmpty.get()));
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

	@Override
	protected GameDataController getObjectController(ConversationOption selectedObject) {
		return new ConversationOptionController(currentGameManager,
					mwController, selectedObject);
	}
}
