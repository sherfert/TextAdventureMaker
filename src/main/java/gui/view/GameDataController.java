package gui.view;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import logic.CurrentGameManager;

/**
 * Abstract class for all controllers that need access to the current game
 * manager. They can use it to obtain the persistence manager and query the
 * database.
 * 
 * Any implementing class must provide a public Constructor with no parameters.
 * 
 * @author Satia
 *
 */
public abstract class GameDataController {

	public static final String NODE_PROPERTIES_KEY_ERROR_TOOLTIP = "Error-Tooltip";

	/** The current game manager. */
	protected CurrentGameManager currentGameManager;

	/**
	 * @param currentGameManager
	 *            the currentGameManager to set
	 */
	public void setCurrentGameManager(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;
	}

	/**
	 * Show an error for any (input) node. This applies the css class "error" to
	 * the node (light red background) and shows a tooltip with the given error
	 * message right below the node. This tooltip cannot be hidden, until
	 * {@link #hideError(Node)} is called.
	 * 
	 * @param node
	 *            the node with the erroneous input
	 * @param errorMessage
	 *            the error message to display
	 */
	protected void showError(Node node, String errorMessage) {
		if (!node.getStyleClass().contains("error")) {
			// Apply some css to the node for light red BG
			node.getStyleClass().add("error");
		}

		// First check if a previous tooltip is still in place
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_ERROR_TOOLTIP)) {
			Tooltip tooltip = (Tooltip) node.getProperties().get(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);

			// Just change the error message
			tooltip.setText(errorMessage);
		} else {
			// Create a new tooltip
			Tooltip tooltip = new Tooltip(errorMessage);
			// Disallow hiding it
			tooltip.setHideOnEscape(false);

			// Show tooltip immediately below the input field
			Point2D p = node.localToScreen(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMaxY());
			tooltip.show(node, p.getX(), p.getY());

			// Save tooltip in node properties to access it later
			node.getProperties().put(NODE_PROPERTIES_KEY_ERROR_TOOLTIP, tooltip);
		}
	}

	/**
	 * Hide any previous error messages and remove the css class "error"from the
	 * node. It is safe to call this method even though no error was showing
	 * previously
	 * 
	 * @param node
	 *            the node
	 */
	protected void hideError(Node node) {
		// Remove error css class
		node.getStyleClass().remove("error");
		// Unset any tooltip message
		if (node.getProperties().containsKey(NODE_PROPERTIES_KEY_ERROR_TOOLTIP)) {
			Tooltip tooltip = (Tooltip) node.getProperties().get(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);
			tooltip.hide();
			// Unset tooltip property
			node.getProperties().remove(NODE_PROPERTIES_KEY_ERROR_TOOLTIP);
		}
	}

}
