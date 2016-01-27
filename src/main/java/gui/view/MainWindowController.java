package gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class MainWindowController {

	@FXML
	private MenuItem openMenuItem;
	
	@FXML
	private MenuItem exportMenuItem;
	
	@FXML
	private MenuItem closeMenuItem;
	
	/**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public MainWindowController() {
		System.out.println("Loading1");
    }
	
	@FXML
	private void initialize() {
		System.out.println("Loading");
		closeMenuItem.setOnAction(this::handleClose);
	}
	
	private void handleClose(ActionEvent event) {
		System.out.println("Closing");
	}
}
