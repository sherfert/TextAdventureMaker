package gui.custumui;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

import data.Location;
import exception.DBClosedException;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import logic.CurrentGameManager;

/**
 * Custom TextField for choosing locations. Must be initialized with
 * {@link LocationChooser#initialize(List, Location, CurrentGameManager, Consumer)}
 * , otherwise it behaves just as a normal TextField.
 * 
 * @author satia
 */
public class LocationChooser extends TextField {

	/** All available location to choose from */
	private List<Location> availableLocations;

	/** The current game manager */
	private CurrentGameManager currentGameManager;

	/** The currently selected location */
	private Location currentSelection;

	/** The action executed when a new location is chosen */
	private Consumer<Location> newLocationChosenAction;

	/** Whether null is an allowed location in the given context */
	private boolean allowNull;

	/**
	 * Converts between locations and Strings
	 */
	private StringConverter<Location> locationConverter = new StringConverter<Location>() {
		@Override
		public String toString(Location loc) {
			if (loc == null) {
				return "(no location)";
			} else {
				return loc.getName() + " - ID: " + loc.getId();
			}
		}

		@Override
		public Location fromString(String s) {
			if (s.equals("(no location)")) {
				return null;
			}
			Pattern regex = Pattern.compile(".*?(\\d+)$");
			Matcher matcher = regex.matcher(s);

			if (matcher.matches()) {
				String match = matcher.group(1);

				int id = Integer.parseInt(match);
				try {
					return currentGameManager.getPersistenceManager().getAllObjectsManager().getObject(Location.class,
							id);
				} catch (DBClosedException e) {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
							"Cannot convert string to location: DB closed");
					return null;
				}
			} else {
				return null;
			}
		}
	};

	/**
	 * Initializes this location chooser.
	 * 
	 * @param initialLocation
	 *            the initial location to choose. {@code null} allowed, even if
	 *            {@code allowNull} is set to false
	 * @param allowNull
	 *            whether null is an allowed location in the given context
	 * @param currentGameManager
	 *            the current game manager
	 * @param newLocationChosenAction
	 *            the action executed when a new location is chosen
	 */
	public void initialize(Location initialLocation, boolean allowNull, CurrentGameManager currentGameManager,
			Consumer<Location> newLocationChosenAction) {
		this.currentGameManager = currentGameManager;
		this.newLocationChosenAction = newLocationChosenAction;
		this.allowNull = allowNull;

		// Retrieve all available locations
		try {
			this.availableLocations = this.currentGameManager.getPersistenceManager().getLocationManager()
					.getAllLocations();
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}
		// If we allow null, we must add it to the list
		if (this.allowNull) {
			this.availableLocations.add(0, null);
		}

		// Enable autocompletion with all available locations
		TextFields.bindAutoCompletion(this,
				(isr) -> this.availableLocations.stream()
						.filter(loc -> (loc == null && isr.getUserText().isEmpty()) || (loc != null
								&& loc.getName().toLowerCase().contains(isr.getUserText().toLowerCase())))
				.collect(Collectors.toList()), locationConverter);

		// Set initial location
		this.currentSelection = initialLocation;
		this.setText(locationConverter.toString(initialLocation));

		// Listen for text changes
		this.textProperty().addListener((f, o, n) -> {
			Location newLoc = locationConverter.fromString(n);
			if (newLoc != null || this.allowNull) {
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
				if (newLoc == null && !this.allowNull) {
					// No valid location, restore the old selection
					this.setText(this.locationConverter.toString(this.currentSelection));
				} else {
					// Valid location, but the user might have typed just the
					// id: Put the correct string in place
					this.setText(this.locationConverter.toString(newLoc));
				}
			}
		});
	}

	/**
	 * @return the current value as a location
	 */
	public Location getLocationValue() {
		return this.currentSelection;
	}

	/**
	 * Sets the new location value. Accepts {@code null} even if
	 * {@code allowNull} was set to false. Note that in this case, the
	 * newLocationChosenAction will NOT be executed for {@code null} values.
	 * 
	 * @param l
	 *            the new location.
	 */
	public void setLocationValue(Location l) {
		this.currentSelection = l;
		this.setText(locationConverter.toString(this.currentSelection));
	}

}
