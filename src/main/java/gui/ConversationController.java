package gui;

import java.util.List;

import data.Conversation;
import data.ConversationLayer;
import exception.DBClosedException;
import gui.custumui.ConversationLayerChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for one conversation.
 * 
 * TODO Support to change additionalActions
 * 
 * @author Satia
 */
public class ConversationController extends NamedObjectsController<ConversationLayer> {

	/** The conversation */
	private Conversation conversation;

	@FXML
	private Button removeButton;

	@FXML
	private CheckBox editConversationEnabledCB;

	@FXML
	private TextArea editGreetingTA;

	@FXML
	private TextArea editEventTA;

	@FXML
	private ConversationLayerChooser startLayerChooser;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param conversation
	 *            the conversation to edit
	 */
	public ConversationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			Conversation conversation) {
		super(currentGameManager, mwController, "view/ConversationLayer.fxml");
		this.conversation = conversation;
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		// Create new bindings
		removeButton.setOnMouseClicked((e) -> removeObject(conversation, "Deleting a conversation",
				"Do you really want to delete this conversation?",
				"This will delete the conversation, all layers and options of the conversation, "
						+ "and actions associated with any of the deleted entities."));

		editConversationEnabledCB.setSelected(conversation.getEnabled());
		editConversationEnabledCB.selectedProperty().addListener((f, o, n) -> conversation.setEnabled(n));

		editGreetingTA.textProperty().bindBidirectional(conversation.greetingProperty());
		editEventTA.textProperty().bindBidirectional(conversation.eventProperty());

		// Initialize startLayerChooser, with only the layers of this
		// conversation to choose from.
		startLayerChooser.initialize(conversation.getStartLayer(), true, true, conversation::getLayers,
				conversation::setStartLayer);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, conversation);
		} else {
			return super.controllerFactory(type);
		}
	}
	
	@Override
	protected List<ConversationLayer> getAllObjects() throws DBClosedException {
		return conversation.getLayers();
	}

	@Override
	protected ConversationLayer createNewObject(String name) {
		ConversationLayer result = new ConversationLayer(name);
		// Also add the layer to this conversation
		conversation.addLayer(result);
		return result;
	}

	@Override
	protected GameDataController getObjectController(ConversationLayer selectedObject) {
		return new ConversationLayerController(currentGameManager, mwController, selectedObject);
	}
}
