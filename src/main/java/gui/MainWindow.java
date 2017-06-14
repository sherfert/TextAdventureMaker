package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utility.WindowUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Opens the GUI of TextAdventureMaker.
 * 
 * @author Satia
 */
public class MainWindow extends Application {

	public static void main(String[] args) {
		// Language is English
		Locale.setDefault(new Locale("en", "EN"));
		
		// Initialize the logging
		try {
			Class.forName(logging.LoggingManager.class.getName());
		} catch (ClassNotFoundException e) {
			Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, "Could not initialize logging:", e);
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
			WindowUtil.attachIcon(primaryStage);

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			// Apply application wide css to the whole scene
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Could not load MainWindow.", e);
		}
	}

}
