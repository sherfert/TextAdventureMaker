package gui.itemEditing.action;

import data.action.ChangeActionAction;
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
		
		initRadioButtonEnablingGroup(enablingActionTG, doNotChangeActionRB, enableActionRB, disableActionRB,
				action::getEnabling, action::setEnabling);
		setNodeTooltip(enableActionRB, "Triggering this action will enable the other action.");
		setNodeTooltip(disableActionRB, "Triggering this action will disable the other action.");
		setNodeTooltip(doNotChangeActionRB, "Triggering this action will not change if the other action is enabled.");
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
