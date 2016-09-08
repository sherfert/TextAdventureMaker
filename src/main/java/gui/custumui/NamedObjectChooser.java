package gui.custumui;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

import data.NamedObject;
import exception.DBClosedException;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import logic.CurrentGameManager;

/**
 * Abstract superclass for all Choosers of a single NamedObject from the
 * Database.
 * 
 * @author Satia
 *
 * @param <E>
 *            the concrete subtype that should be managed by inheriting choosers
 */
public abstract class NamedObjectChooser<E extends NamedObject> extends TextField {

	/** The current game manager */
	protected CurrentGameManager currentGameManager;

	/** All available values to choose from */
	private List<E> availableValues;

	/** The currently selected value */
	private E currentSelection;

	/** Whether null is an allowed value in the given context */
	private boolean allowNull;

	/** The action executed when a new value is chosen */
	private Consumer<E> newValueChosenAction;

	/** The class of the generic type argument */
	private Class<E> valueClass;

	/**
	 * @param valueClass
	 *            the concrete class to manage
	 */
	public NamedObjectChooser(Class<E> valueClass) {
		this.valueClass = valueClass;
	}

	/**
	 * Converts between values and Strings
	 */
	protected StringConverter<E> valueConverter = new StringConverter<E>() {
		@Override
		public String toString(E loc) {
			if (loc == null) {
				// TODO E
				return "(no location)";
			} else {
				return loc.getName() + " - ID: " + loc.getId();
			}
		}

		@Override
		public E fromString(String s) {
			if (s.equals("(no location)")) {
				return null;
			}
			Pattern regex = Pattern.compile(".*?(\\d+)$");
			Matcher matcher = regex.matcher(s);

			if (matcher.matches()) {
				String match = matcher.group(1);

				int id = Integer.parseInt(match);
				try {
					return currentGameManager.getPersistenceManager().getAllObjectsManager().getObject(valueClass, id);
				} catch (DBClosedException e) {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
							"Cannot convert string to value: DB closed");
					return null;
				}
			} else {
				return null;
			}
		}
	};

	/**
	 * @return the current value, casted to the NamedObject subclass
	 */
	public E getObjectValue() {
		return this.currentSelection;
	}

	/**
	 * Sets the new value. Accepts {@code null} even if {@code allowNull} was
	 * set to false. Note that in this case, the newValueChosenAction will NOT
	 * be executed for {@code null} values.
	 * 
	 * @param e
	 *            the new value.
	 */
	public void setLocationValue(E e) {
		this.currentSelection = e;
		this.setText(valueConverter.toString(this.currentSelection));
	}

	/**
	 * Initializes this chooser.
	 * 
	 * @param initialValue
	 *            the initial value to choose. {@code null} allowed, even if
	 *            {@code allowNull} is set to false
	 * @param allowNull
	 *            whether null is an allowed value in the given context
	 * @param currentGameManager
	 *            the current game manager
	 * @param newValueChosenAction
	 *            the action executed when a new location is chosen
	 */
	public void initialize(E initialValue, boolean allowNull, CurrentGameManager currentGameManager,
			Consumer<E> newValueChosenAction) {
		this.currentGameManager = currentGameManager;
		this.newValueChosenAction = newValueChosenAction;
		this.allowNull = allowNull;

		// Retrieve all available values
		try {
			this.availableValues = getAvailableValues();
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}
		// If we allow null, we must add it to the list
		if (this.allowNull) {
			this.availableValues.add(0, null);
		}

		// Enable autocompletion with all available values
		TextFields.bindAutoCompletion(this,
				(isr) -> this.availableValues.stream()
						.filter(val -> (val == null && isr.getUserText().isEmpty()) || (val != null
								&& val.getName().toLowerCase().contains(isr.getUserText().toLowerCase())))
				.collect(Collectors.toList()), valueConverter);

		// Set initial location
		this.currentSelection = initialValue;
		this.setText(valueConverter.toString(initialValue));

		// Listen for text changes
		this.textProperty().addListener((f, o, n) -> {
			E newVal = valueConverter.fromString(n);
			if (newVal != null || this.allowNull) {
				this.currentSelection = newVal;
				// Execute the action when a new value is chosen
				this.newValueChosenAction.accept(newVal);
			}
		});

		// If the text field loses focus, make sure a value is currently
		// typed
		this.focusedProperty().addListener((f, o, n) -> {
			if (!n) {
				// textField lost focus
				E newVal = valueConverter.fromString(this.getText());
				if (newVal == null && !this.allowNull) {
					// No valid value, restore the old selection
					this.setText(this.valueConverter.toString(this.currentSelection));
				} else {
					// Valid value, but the user might have typed just the
					// id: Put the correct string in place
					this.setText(this.valueConverter.toString(newVal));
				}
			}
		});
	}

	/**
	 * Retrieve all available values from the Database.
	 * 
	 * @return the values to choose from.
	 * @throws DBClosedException
	 *             of the Database is closed.
	 */
	protected abstract List<E> getAvailableValues() throws DBClosedException;

}