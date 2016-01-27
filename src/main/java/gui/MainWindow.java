package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import logic.JARCreator;
import configuration.PropertiesReader;

/**
 * Test class to have a GUI and a playing main class and different JARs.
 * 
 * @author Satia
 *
 */
public class MainWindow extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String gameDB = PropertiesReader.DIRECTORY + "Test-Adventure"
				+ ".h2.db";
		// Copy our TestAdventure into the Game_missing_db.jar
		JARCreator.copyGameDBIntoGameJAR(gameDB);
		
		launch(args);
	}

}
