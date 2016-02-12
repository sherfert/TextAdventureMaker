package gui.view;

import java.io.File;
import java.io.IOException;

import gui.MainWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import logic.CurrentGameManager;
import logic.JARCreator;

public class MainWindowController {

	/** The window of this controller. */
	private Window window;

	/** The current game manager. */
	private CurrentGameManager currentGameManager;

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

	public void setCenterContent(String fxml) {
		System.out.println(borderPane);
		try {
			// Load layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindowController.class.getResource(fxml));

			//centerPane.getChildren().clear();
			//centerPane.getChildren().add(loader.load());
			borderPane.setCenter(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
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

			// Load the game details in the center of the window
			setCenterContent("GameDetails.fxml");
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

	/**
	 * Called when the main window is closed. Cleans up and then exits.
	 */
	public void close() {
		currentGameManager.close();
		Platform.exit();
	}

	/**
	 * Starts the loaded game.
	 */
	public void play() {
		currentGameManager.playGame();
	}
}
