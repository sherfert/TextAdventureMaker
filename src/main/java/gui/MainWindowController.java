package gui;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import logic.CurrentGameManager;
import logic.JARCreator;

/**
 * Controller for the main window.
 * FIXME crash if same DB opened twice.
 * 
 * @author Satia
 */
public class MainWindowController {

	/** The window of this controller. */
	private Window window;

	/** The current game manager. */
	private CurrentGameManager currentGameManager;
	
	// All controllers that can be loaded dynamically
	private GameDetailsController gameDetailsController;
	private LocationsController locationsController;

	@FXML
	private MenuItem newMenuItem;

	@FXML
	private MenuItem openMenuItem;

	@FXML
	private MenuItem closeMenuItem;

	@FXML
	private MenuItem exportMenuItem;

	@FXML
	private Menu gameMenu;

	@FXML
	private MenuItem playMenuItem;

	@FXML
	private VBox centerPane;

	@FXML
	private BorderPane borderPane;

	/**
	 * The constructor is called before the initialize() method.
	 */
	public MainWindowController() {
		currentGameManager = new CurrentGameManager();
	}

	@FXML
	private void initialize() {
		newMenuItem.setOnAction((e) -> this.newOrOpenMenuItemClicked(true));
		openMenuItem.setOnAction((e) -> this.newOrOpenMenuItemClicked(false));
		closeMenuItem.setOnAction((e) -> this.close());
		exportMenuItem.setOnAction((e) -> this.exportMenuItemClicked());
		exportMenuItem.setDisable(true);

		gameMenu.setDisable(true);
		playMenuItem.setOnAction((e) -> this.play());
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
	 * Load the view given by the fxml into the center of the main window's
	 * border pane. With that fxml must be associated a controller implementing
	 * {@link GameDataController}.
	 * 
	 * @param fxml
	 *            The fxml file to load
	 * @param the
	 *            controller
	 */
	private void setCenterContent(String fxml, GameDataController controller) {
		try {
			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource(fxml));

			// Set the controller for the fxml
			loader.setController(controller);

			// Load the view
			Node node = loader.load();

			borderPane.setCenter(node);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Could not set the main window's center content.", e);
			
			// Unload GUI
			borderPane.setLeft(null);
			borderPane.setCenter(null);
		}
	}
	
	/**
	 * Resets the content of the center
	 */
	private void resetCenterGUI() {
		// Create all controllers
		gameDetailsController = new GameDetailsController(currentGameManager);
		locationsController = new LocationsController(currentGameManager);

		// Load game details as first view
		loadGameDetails();
	}

	/**
	 * Loads the game details into the center.
	 */
	public void loadGameDetails() {
		// Load the game details in the center of the window
		setCenterContent("view/GameDetails.fxml", gameDetailsController);
	}

	/**
	 * Loads the locations into the center.
	 */
	public void loadLocations() {
		setCenterContent("view/Locations.fxml", locationsController);
	}

	/**
	 * Load the sidebar.
	 */
	private void loadSidebar() {
		try {
			SidebarController controller = new SidebarController(this);

			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource("view/Sidebar.fxml"));

			// Set the controller for the fxml
			loader.setController(controller);

			// Load the view
			Node node = loader.load();

			borderPane.setLeft(node);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not load the main window's sidebar",
					e);
		}
	}

	/**
	 * Opens a file chooser to let the user choose the file to create/open in
	 * the application.
	 * 
	 * @param creatingNew
	 *            if a new file is being created
	 */
	private void newOrOpenMenuItemClicked(boolean creatingNew) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(creatingNew ? "Choose where to save the new file" : "Choose a game file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game databases", "*.h2.db"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));

		File file;
		if (creatingNew) {
			file = fileChooser.showSaveDialog(window);
		} else {
			file = fileChooser.showOpenDialog(window);
		}

		// Open the file, if one was chosen
		if (file != null) {
			currentGameManager.open(file);
			// Enable the menu items that need a loaded game
			exportMenuItem.setDisable(false);
			gameMenu.setDisable(false);
			
			// Load the side bar left
			loadSidebar();
			
			// Load the center stuff (last, because this can cause an unload of the whole GUI!)
			resetCenterGUI();
		}
	}



	/**
	 * Called when the export menu item is clicked.
	 */
	private void exportMenuItemClicked() {
		// A file chooser to choose destination
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose where to save the new executable game");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JAR files", "*.jar"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));

		File file = fileChooser.showSaveDialog(window);
		// Ensure JARCReator does not crash if the Game-missing-db file is not
		// present.
		if (file != null) {
			try {
				JARCreator.copyGameDBIntoGameJAR(currentGameManager.getOpenFile(), file);
			} catch (IOException e) {
				// This very probably means that the "Game_missing_db.jar" was
				// removed

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Export Error");
				alert.setHeaderText("The game file could not be exported!");
				alert.setContentText(
						"Make sure that the file \"Game_missing_db.jar\" is present in the same folder as the executable of TextAdventureMaker.");
				alert.showAndWait();
			}
			// Show a success message.
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Export Successful");
			alert.setHeaderText("The game file was exported!");
			alert.setContentText(
					"To execute the game, double click it or run 'java -jar <name-of-the-file>' in a command line.");
			alert.showAndWait();
		}
	}

	/**
	 * Starts the loaded game.
	 */
	private void play() {
		currentGameManager.playGame();
		// After starting the game, the DB has been reconnected and the GUI is in a detached state.
		// Therefore, we reset the GUI state.
		resetCenterGUI();
	}

	/**
	 * Called when the main window is closed. Cleans up and then exits.
	 */
	public void close() {
		currentGameManager.close();
		Platform.exit();
	}
}
