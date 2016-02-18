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
	private void initialize() {
		gameConfigHL.setOnAction(this::linkClicked);
		locationsHL.setOnAction(this::linkClicked);
		

		allLinks = new Hyperlink[] { gameConfigHL, locationsHL };
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
			System.out.println("game");
		} else if(clicked == locationsHL) {
			System.out.println("locations");
		}
	}

}
