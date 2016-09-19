package gui.customui;

import data.Person;

/**
 * Custom TextField for choosing persons.
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
