package gui.view;

import java.io.File;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import logic.CurrentGameManager;

public class MainWindowController {

	/** The window of this controller. */
	private Window window;

	@FXML
	private MenuItem newMenuItem;
	
	@FXML
	private MenuItem openMenuItem;

	@FXML
	private MenuItem exportMenuItem;

	@FXML
	private MenuItem closeMenuItem;

	/**
	 * The constructor is called before the initialize() method.
	 */
	public MainWindowController() {
	}

	@FXML
	private void initialize() {
		newMenuItem.setOnAction((e) -> this.newOrOpenMenuItemClicked(e, true));
		openMenuItem.setOnAction((e) -> this.newOrOpenMenuItemClicked(e, false));
		closeMenuItem.setOnAction((e) -> this.close());
	}

	/**
	 * Sets the window of this controller.
	 * 
	 * @param window
	 *            the window
	 */
	public void setWindow(Window window) {
		this.window = window;
	}
	
	/**
	 * Opens a file chooser to let the user choose the file to create/open in the
	 * application.
	 * 
	 * @param e
	 * @param creatingNew if a new file is being created
	 */
	private void newOrOpenMenuItemClicked(ActionEvent e, boolean creatingNew) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(creatingNew ? "Choose where to save the new file" : "Choose a game file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game databases", "*.h2.db"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		
		File file;
		if(creatingNew) {
		file = fileChooser.showSaveDialog(window);
		} else {
			file = fileChooser.showOpenDialog(window);
		}

		// Open the file, if one was chosen
		if (file != null) {
			CurrentGameManager.open(file);
		}
	}
	
	/**
	 * Called when the main window is closed. Cleans up and then exits.
	 */
	public void close() {
		CurrentGameManager.close();
		Platform.exit();
	}
}
