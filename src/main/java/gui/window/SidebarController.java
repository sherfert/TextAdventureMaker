package gui.window;

import gui.MainWindowController;
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
	private Hyperlink overviewHL;

	@FXML
	private Hyperlink locationsHL;

	@FXML
	private Hyperlink waysHL;

	@FXML
	private Hyperlink itemsHL;

	@FXML
	private Hyperlink inventoryItemsHL;

	@FXML
	private Hyperlink personsHL;

	@FXML
	private Hyperlink conversationsHL;

	@FXML
	private Hyperlink actionsHL;

	/**
	 * A reference to the main window controller to change the contents.
	 */
	private MainWindowController mwController;

	/**
	 * @param mwController
	 *            the main window controller
	 */
	public SidebarController(MainWindowController mwController) {
		this.mwController = mwController;
	}

	@FXML
	private void initialize() {
		gameConfigHL.setOnAction(this::linkClicked);
		overviewHL.setOnAction(this::linkClicked);
		locationsHL.setOnAction(this::linkClicked);
		waysHL.setOnAction(this::linkClicked);
		itemsHL.setOnAction(this::linkClicked);
		inventoryItemsHL.setOnAction(this::linkClicked);
		personsHL.setOnAction(this::linkClicked);
		conversationsHL.setOnAction(this::linkClicked);
		actionsHL.setOnAction(this::linkClicked);

		allLinks = new Hyperlink[] { gameConfigHL, overviewHL, locationsHL, waysHL, itemsHL, inventoryItemsHL, personsHL,
				conversationsHL, actionsHL };
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
		} else if (clicked == overviewHL) {
			mwController.loadOverview();
		} else if (clicked == locationsHL) {
			mwController.loadLocations();
		} else if (clicked == waysHL) {
			mwController.loadWays();
		} else if (clicked == itemsHL) {
			mwController.loadItems();
		} else if (clicked == inventoryItemsHL) {
			mwController.loadInventoryItems();
		} else if (clicked == personsHL) {
			mwController.loadPersons();
		} else if (clicked == conversationsHL) {
			mwController.loadConversations();
		}else if (clicked == actionsHL) {
			mwController.loadActions();
		}
	}

}
