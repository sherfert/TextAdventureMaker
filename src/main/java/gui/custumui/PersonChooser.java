package gui.custumui;

import java.util.function.Consumer;

import data.Person;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing persons. Must be initialized with
 * {@link PersonChooser#initialize(Person, boolean, boolean, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * @author satia
 */
public class PersonChooser extends NamedObjectChooser<Person> {

	/**
	 * Create a new LocationChooser
	 */
	public PersonChooser() {
		super("(no person)");
	}
}
