package gui;

import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * The navbar controller.
 * 
 * @author Satia
 */
public class NavbarController {

	/**
	 * One item in the navbar.
	 * 
	 * @author Satia
	 */
	private class NavbarItem {
		GameDataController controller;
		String fxml;
		Hyperlink link;
		Label arrow;

		public NavbarItem(String title, GameDataController controller, String fxml) {
			this.controller = controller;
			this.fxml = fxml;
			this.link = new Hyperlink(title);
			this.arrow = new Label("⇒");
			this.link.setOnAction(NavbarController.this::linkClicked);
		}

	}

	@FXML
	private HBox box;

	/**
	 * The items in the navbar
	 */
	private Stack<NavbarItem> items;

	/**
	 * A reference to the main window controller to change the contents.
	 */
	private MainWindowController mwController;

	/**
	 * @param mwController
	 */
	public NavbarController(MainWindowController mwController) {
		this.mwController = mwController;
		items = new Stack<>();
	}

	/**
	 * Invoked when any link is clicked.
	 * 
	 * @param e
	 *            the link
	 */
	private void linkClicked(ActionEvent e) {
		Object clicked = e.getSource();

		int popCount = 0;
		boolean found = false;
		for (NavbarItem item : items) {

			if (!found) {
				found = (clicked == item.link);
				if (found) {
					// Load the controller
					mwController.setCenterContent(item.fxml, item.controller);
				}
			} else {
				// Remove everything after the found link
				box.getChildren().removeAll(item.arrow, item.link);
				// Increment the pop count
				popCount++;
			}
		}
		// Pop all unused items from the stack
		for (int i = 0; i < popCount; i++) {
			items.pop();
		}
	}

	/**
	 * Pushes a new link to the end of the navbar.
	 * 
	 * @param title
	 *            the title for the link
	 * @param c
	 *            the controller
	 * @param fxml
	 *            the fxml
	 */
	public void push(String title, GameDataController c, String fxml) {
		NavbarItem item = new NavbarItem(title, c, fxml);
		if (!box.getChildren().isEmpty()) {
			// Only add the arrow starting from the second item
			box.getChildren().add(item.arrow);
		}
		box.getChildren().add(item.link);
		items.push(item);
	}

	/**
	 * Pops the last link from the navbar and navigates to the view of the
	 * second last link.
	 */
	public void pop() {
		NavbarItem item = items.pop();
		box.getChildren().removeAll(item.arrow, item.link);

		// Navigate to the top entry
		NavbarItem top = items.peek();
		mwController.setCenterContent(top.fxml, top.controller);
	}

	/**
	 * Clears the navbar.
	 */
	public void reset() {
		box.getChildren().clear();
		items.clear();
	}

}
