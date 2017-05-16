package gui.wizards;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import gui.utility.Loader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Pane to choose a String.
 * 
 * @author satia
 */
public class ChooseStringPane extends WizardPane {
	private @FXML Label text;
	private @FXML TextField tf;
	private Wizard wizard;
	
	private  boolean allowEmpty;
	private String settingsKey;

	public ChooseStringPane(String labelText, String settingsKey, boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
		this.settingsKey = settingsKey;
		Loader.load(this, this, "view/wizards/ChooseString.fxml");
		text.setText(labelText);
		tf.textProperty().addListener((f, o, n) -> {
			wizard.setInvalid(!allowEmpty && n.isEmpty());
			// Cannot do this in onExitingPage: Not called if this is the last pane of a wizard
			wizard.getSettings().put(settingsKey, n);
		});	
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		this.wizard = wizard;
		wizard.setInvalid(!allowEmpty && tf.getText().isEmpty());
		wizard.getSettings().put(settingsKey, tf.getText());
	}
}
