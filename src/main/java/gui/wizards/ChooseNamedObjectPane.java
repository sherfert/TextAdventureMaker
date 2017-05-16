package gui.wizards;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import data.NamedObject;
import gui.customui.NamedObjectChooser;
import gui.utility.Loader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * A pane to choose a NamedObject. Configurable with the label to display, etc.
 * 
 * @author satia
 */
public class ChooseNamedObjectPane<E extends NamedObject> extends WizardPane {
	private @FXML NamedObjectChooser<E> chooser;
	private @FXML Label text;

	private String settingsKey;

	/**
	 * After the constructor, the chooser must be initialized by the creator.
	 * 
	 * @param currentGameManager
	 * @param labelText
	 *            the descriptive text above the chooser.
	 * @param settingsKey
	 */
	public ChooseNamedObjectPane(String labelText, String settingsKey) {
		Loader.load(this, this, "view/wizards/ChooseNamedObject.fxml");
		text.setText(labelText);
		this.settingsKey = settingsKey;
	}

	/**
	 * @return the chooser
	 */
	public NamedObjectChooser<E> getChooser() {
		return chooser;
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		wizard.setInvalid(chooser.getObjectValue() == null && !chooser.isAllowNull());
	}

	@Override
	public void onExitingPage(Wizard wizard) {
		wizard.getSettings().put(this.settingsKey, chooser.getObjectValue());
	}
}
