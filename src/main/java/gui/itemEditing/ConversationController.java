package gui.itemEditing;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Conversation;
import data.ConversationLayer;
import data.action.AbstractAction;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedObjectsTableController;
import gui.customui.NamedObjectChooser;
import gui.customui.NamedObjectListView;
import gui.include.NamedObjectController;
import gui.wizards.NewNamedObjectWizard;
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
public class ConversationController extends NamedObjectsTableController<ConversationLayer> {

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
		setNodeTooltip(editConversationEnabledCB, "If ticked, the person with this conversation can be talked to.");

		editGreetingTA.textProperty().bindBidirectional(conversation.greetingProperty());
		setNodeTooltip(editGreetingTA, "The person will greet the player like this.");
		
		editEventTA.textProperty().bindBidirectional(conversation.eventProperty());
		setNodeTooltip(editEventTA, "Here you can describe anything that happens when the conversation starts.");

		// Initialize startLayerChooser, with only the layers of this
		// conversation to choose from.
		startLayerChooser.setNoValueString("(conversation ends immediately)");
		startLayerChooser.initialize(conversation.getStartLayer(), true, true, conversation::getLayers,
				conversation::setStartLayer);
		setNodeTooltip(startLayerChooser, "The first layer of the conversation. If you choose none, the conversation "
				+ "will end imediately after the greeting.");

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
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(conversation);
	}

	@Override
	protected void createObject() {
		new NewNamedObjectWizard("New conversation layer").showAndGetName().map(name -> new ConversationLayer(name))
				.ifPresent(cl -> {
					// Also add the layer to this conversation
					conversation.addLayer(cl);
					try {
						saveObject(cl);
					} catch (Exception e) {
						Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed", e);
						conversation.getLayers().remove(cl);
					}
				});
	}
}
