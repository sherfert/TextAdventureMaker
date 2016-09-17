package gui.customui;

import data.Way;

/**
 * ListView to manage ways.
 * 
 * @author Satia
 */
public class WayListView extends NamedObjectListView<Way> {

	public WayListView() {
		super(new WayChooser());
	}

}
