package gui.toplevel;

import java.util.List;

import data.Person;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
import gui.wizards.NewNamedObjectWizard;
import javafx.fxml.FXML;
import logic.CurrentGameManager;

/**
 * Controller for the persons view.
 * 
 * @author Satia
 */
public class PersonsController extends NamedDescribedObjectsController<Person> {

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 */
	public PersonsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController);
	}

	@FXML
	@Override
	protected void initialize() {
		super.initialize();
		newButton.setText("New person");
	}

	@Override
	protected List<Person> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getPersonManager().getAllPersons();
	}

	@Override
	public boolean isObsolete() {
		return false;
	}

	@Override
	protected void createObject() {
		new NewNamedObjectWizard("New person").showAndGetName().map(name -> new Person(name, ""))
				.ifPresent(this::trySaveObject);
	}

}
