package gui.itemEditing.action;

import data.action.AbstractAction;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import logic.CurrentGameManager;

/**
 * Abstract base class for all controllers editing a single action. They all
 * share the same remove button handler.
 * 
 * @author Satia
 *
 * @param <E>
 *            the concrete action subclass
 */
public abstract class ActionController<E extends AbstractAction> extends GameDataController {

	/** The action */
	protected E action;

	@FXML
	protected Button removeButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ActionController(CurrentGameManager currentGameManager, MainWindowController mwController, E action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	protected void initialize() {
		removeButton.setOnMouseClicked((e) -> removeObject(action, "Deleting an action",
				"Do you really want to delete this action?", "No other entities will be deleted."));
	}

}