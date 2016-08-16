package gui.custumui;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

import data.Location;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing locations. Must be initialized with
 * {@link LocationChooser#initialize(List, Location, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * 
 * @author satia
 */
public class LocationChooser extends TextField {

	/** All available location to choose from */
	private List<Location> availableLocations;

	/** The curret game manager */
	private CurrentGameManager currentGameManager;

	/** The currently selected location */
	private Location currentSelection;

	/** The action executed when a new location is chosen */
	private Consumer<Location> newLocationChosenAction;

	/**
	 * Converts between locations and Strings
	 */
	private StringConverter<Location> locationConverter = new StringConverter<Location>() {
		@Override
		public String toString(Location loc) {
			if (loc == null) {
				return "";
			} else {
				return loc.getName() + " - ID: " + loc.getId();
			}
		}

		@Override
		public Location fromString(String s) {
			Pattern regex = Pattern.compile(".*?(\\d+)$");
			Matcher matcher = regex.matcher(s);

			if (matcher.matches()) {
				String match = matcher.group(1);

				int id = Integer.parseInt(match);
				return currentGameManager.getPersistenceManager().getAllObjectsManager().getObject(Location.class, id);
			} else {
				return null;
			}
		}
	};

	/**
	 * Initializes this location chooser.
	 * 
	 * @param initialLocation
	 *            the initial location to choose
	 * @param currentGameManager
	 *            the current game manager
	 * @param newLocationChosenAction
	 *            the action executed when a new location is chosen
	 */
	public void initialize(Location initialLocation,
			CurrentGameManager currentGameManager, Consumer<Location> newLocationChosenAction) {
		this.currentGameManager = currentGameManager;
		this.newLocationChosenAction = newLocationChosenAction;
		
		// Retrieve all available locations
		this.availableLocations = this.currentGameManager.getPersistenceManager().getLocationManager().getAllLocations();

		// Enable autocompletion with all available locations
		TextFields.bindAutoCompletion(this,
				(isr) -> this.availableLocations.stream()
						.filter(loc -> loc.getName().toLowerCase().contains(isr.getUserText().toLowerCase()))
						.collect(Collectors.toList()),
				locationConverter);

		// Set initial location
		this.currentSelection = initialLocation;
		this.setText(locationConverter.toString(initialLocation));

		// Listen for text changes
		this.textProperty().addListener((f, o, n) -> {
			Location newLoc = locationConverter.fromString(n);
			if (newLoc != null) {
				this.currentSelection = newLoc;
				// Execute the action when a new location is chosen
				this.newLocationChosenAction.accept(newLoc);
			}
		});

		// If the text field loses focus, make sure a location is currently
		// typed
		this.focusedProperty().addListener((f, o, n) -> {
			if (!n) {
				// textField lost focus
				Location newLoc = locationConverter.fromString(this.getText());
				if (newLoc == null) {
					// No valid location, restore the old selection
					this.setText(locationConverter.toString(currentSelection));
				} else {
					// Valid location, but the user might have typed just the
					// id:
					// Put the correct string in place
					this.setText(locationConverter.toString(newLoc));
				}
			}
		});
	}

}
