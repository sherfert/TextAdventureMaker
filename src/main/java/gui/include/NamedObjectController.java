package gui.include;

import data.NamedObject;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import logic.CurrentGameManager;

/**
 * Controller for one named object.
 * 
 * @author Satia
 */
public class NamedObjectController extends GameDataController {

	/** The object */
	private NamedObject object;

	@FXML
	private TextField editNameTF;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param object
	 *            the object to edit
	 */
	public NamedObjectController(CurrentGameManager currentGameManager, MainWindowController mwController,
			NamedObject object) {
		super(currentGameManager, mwController);
		this.object = object;
	}

	@FXML
	private void initialize() {
		// Refresh the displayed object
		currentGameManager.getPersistenceManager().refreshEntity(object);

		// Create new bindings
		editNameTF.setText(object.getName());
		editNameTF.textProperty().addListener((f, o, n) -> updateName(n, editNameTF, false, object::setName));
		setNodeTooltip(editNameTF, "The name can ne used to refer to the object. For inventory items, it is also "
				+ "displayed when the player looks into the inventory.");
	}

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(object);
	}
}
