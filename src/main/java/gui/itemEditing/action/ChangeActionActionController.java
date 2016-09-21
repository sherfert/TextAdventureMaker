package gui.itemEditing.action;

import data.action.ChangeActionAction;
import data.action.AbstractAction.Enabling;
import gui.MainWindowController;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Controller for one {@link ChangeActionAction}.
 * 
 * @author Satia
 */
public class ChangeActionActionController extends ActionController<ChangeActionAction> {

	@FXML
	private Hyperlink link;
	
	@FXML
	private RadioButton doNotChangeActionRB;

	@FXML
	private RadioButton enableActionRB;

	@FXML
	private RadioButton disableActionRB;

	@FXML
	private ToggleGroup enablingActionTG;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ChangeActionActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			ChangeActionAction action) {
		super(currentGameManager, mwController, action);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		link.setText("Changing: " + action.getAction().toString());
		link.setOnAction((e) -> {
			objectSelected(action.getAction());
		});
		
		switch(action.getEnabling()) {
		case DISABLE:
			enablingActionTG.selectToggle(disableActionRB);
			break;
		case DO_NOT_CHANGE:
			enablingActionTG.selectToggle(doNotChangeActionRB);
			break;
		case ENABLE:
			enablingActionTG.selectToggle(enableActionRB);
			break;
		}
		enablingActionTG.selectedToggleProperty().addListener((f, o, n) -> {
			if(n == doNotChangeActionRB) {
				action.setEnabling(Enabling.DO_NOT_CHANGE);
			} else if(n == enableActionRB) {
				action.setEnabling(Enabling.ENABLE);
			} else if(n == disableActionRB) {
				action.setEnabling(Enabling.DISABLE);
			}
		});
	}

	/**
	 * Controller factory that initializes controllers correctly.
	 */
	@Override
	public Object controllerFactory(Class<?> type) {
		if (type == NamedObjectController.class) {
			return new NamedObjectController(currentGameManager, mwController, action);
		} else if (type == AbstractActionController.class) {
			return new AbstractActionController(currentGameManager, mwController, action);
		} else {
			return super.controllerFactory(type);
		}
	}
}
