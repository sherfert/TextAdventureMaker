package gui.customui;

import data.Person;

/**
 * ListView to manage persons.
 * 
 * @author Satia
 */
public class PersonListView extends NamedObjectListView<Person> {

	public PersonListView() {
		super(new PersonChooser());
	}

}
