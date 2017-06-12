package gui.include;

import data.action.ChangeInspectableObjectAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.itemEditing.action.ActionController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller to change properties of one inspectable object.
 * 
 * @author Satia
 */
public class ChangeInspectableObjectActionController extends GameDataController {

	/** The action */
	private ChangeInspectableObjectAction action;

	@FXML
	private TextArea newInspectionTextTA;

	@FXML
	private CheckBox newInspectionTextCB;

	@FXML
	private TextArea identifiersAddTA;

	@FXML
	private TextArea identifiersRemoveTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeInspectableObjectActionController(CurrentGameManager currentGameManager,
			MainWindowController mwController, ChangeInspectableObjectAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		ActionController.initCheckBoxAndTextFieldSetter(newInspectionTextCB, newInspectionTextTA,
				action::getNewInspectionText, action::setNewInspectionText);

		identifiersAddTA.setText(getListString(action.getIdentifiersToAdd()));
		identifiersAddTA.textProperty().addListener((f, o, n) -> updateList(n, action::setIdentifiersToAdd));
		setNodeTooltip(identifiersAddTA, "Identifiers listed here will be added to the entity.");

		identifiersRemoveTA.setText(getListString(action.getIdentifiersToRemove()));
		identifiersRemoveTA.textProperty().addListener((f, o, n) -> updateList(n, action::setIdentifiersToRemove));
		setNodeTooltip(identifiersRemoveTA, "Identifiers listed here will be removed from the entity.");
		
		setNodeTooltip(newInspectionTextTA, "This will be the new inspection text.");
		setNodeTooltip(newInspectionTextCB, "If ticked, the inspection text will be changed.");
	}
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(action);
	}
}
