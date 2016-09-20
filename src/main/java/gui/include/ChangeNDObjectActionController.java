package gui.include;

import data.action.ChangeNDObjectAction;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one named described object.
 * 
 * @author Satia
 */
public class ChangeNDObjectActionController extends GameDataController {

	/** The action */
	private ChangeNDObjectAction action;

	@FXML
	Hyperlink link;

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

		newNameTF.setText(action.getNewName());
		newNameTF.disableProperty().bind(newNameCB.selectedProperty().not());
		newNameTF.textProperty().addListener((f, o, n) -> {
			action.setNewName(n);
		});
		newNameCB.setSelected(action.getNewName() != null);
		newNameCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewName(null);
			}
		});

		newDescriptionTA.setText(action.getNewDescription());
		newDescriptionTA.disableProperty().bind(newDescriptionCB.selectedProperty().not());
		newDescriptionTA.textProperty().addListener((f, o, n) -> {
			action.setNewDescription(n);
		});
		newDescriptionCB.setSelected(action.getNewDescription() != null);
		newDescriptionCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewDescription(null);
			}
		});
	}
}
