package gui.customui;

import data.action.AbstractAction;

/**
 * ListView to manage actions. This can group several concrete subclasses into one list.
 * 
 * @author Satia
 */
public class ActionListView extends NamedObjectListView<AbstractAction> {

	public ActionListView() {
		super(new ActionChooser());
	}

}
