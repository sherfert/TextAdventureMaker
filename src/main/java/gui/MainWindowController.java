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
import javafx.util.Callback;
import logic.CurrentGameManager;
import logic.JARCreator;
import playing.menu.LoadSaveManager;

/**
 * Controller for the main window.
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
	private ItemsController itemsController;

	// The navigation bar controller
	private NavbarController navbarController;

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
	 * @param controller
	 *            the controller
	 */
	public void setCenterContent(String fxml, GameDataController controller) {
		setCenterContent(fxml, controller, null);
	}

	/**
	 * Load the view given by the fxml into the center of the main window's
	 * border pane. With that fxml must be associated a controller implementing
	 * {@link GameDataController}.
	 * 
	 * @param fxml
	 *            The fxml file to load
	 * @param controller
	 *            the controller
	 * @param controllerFactory
	 *            either {@code} null, or a context specific controller factory
	 */
	public void setCenterContent(String fxml, GameDataController controller,
			Callback<Class<?>, Object> controllerFactory) {
		// Update the controller to load
		controller.update();

		try {
			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource(fxml));

			// Set the controller for the fxml
			loader.setController(controller);

			// If provided, use the controller factory
			if (controllerFactory != null) {
				loader.setControllerFactory(controllerFactory);
			}

			// Load the view
			Node node = loader.load();

			borderPane.setCenter(node);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Could not set the main window's center content.", e);

			// Unload GUI
			unloadGameRelatedGUI();
		}
	}

	/**
	 * Puts the GUI in a state like no game was loaded.
	 */
	private void unloadGameRelatedGUI() {
		borderPane.setLeft(null);
		borderPane.setCenter(null);
		borderPane.setBottom(null);
		exportMenuItem.setDisable(true);
		gameMenu.setDisable(true);
	}

	/**
	 * Resets the content of the center
	 */
	private void resetCenterGUI() {
		// Create all controllers
		gameDetailsController = new GameDetailsController(currentGameManager, this);
		locationsController = new LocationsController(currentGameManager, this);
		itemsController = new ItemsController(currentGameManager, this);

		// Load game details as first view
		loadGameDetails();
	}
	
	/**
	 * Pushes a new view onto the center.
	 * 
	 * @param linkTitle
	 *            the title for the link to this view in the navbar
	 * @param fxml
	 *            the FXML
	 * @param controller
	 *            the controller
	 */
	public void pushCenterContent(String linkTitle, String fxml, GameDataController controller) {
		pushCenterContent(linkTitle, fxml, controller, null);
	}

	/**
	 * Pushes a new view onto the center.
	 * 
	 * @param linkTitle
	 *            the title for the link to this view in the navbar
	 * @param fxml
	 *            the FXML
	 * @param controller
	 *            the controller
	 * @param controllerFactory
	 *            either {@code} null, or a context specific controller factory
	 */
	public void pushCenterContent(String linkTitle, String fxml, GameDataController controller,
			Callback<Class<?>, Object> controllerFactory) {
		// Push to the navbar
		navbarController.push(linkTitle, controller, fxml);
		// Set the content
		setCenterContent(fxml, controller, controllerFactory);
	}

	/**
	 * Pops the last controller from the center content and restores the view
	 * below that.
	 */
	public void popCenterContent() {
		navbarController.pop();
	}

	/**
	 * Loads the game details into the center.
	 */
	public void loadGameDetails() {
		String fxml = "view/GameDetails.fxml";
		// Reset the navbar
		navbarController.reset();
		navbarController.push("Game Configuration", gameDetailsController, fxml);
		// Load the content
		setCenterContent(fxml, gameDetailsController);
	}

	/**
	 * Loads the locations into the center.
	 */
	public void loadLocations() {
		String fxml = "view/Locations.fxml";
		// Reset the navbar
		navbarController.reset();
		navbarController.push("Locations", locationsController, fxml);
		// Load the content
		setCenterContent(fxml, locationsController);
	}

	/**
	 * Loads the items into the center.
	 */
	public void loadItems() {
		String fxml = "view/Items.fxml";
		// Reset the navbar
		navbarController.reset();
		navbarController.push("Items", itemsController, fxml);
		// Load the content
		setCenterContent(fxml, itemsController);
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
	 * Load the navbar.
	 */
	private void loadNavbar() {
		try {
			navbarController = new NavbarController(this);

			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource("view/Navbar.fxml"));

			// Set the controller for the fxml
			loader.setController(navbarController);

			// Load the view
			Node node = loader.load();

			borderPane.setBottom(node);
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
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Game databases", "*" + LoadSaveManager.H2_ENDING),
				new FileChooser.ExtensionFilter("All Files", "*.*"));

		File file;
		if (creatingNew) {
			file = fileChooser.showSaveDialog(window);
		} else {
			file = fileChooser.showOpenDialog(window);
		}

		// Open the file, if one was chosen
		if (file != null) {
			try {
				currentGameManager.open(file, creatingNew);
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not open db file.", e);
				// Show error message
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Could not open the game");
				alert.setHeaderText("Propably the file is already opened by another application.");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
				return;
			}

			// Enable the menu items that need a loaded game
			exportMenuItem.setDisable(false);
			gameMenu.setDisable(false);

			// Load the side bar left
			loadSidebar();
			// And the navbar top
			loadNavbar();

			// Load the center stuff (last, because this can cause an unload of
			// the whole GUI!)
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
		try {
			currentGameManager.playGame();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not copy db file.", e);
			// Show error message
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Could not start the game");
			alert.setHeaderText("While copying the game file to a temporary Location, an error ocurred:");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
		// After starting the game, the DB has been reconnected and the GUI is
		// in a detached state.
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
