package gui.customui;

import data.interfaces.PassivelyUsable;

/**
 * ListView to manage PassivelyUsables. This can group several concrete subclasses into one list.
 * 
 * @author Satia
 */
public class PassivelyUsableListView extends NamedObjectListView<PassivelyUsable> {

	public PassivelyUsableListView() {
		super(new PassivelyUsableChooser());
	}

}
