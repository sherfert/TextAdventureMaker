package gui;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.DBIncompatibleException;
import gui.toplevel.ActionsController;
import gui.toplevel.ConversationsController;
import gui.toplevel.GameDetailsController;
import gui.toplevel.InventoryItemsController;
import gui.toplevel.ItemsController;
import gui.toplevel.LocationsController;
import gui.toplevel.PersonsController;
import gui.toplevel.WaysController;
import gui.window.NavbarController;
import gui.window.SidebarController;
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
	private WaysController waysController;
	private ItemsController itemsController;
	private InventoryItemsController inventoryItemsController;
	private PersonsController personsController;
	private ConversationsController conversationsController;
	private ActionsController actionsController;

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
	 * @param controllerFactory
	 *            either {@code} null, or a context specific controller factory
	 */
	public void setCenterContent(String fxml, GameDataController controller,
			Callback<Class<?>, Object> controllerFactory) {
		try {
			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(fxml));

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
	 * Resets the content of the center.
	 */
	private void resetCenterGUI() {
		// Create all controllers
		gameDetailsController = new GameDetailsController(currentGameManager, this);
		locationsController = new LocationsController(currentGameManager, this);
		waysController = new WaysController(currentGameManager, this);
		itemsController = new ItemsController(currentGameManager, this);
		inventoryItemsController = new InventoryItemsController(currentGameManager, this);
		personsController = new PersonsController(currentGameManager, this);
		conversationsController = new ConversationsController(currentGameManager, this);
		actionsController = new ActionsController(currentGameManager, this);

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
	 * @param controllerFactory
	 *            either {@code} null, or a context specific controller factory
	 */
	public void pushCenterContent(String linkTitle, String fxml, GameDataController controller,
			Callback<Class<?>, Object> controllerFactory) {
		// Push to the navbar
		navbarController.push(linkTitle, fxml, controller, controllerFactory);
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
		String fxml = "view/toplevel/GameDetails.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Game Configuration", fxml, gameDetailsController, gameDetailsController::controllerFactory);
	}

	/**
	 * Loads the locations into the center.
	 */
	public void loadLocations() {
		String fxml = "view/toplevel/NameDescTableAndFields.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Locations", fxml, locationsController, locationsController::controllerFactory);
	}

	/**
	 * Loads the ways into the center.
	 */
	public void loadWays() {
		String fxml = "view/toplevel/Ways.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Ways", fxml, waysController, waysController::controllerFactory);
	}

	/**
	 * Loads the items into the center.
	 */
	public void loadItems() {
		String fxml = "view/toplevel/NameDescTableAndFields.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Items", fxml, itemsController, itemsController::controllerFactory);
	}

	/**
	 * Loads the items into the center.
	 */
	public void loadInventoryItems() {
		String fxml = "view/toplevel/NameDescTableAndFields.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Inventory items", fxml, inventoryItemsController,
				inventoryItemsController::controllerFactory);
	}

	/**
	 * Loads the persons into the center.
	 */
	public void loadPersons() {
		String fxml = "view/toplevel/NameDescTableAndFields.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Persons", fxml, personsController, personsController::controllerFactory);
	}

	/**
	 * Loads the conversations into the center.
	 */
	public void loadConversations() {
		String fxml = "view/toplevel/Conversations.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Conversations", fxml, conversationsController, conversationsController::controllerFactory);
	}

	/**
	 * Loads the actions into the center.
	 */
	public void loadActions() {
		String fxml = "view/toplevel/Actions.fxml";
		// Reset the navbar
		navbarController.reset();
		// Load the content
		pushCenterContent("Actions", fxml, actionsController, actionsController::controllerFactory);
	}

	/**
	 * Load the sidebar.
	 */
	private void loadSidebar() {
		try {
			SidebarController controller = new SidebarController(this);

			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/window/Sidebar.fxml"));

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
			loader.setLocation(getClass().getResource("view/window/Navbar.fxml"));

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
			} catch (DBIncompatibleException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "DB incompatible.", e);
				// Show error message
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Could not open the game");
				alert.setHeaderText("Propably the game was created with a newer version of TextAdventureMaker.");
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
				currentGameManager.disconnectDoReconnect(() -> {
					JARCreator.copyGameDBIntoGameJAR(currentGameManager.getOpenFile(), file);
				});
				// Show a success message.
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Export Successful");
				alert.setHeaderText("The game file was exported!");
				alert.setContentText(
						"To execute the game, double click it or run 'java -jar <name-of-the-file>' in a command line.");
				alert.showAndWait();
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
			// After that, the DB has been reconnected and the GUI is
			// in a detached state.
			// Therefore, we reset the GUI state.
			resetCenterGUI();
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
		// After that, the DB has been reconnected and the GUI is
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
