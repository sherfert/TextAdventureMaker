package gui.include;

import data.action.ChangeNDObjectAction;
import gui.GameDataController;
import gui.MainWindowController;
import gui.itemEditing.action.ActionController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller to change properties of one named described object.
 * 
 * @author Satia
 */
public class ChangeNDObjectActionController extends GameDataController {

	/** The action */
	private ChangeNDObjectAction action;

	@FXML
	private Hyperlink link;

	@FXML
	private CheckBox newNameCB;

	@FXML
	private TextField newNameTF;

	@FXML
	private TextArea newDescriptionTA;

	@FXML
	private CheckBox newDescriptionCB;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeNDObjectActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeNDObjectAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		link.setText("Changing: " + action.getObject().toString());
		link.setOnAction((e) -> {
			objectSelected(action.getObject());
		});

		ActionController.initCheckBoxAndTextFieldSetter(newNameCB, newNameTF,
				action::getNewName, action::setNewName);
		ActionController.initCheckBoxAndTextFieldSetter(newDescriptionCB, newDescriptionTA,
				action::getNewDescription, action::setNewDescription);
	}
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(action);
	}
}
