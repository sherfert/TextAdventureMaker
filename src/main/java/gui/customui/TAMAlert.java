package gui.customui;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import utility.WindowUtil;

/**
 * An alert that does not crop text and has the correct icon.
 * 
 * @author satia
 *
 */
public class TAMAlert extends Alert {

	public TAMAlert(AlertType arg0) {
		super(arg0);
		getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		WindowUtil.attachIcon((Stage) getDialogPane().getScene().getWindow());
	}

}
