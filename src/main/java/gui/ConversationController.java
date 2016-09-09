package gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import data.Conversation;
import data.ConversationLayer;
import exception.DBClosedException;
import gui.custumui.ConversationLayerChooser;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one conversation.
 * 
 * TODO Support to additionalActions
 * 
 * @author Satia
 */
public class ConversationController extends GameDataController {

	/** The conversation */
	private Conversation conversation;

	/** An observable list with the layers. */
	private ObservableList<ConversationLayer> layersOL;

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

	@FXML
	private TableView<ConversationLayer> table;

	@FXML
	private TableColumn<ConversationLayer, Integer> idCol;

	@FXML
	private TableColumn<ConversationLayer, String> nameCol;

	@FXML
	private TextField newNameTF;

	@FXML
	private Button saveButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param item
	 *            the item to edit
	 */
	public ConversationController(CurrentGameManager currentGameManager, MainWindowController mwController,
			Conversation conversation) {
		super(currentGameManager, mwController);
		this.conversation = conversation;
	}

	@FXML
	private void initialize() {
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

		// Layers tab
		// Set cell value factories for the columns
		idCol.setCellValueFactory((p) -> new ReadOnlyObjectWrapper<Integer>(p.getValue().getId()));
		nameCol.setCellValueFactory((p) -> p.getValue().nameProperty());

		// A listener for row double-clicks
		table.setRowFactory(tv -> {
			TableRow<ConversationLayer> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					layerSelected(row.getItem());
				}
			});
			return row;
		});

		// FIXME there is always null in the list of layers !?
		// Get all layers and store in observable list, unless the list is
		// already propagated
		if (layersOL == null) {
			layersOL = FXCollections.observableArrayList(conversation.getLayers());
		}

		// Fill table
		table.setItems(layersOL);

		// Disable buttons at beginning
		saveButton.setDisable(true);

		// Assure save is only enabled if there is a name
		newNameTF.textProperty().addListener((f, o, n) -> saveButton.setDisable(n.isEmpty()));
		// Save button handler
		saveButton.setOnMouseClicked((e) -> saveNewLayer());
	}

	@Override
	public void update() {
		if (layersOL != null) {
			layersOL.setAll(conversation.getLayers());
		}
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
	
	/**
	 * Saves a new layer to both DB and table.
	 */
	private void saveNewLayer() {
		ConversationLayer l = new ConversationLayer(newNameTF.getText());
		// Add item to DB
		try {
			currentGameManager.getPersistenceManager().getAllObjectsManager().addObject(l);
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Abort: DB closed");
			return;
		}
		// Add the layer to this conversation
		conversation.addLayer(l);
		// Update
		currentGameManager.getPersistenceManager().updateChanges();
		// Add layer to our table
		layersOL.add(l);

		// Reset the form values
		newNameTF.setText("");
	}
	
	/**
	 * Opens this layer for editing.
	 * 
	 * @param l
	 *            the layer
	 */
	private void layerSelected(ConversationLayer l) {
		if (l == null) {
			return;
		}

		// TODO Open the layer view
		//ItemController itemController = new ItemController(currentGameManager, mwController, l);
		//mwController.pushCenterContent(l.getName(),"view/Item.fxml", itemController, itemController::controllerFactory);
	}
}
