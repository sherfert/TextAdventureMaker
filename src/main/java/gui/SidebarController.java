package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

/**
 * Controller for the sidebar.
 * 
 * @author Satia
 */
public class SidebarController {

	// An array of all links
	private Hyperlink[] allLinks;

	@FXML
	private Hyperlink gameConfigHL;

	@FXML
	private Hyperlink locationsHL;

	@FXML
	private Hyperlink itemsHL;

	@FXML
	private Hyperlink personsHL;

	/**
	 * A reference to the main window controller to change the contents.
	 */
	private MainWindowController mwController;

	/**
	 * @param mwController
	 */
	public SidebarController(MainWindowController mwController) {
		this.mwController = mwController;
	}

	@FXML
	private void initialize() {
		gameConfigHL.setOnAction(this::linkClicked);
		locationsHL.setOnAction(this::linkClicked);
		itemsHL.setOnAction(this::linkClicked);
		personsHL.setOnAction(this::linkClicked);

		allLinks = new Hyperlink[] { gameConfigHL, locationsHL, itemsHL, personsHL };
	}

	/**
	 * Invoked when any link is clicked.
	 * 
	 * @param e
	 *            the link
	 */
	private void linkClicked(ActionEvent e) {
		Object clicked = e.getSource();

		// Set all links to not-visited except the clicked one
		for (Hyperlink link : allLinks) {
			if (link != clicked) {
				link.setVisited(false);
			}
		}

		// Switch behavior depending on the link
		if (clicked == gameConfigHL) {
			mwController.loadGameDetails();
		} else if (clicked == locationsHL) {
			mwController.loadLocations();
		} else if (clicked == itemsHL) {
			mwController.loadItems();
		}else if (clicked == personsHL) {
			mwController.loadPersons();
		}
	}

}
