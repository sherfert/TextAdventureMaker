package gui;

import java.util.List;

import data.Person;
import exception.DBClosedException;
import logic.CurrentGameManager;

/**
 * Controller for the persons view.
 * 
 * @author Satia
 */
public class PersonsController extends NamedDescribedObjectsController<Person> {

	public PersonsController(CurrentGameManager currentGameManager, MainWindowController mwController) {
		super(currentGameManager, mwController, "view/Person.fxml");
	}

	@Override
	protected List<Person> getAllObjects() throws DBClosedException {
		return currentGameManager.getPersistenceManager().getPersonManager().getAllPersons();
	}

	@Override
	protected Person createNewObject(String name, String description) {
		return new Person(name, description);
	}

	@Override
	protected GameDataController getObjectController(Person selectedObject) {
		return new PersonController(currentGameManager, mwController, selectedObject);
	}

}
