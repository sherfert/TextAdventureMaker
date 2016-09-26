package gui.toplevel;

import java.util.List;

import data.Person;
import exception.DBClosedException;
import gui.MainWindowController;
import gui.NamedDescribedObjectsController;
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

	@Override
	protected List<Person> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getPersonManager().getAllPersons();
	}

	@Override
	protected Person createNewObject(String name, String description) {
		return new Person(name, description);
	}

}
