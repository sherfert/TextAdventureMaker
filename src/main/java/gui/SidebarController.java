package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class SidebarController {
	
	// An array of all links
	private Hyperlink[] allLinks;
	
	@FXML
	private Hyperlink gameConfigHL;

	@FXML
	private Hyperlink locationsHL;
	
	@FXML
	private Hyperlink itemsHL;

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
		

		allLinks = new Hyperlink[] { gameConfigHL, locationsHL, itemsHL };
	}
	
	private void linkClicked(ActionEvent e) {
		Object clicked = e.getSource();
		
		// Set all links to not-visited except the clicked one
		for(Hyperlink link : allLinks) {
			if(link != clicked) {
				link.setVisited(false);
			}
		}
		
		// Switch behavior depending on the link
		if(clicked == gameConfigHL) {
			mwController.loadGameDetails();
		} else if(clicked == locationsHL) {
			mwController.loadLocations();
		} else if(clicked == itemsHL) {
			mwController.loadItems();
		}
	}

}
