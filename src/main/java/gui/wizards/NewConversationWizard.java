package gui.wizards;

import java.util.Optional;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import data.Conversation;
import gui.GameDataController;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utility.WindowUtil;

/**
 * A wizard that guides the user through the creation of a Conversation.
 * 
 * @author satia
 */
public class NewConversationWizard extends Wizard {

	private static final String NAME_KEY = "name";
	private static final String GREETING_KEY = "greeting";
	private static final String EVENT_KEY = "event";

	public NewConversationWizard() {
		setTitle("New conversation");
		NewConversationFlow flow = new NewConversationFlow();
		setFlow(flow);

		// Set the icon using the first pane in the flow.
		Image img = new Image(WindowUtil.getWindowIconURL().toString());
		Stage stage = (Stage) flow.chooseGreetingPane.getScene().getWindow();
		stage.getIcons().add(img);
	}

	/**
	 * Show the wizard and obtain the new ConversationOption, if any.
	 * 
	 * @return the new ConversationOption.
	 */
	public Optional<Conversation> showAndGet() {
		return showAndWait().map(result -> {
			Conversation co = null;

			if (result == ButtonType.FINISH) {
				ObservableMap<String, Object> settings = getSettings();

				co = new Conversation((String) settings.get(NAME_KEY), (String) settings.get(GREETING_KEY),
						(String) settings.get(EVENT_KEY));
			}
			return co;
		});
	}

	/**
	 * Flow for a wizard to create a new Conversation.
	 * 
	 * @author satia
	 */
	private class NewConversationFlow implements Wizard.Flow {
		private ChooseStringPane chooseNamePane;
		private ChooseStringPane chooseGreetingPane;
		private ChooseStringPane chooseEventPane;

		/**
		 * Creates all panes and defines the flow between them.
		 */
		public NewConversationFlow() {
			this.chooseNamePane = new ChooseStringPane("Type a name for the new conversation option", NAME_KEY, false,
					GameDataController::checkName);
			this.chooseGreetingPane = new ChooseStringPane("How does the person greet the player?", GREETING_KEY, false,
					null);
			this.chooseEventPane = new ChooseStringPane("Describe any additional events (optional)", EVENT_KEY, true,
					null);
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == null) {
				return Optional.of(chooseGreetingPane);
			} else if (currentPage == chooseGreetingPane) {
				return Optional.of(chooseEventPane);
			} else if (currentPage == chooseEventPane) {
				return Optional.of(chooseNamePane);
			}

			// As default, don't switch
			return Optional.of(currentPage);
		}

		@Override
		public boolean canAdvance(WizardPane currentPage) {
			return currentPage != chooseNamePane;
		}

	}
}
