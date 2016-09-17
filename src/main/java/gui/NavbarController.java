package gui;

import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

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
		String fxml;
		GameDataController controller;
		Callback<Class<?>, Object> controllerFactory;
		Hyperlink link;
		Label arrow;

		public NavbarItem(String title, String fxml, GameDataController controller,
				Callback<Class<?>, Object> controllerFactory) {
			this.fxml = fxml;
			this.controller = controller;
			this.controllerFactory = controllerFactory;
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
	 *            the main window controller
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
					mwController.setCenterContent(item.fxml, item.controller, item.controllerFactory);
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
	 * @param fxml
	 *            the fxml
	 * @param c
	 *            the controller
	 * @param controllerFactory
	 *            a controller factory or {@code null}
	 */
	public void push(String title, String fxml, GameDataController c, Callback<Class<?>, Object> controllerFactory) {
		NavbarItem item = new NavbarItem(title, fxml, c, controllerFactory);
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
		mwController.setCenterContent(top.fxml, top.controller, top.controllerFactory);
	}

	/**
	 * Clears the navbar.
	 */
	public void reset() {
		box.getChildren().clear();
		items.clear();
	}

}
