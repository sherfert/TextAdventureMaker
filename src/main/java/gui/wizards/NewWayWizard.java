package gui.wizards;

import java.util.Optional;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import data.Location;
import data.Way;
import gui.GameDataController;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import logic.CurrentGameManager;
import utility.WindowUtil;

/**
 * A wizard that guides the user through the creation of a Way.
 * 
 * @author satia
 */
public class NewWayWizard extends Wizard {

	private static final String NAME_KEY = "name";
	private static final String ORIGIN_KEY = "origin";
	private static final String DESTINATION_KEY = "destination";

	/** The current game manager. */
	private CurrentGameManager currentGameManager;

	public NewWayWizard(CurrentGameManager currentGameManager) {
		this.currentGameManager = currentGameManager;

		setTitle("New way");
		NewWayFlow flow = new NewWayFlow();
		setFlow(flow);

		// Set the icon using the first pane in the flow.
		WindowUtil.attachIcon((Stage) flow.chooseOriginPane.getScene().getWindow());
	}

	public NewWayWizard(CurrentGameManager currentGameManager, Location origin, Location destination) {
		this.currentGameManager = currentGameManager;
		getSettings().put(ORIGIN_KEY, origin);
		getSettings().put(DESTINATION_KEY, destination);
		
		setTitle("New way");
		NewWayFlow flow = new NewWayFlow();
		setFlow(flow);

		// Set the icon using the first pane in the flow.
		WindowUtil.attachIcon((Stage) flow.chooseNamePane.getScene().getWindow());
	}

	/**
	 * Show the wizard and obtain the new Way, if any.
	 * 
	 * @return the new way.
	 */
	public Optional<Way> showAndGet() {
		return showAndWait().map(result -> {
			Way way = null;

			if (result == ButtonType.FINISH) {
				ObservableMap<String, Object> settings = getSettings();

				way = new Way((String) settings.get(NAME_KEY), "", (Location) settings.get(ORIGIN_KEY),
						(Location) settings.get(DESTINATION_KEY));
			}
			return way;
		});
	}

	/**
	 * Flow for a wizard to create a new Way.
	 * 
	 * @author satia
	 */
	private class NewWayFlow implements Wizard.Flow {
		private ChooseStringPane chooseNamePane;
		private ChooseNamedObjectPane<Location> chooseOriginPane;
		private ChooseNamedObjectPane<Location> chooseDestinationPane;

		/**
		 * Creates all panes and defines the flow between them.
		 */
		public NewWayFlow() {
			this.chooseNamePane = new ChooseStringPane("Type a name for the new way", NAME_KEY, false,
					GameDataController::checkName);
			this.chooseOriginPane = new ChooseNamedObjectPane<>("Where should the way start?", ORIGIN_KEY);
			this.chooseOriginPane.getChooser().setNoValueString("(no location)");
			this.chooseOriginPane.getChooser().initialize(null, false, false,
					currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
						setInvalid(l == null);
					});

			this.chooseDestinationPane = new ChooseNamedObjectPane<>("Where should the way go to?", DESTINATION_KEY);
			this.chooseDestinationPane.getChooser().setNoValueString("(no location)");
			this.chooseDestinationPane.getChooser().initialize(null, false, false,
					currentGameManager.getPersistenceManager().getLocationManager()::getAllLocations, (l) -> {
						setInvalid(l == null);
					});
		}

		@Override
		public Optional<WizardPane> advance(WizardPane currentPage) {
			if (currentPage == chooseDestinationPane
					|| (getSettings().containsKey(ORIGIN_KEY) && getSettings().containsKey(DESTINATION_KEY))) {
				return Optional.of(chooseNamePane);
			} else if (currentPage == chooseOriginPane) {
				return Optional.of(chooseDestinationPane);
			} else if (currentPage == null) {
				return Optional.of(chooseOriginPane);
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
