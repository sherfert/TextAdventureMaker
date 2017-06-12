package gui.wizards;

import java.util.function.Function;

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

	private boolean allowEmpty;
	private String settingsKey;
	private Function<String, Boolean> stringChecker;

	public ChooseStringPane(String labelText, String settingsKey, boolean allowEmpty,
			Function<String, Boolean> stringChecker) {
		this.allowEmpty = allowEmpty;
		this.settingsKey = settingsKey;
		this.stringChecker = stringChecker;
		Loader.load(this, this, "view/wizards/ChooseString.fxml");
		text.setText(labelText);
		tf.textProperty().addListener((f, o, n) -> {
			wizard.setInvalid(!isValid(n));
			// Cannot do this in onExitingPage: Not called if this is the last
			// pane of a wizard
			wizard.getSettings().put(settingsKey, n);
		});
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		this.wizard = wizard;
		wizard.setInvalid(!isValid(tf.getText()));
		wizard.getSettings().put(settingsKey, tf.getText());
	}

	private boolean isValid(String s) {
		return (allowEmpty || !s.isEmpty()) && (stringChecker == null || stringChecker.apply(s));
	}
}
