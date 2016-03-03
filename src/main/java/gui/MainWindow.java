package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Test class to have a GUI and a playing main class and different JARs.
 * 
 * @author Satia
 *
 */
public class MainWindow extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("TextAdventureMaker");

		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainWindow.class.getResource("view/MainWindow.fxml"));

			rootLayout = (BorderPane) loader.load();

			MainWindowController controller = loader.getController();
			controller.setWindow(primaryStage);
			primaryStage.setOnCloseRequest(e -> controller.close());

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