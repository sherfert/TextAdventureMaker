package gui.itemEditing.action;

import java.util.List;
import java.util.stream.Collectors;

import data.action.AbstractAction;
import data.action.MultiAction;
import gui.MainWindowController;
import gui.customui.ActionListView;
import gui.include.AbstractActionController;
import gui.include.NamedObjectController;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for one {@link MultiAction}.
 * 
 * @author Satia
 */
public class MultiActionController extends ActionController<MultiAction> {

	@FXML
	private ActionListView actionsListView;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public MultiActionController(CurrentGameManager currentGameManager, MainWindowController mwController,
			MultiAction action) {
		super(currentGameManager, mwController, action);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();

		actionsListView.initialize(action.getActions(), () -> {
			List<AbstractAction> allActions = this.currentGameManager.getPersistenceManager().getActionManager().getAllActions();
			// Filter out Multiactions to avoid recursion
			return allActions.stream().filter((a)-> !(a instanceof MultiAction)).collect(Collectors.toList());
		} , null, this::objectSelected, (a) -> action.addAction(a), (a) -> action.removeAction(a));
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
