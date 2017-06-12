package gui.wizards;

import java.util.Optional;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import data.ConversationLayer;
import data.ConversationOption;
import gui.GameDataController;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utility.WindowUtil;

/**
 * A wizard that guides the user through the creation of a ConversationOption.
 * 
 * @author satia
 */
public class NewConversationOptionWizard extends Wizard {

	private static final String NAME_KEY = "name";
	private static final String TEXT_KEY = "text";
	private static final String ANSWER_KEY = "answer";
	private static final String EVENT_KEY = "event";
	private static final String TARGET_KEY = "target";
	private ConversationLayer layer;

	public NewConversationOptionWizard(ConversationLayer layer) {
		this.layer = layer;

		setTitle("New conversation option");
		NewConversationOptionFlow flow = new NewConversationOptionFlow();
		setFlow(flow);

		// Set the icon using the first pane in the flow.
		Image img = new Image(WindowUtil.getWindowIconURL().toString());
		Stage stage = (Stage) flow.chooseTextPane.getScene().getWindow();
		stage.getIcons().add(img);
	}

	/**
	 * Show the wizard and obtain the new ConversationOption, if any.
	 * 
	 * @return the new ConversationOption.
	 */
	public Optional<ConversationOption> showAndGet() {
		return showAndWait().map(result -> {
			ConversationOption co = null;

			if (result == ButtonType.FINISH) {
				ObservableMap<String, Object> settings = getSettings();

				co = new ConversationOption((String) settings.get(NAME_KEY), (String) settings.get(TEXT_KEY),
						(String) settings.get(ANSWER_KEY), (String) settings.get(EVENT_KEY),
						(ConversationLayer) settings.get(TARGET_KEY));
			}
			return co;
		});
	}

	/**
	 * Flow for a wizard to create a new ConversationOption.
	 * 
	 * @author satia
	 */
	private class NewConversationOptionFlow implements Wizard.Flow {
		private ChooseStringPane chooseNamePane;
		private ChooseStringPane chooseTextPane;
		private ChooseStringPane chooseAnswerPane;
		private ChooseStringPane chooseEventPane;
		private ChooseNamedObjectPane<ConversationLayer> chooseTargetPane;

		/**
		 * Creates all panes and defines the flow between them.
		 */
		public NewConversationOptionFlow() {
			this.chooseNamePane = new ChooseStringPane("Type a name for the new conversation option", NAME_KEY, false,
					GameDataController::checkName);
			this.chooseTextPane = new ChooseStringPane("What does the player say?", TEXT_KEY, false, null);
			this.chooseAnswerPane = new ChooseStringPane("What is the answer the player gets?", ANSWER_KEY, false,
					null);
			this.chooseEventPane = new ChooseStringPane("Describe any additional events (optional)", EVENT_KEY, true,
					null);
			this.chooseTargetPane = new ChooseNamedObjectPane<>(
					"To which layer should the conversation switch afterwards?\n", TARGET_KEY);
			this.chooseTargetPane.getChooser().setNoValueString("(ends conversation)");
			this.chooseTargetPane.getChooser().initialize(layer, true, false, () -> layer.getConversation().getLayers(),
					(l) -> {
					});
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == null) {
				return Optional.of(chooseTextPane);
			} else if (currentPage == chooseTextPane) {
				return Optional.of(chooseAnswerPane);
			} else if (currentPage == chooseAnswerPane) {
				return Optional.of(chooseEventPane);
			} else if (currentPage == chooseEventPane) {
				return Optional.of(chooseTargetPane);
			} else if (currentPage == chooseTargetPane) {
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
