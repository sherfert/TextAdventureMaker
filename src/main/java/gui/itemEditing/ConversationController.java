package gui.itemEditing;

import java.util.List;

import data.Conversation;
import data.ConversationLayer;
import data.action.AbstractAction;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsController;
import gui.customui.NamedObjectChooser;
import gui.customui.NamedObjectListView;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for one conversation.
 * 
 * @author Satia
 */
public class ConversationController extends NamedObjectsController<ConversationLayer> {

	/** The conversation */
	private Conversation conversation;

	@FXML
	private TabPane tabPane;

	@FXML
	private Button removeButton;

	@FXML
	private CheckBox editConversationEnabledCB;

	@FXML
	private TextArea editGreetingTA;

	@FXML
	private TextArea editEventTA;

	@FXML
	private NamedObjectChooser<ConversationLayer> startLayerChooser;

	@FXML
	private NamedObjectListView<AbstractAction> actionsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param conversation
	 *            the conversation to edit
	 */
	public ConversationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			Conversation conversation) {
		super(currentGameManager, mwController);
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
		
		actionsListView.initialize(conversation.getAdditionalActions(),
				this.currentGameManager.getPersistenceManager().getActionManager()::getAllActions, null,
				this::objectSelected, (a) -> conversation.addAdditionalAction(a),
				(a) -> conversation.removeAdditionalAction(a));

		saveTabIndex(tabPane);
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
}
