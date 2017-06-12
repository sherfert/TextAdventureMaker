package gui.include;

import data.NamedDescribedObject;
import gui.GameDataController;
import gui.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logic.CurrentGameManager;

/**
 * Controller for one named described object.
 * 
 * @author Satia
 */
public class NamedDescribedObjectController extends GameDataController {

	/** The object */
	private NamedDescribedObject object;

	@FXML
	private TextArea editDescriptionTA;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param object
	 *            the object to edit
	 */
	public NamedDescribedObjectController(CurrentGameManager currentGameManager, MainWindowController mwController,
			NamedDescribedObject object) {
		super(currentGameManager, mwController);
		this.object = object;
	}

	@FXML
	private void initialize() {
		editDescriptionTA.textProperty().bindBidirectional(object.descriptionProperty());
		setNodeTooltip(editDescriptionTA, "When the player looks around, the descriptions of all items, persons, and "
				+ "ways leading out are displayed. (You can change the order by rearranging it in the location). "
				+ "When the player looks into the inventory, the descriptions of all inventory items are displayed.");
	}

	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(object);
	}
}
