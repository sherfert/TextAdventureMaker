package gui.include;

import data.action.AbstractAction.Enabling;
import data.action.ChangeUsableObjectAction;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller to change properties of one usable object.
 * 
 * @author Satia
 */
public class ChangeUsableObjectActionController extends GameDataController {

	/** The action */
	private ChangeUsableObjectAction action;
	
	
	@FXML
	private RadioButton doNotChangeUseRB;

	@FXML
	private RadioButton enableUseRB;

	@FXML
	private RadioButton disableUseRB;

	@FXML
	private ToggleGroup enablingUseTG;

	@FXML
	private CheckBox newUseSuccessfulTextCB;

	@FXML
	private TextField newUseSuccessfulTextTF;

	@FXML
	private CheckBox newUseForbiddenTextCB;

	@FXML
	private TextField newUseForbiddenTextTF;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeUsableObjectActionController(CurrentGameManager currentGameManager,
			MainWindowController mwController, ChangeUsableObjectAction action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	private void initialize() {
		switch(action.getEnabling()) {
		case DISABLE:
			enablingUseTG.selectToggle(disableUseRB);
			break;
		case DO_NOT_CHANGE:
			enablingUseTG.selectToggle(doNotChangeUseRB);
			break;
		case ENABLE:
			enablingUseTG.selectToggle(enableUseRB);
			break;
		}
		enablingUseTG.selectedToggleProperty().addListener((f, o, n) -> {
			if(n == doNotChangeUseRB) {
				action.setEnabling(Enabling.DO_NOT_CHANGE);
			} else if(n == enableUseRB) {
				action.setEnabling(Enabling.ENABLE);
			} else if(n == disableUseRB) {
				action.setEnabling(Enabling.DISABLE);
			}
		});
		
		newUseSuccessfulTextTF.setText(action.getNewUseSuccessfulText());
		newUseSuccessfulTextTF.disableProperty().bind(newUseSuccessfulTextCB.selectedProperty().not());
		newUseSuccessfulTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewUseSuccessfulText(n);
		});
		newUseSuccessfulTextCB.setSelected(action.getNewUseSuccessfulText() != null);
		newUseSuccessfulTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewUseSuccessfulText(null);
			}
		});

		newUseForbiddenTextTF.setText(action.getNewUseForbiddenText());
		newUseForbiddenTextTF.disableProperty().bind(newUseForbiddenTextCB.selectedProperty().not());
		newUseForbiddenTextTF.textProperty().addListener((f, o, n) -> {
			action.setNewUseForbiddenText(n);
		});
		newUseForbiddenTextCB.setSelected(action.getNewUseForbiddenText() != null);
		newUseForbiddenTextCB.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				action.setNewUseForbiddenText(null);
			}
		});
	}
}
