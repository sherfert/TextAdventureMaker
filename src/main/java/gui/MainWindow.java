package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import playing.menu.LoadSaveManager;
import utility.WindowUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Opens the GUI of TextAdventureMaker.
 * 
 * TODO more headlines, so you know better what you're doing
 * 
 * TODO Font sizes in Linux
 * 
 * TODO Locale to english!
 * 
 * @author Satia
 */
public class MainWindow extends Application {

	public static void main(String[] args) {
		// Initialize the logging
		try {
			Class.forName(logging.LogManager.class.getName());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(LoadSaveManager.class.getName()).log(Level.SEVERE, "Could not initialize logging:", e);
		}

		Application.launch(args);
	}

	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("TextAdventureMaker");

		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/MainWindow.fxml"));

			rootLayout = (BorderPane) loader.load();

			MainWindowController controller = loader.getController();
			controller.setWindow(primaryStage);
			primaryStage.setOnCloseRequest(e -> controller.close());

			// Set the icon
			Image img = new Image(WindowUtil.getWindowIconURL().toString());
			primaryStage.getIcons().add(img);

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			// Apply application wide css to the whole scene
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
