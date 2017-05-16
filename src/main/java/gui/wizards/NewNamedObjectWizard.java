package gui.wizards;

import java.util.Optional;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utility.WindowUtil;

/**
 * A wizard that guides the user through the creation of a NamedObject.
 * 
 * @author satia
 */
public class NewNamedObjectWizard extends Wizard {
	

	private static final String NAME_KEY = "name";

	/**
	 * @param title
	 *            the title of the wizard
	 */
	public NewNamedObjectWizard(String title) {
		setTitle(title);
		NewNamedObjectFlow flow = new NewNamedObjectFlow();
		setFlow(flow);

		// Set the icon using the first pane in the flow.
		Image img = new Image(WindowUtil.getWindowIconURL().toString());
		Stage stage = (Stage) flow.chooseNamePane.getScene().getWindow();
		stage.getIcons().add(img);
	}

	/**
	 * Show the wizard and obtain the name for the new object, if any.
	 * 
	 * @return the new name.
	 */
	public Optional<String> showAndGetName() {
		return showAndWait().map(result -> {
			String name = null;

			if (result == ButtonType.FINISH) {
				return (String) getSettings().get(NAME_KEY);
			}
			return name;
		});
	}

	/**
	 * Flow for a wizard to create a new NamedObject.
	 * 
	 * @author satia
	 */
	private class NewNamedObjectFlow implements Wizard.Flow {
		private ChooseStringPane chooseNamePane;

		/**
		 * Creates all panes and defines the flow between them.
		 */
		public NewNamedObjectFlow() {
			this.chooseNamePane = new ChooseStringPane("Type a name", NAME_KEY, false);
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == null) {
				return Optional.of(chooseNamePane);
			}
			// As default, don't switch
			return Optional.of(currentPage);
		}

		@Override
		public boolean canAdvance(WizardPane currentPage) {
			return false;
		}

	}

}
