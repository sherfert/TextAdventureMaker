package gui;

import data.ConversationOption;
import gui.custumui.ConversationLayerChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one item.
 * 
 * TODO Support to change additionalTakeActions
 * 
 * @author Satia
 */
public class ConversationOptionController extends GameDataController {

	/** The conversation option */
	private ConversationOption option;

	@FXML
	private ConversationLayerChooser targetChooser;

	@FXML
	private Button removeButton;

	@FXML
	private CheckBox editEnabledCB;

	@FXML
	private CheckBox editRemoveOptionEnabledCB;

	@FXML
	private TextField editTextTF;

	@FXML
	private TextArea editAnswerTA;

	@FXML
	private TextArea editEventTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param item
	 *            the item to edit
	 */
	public ConversationOptionController(CurrentGameManager currentGameManager, MainWindowController mwController, ConversationOption option) {
		super(currentGameManager, mwController);
		this.option = option;
	}

	@FXML
	private void initialize() {
		// Create new bindings
		targetChooser.initialize(option.getTarget(), true, false,
				option.getLayer().getConversation()::getLayers, option::setTarget);

		removeButton.setOnMouseClicked(
				(e) -> removeObject(option, "Deleting a conversation option", "Do you really want to delete this conversation option?",
						"This will delete the conversation option "
								+ "and actions associated with any of the deleted entities."));

		editEnabledCB.selectedProperty().bindBidirectional(option.enabledProperty());

		editRemoveOptionEnabledCB.setSelected(option.isDisablingOptionAfterChosen());
		editRemoveOptionEnabledCB.selectedProperty().addListener((f, o, n) -> option.setDisablingOptionAfterChosen(n));
		
		editTextTF.textProperty().bindBidirectional(option.textProperty());
		editAnswerTA.textProperty().bindBidirectional(option.answerProperty());
		editEventTA.textProperty().bindBidirectional(option.eventProperty());
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, option);
		} else {
			return super.controllerFactory(type);
		}
	}
}
