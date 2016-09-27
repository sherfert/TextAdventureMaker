package gui.customui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;

import data.interfaces.HasName;
import exception.DBClosedException;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Abstract superclass for all Choosers of a single NamedObject from the
 * Database. Each instance must be initialized with
 * {@link #initialize(HasName, boolean, boolean, ValuesSupplier, Consumer)}
 * before it behaves properly.
 * 
 * TODO delete all these subclasses! (also from listview?)
 * 
 * @author Satia
 *
 * @param <E>
 *            the concrete subtype that should be managed by inheriting choosers
 */
public class NamedObjectChooser<E extends HasName> extends TextField {

	/** All available values to choose from */
	private List<E> availableValues;

	/** The currently selected value */
	private E currentSelection;

	/** The String to display if no value is chosen. */
	private String noValueString;

	/** Whether null is an allowed value in the given context */
	private boolean allowNull;

	/** A method to supply the available values */
	private ValuesSupplier<E> getAvailableValues;
	
	/**
	 * Chooser with standard string
	 */
	public NamedObjectChooser() {
		this("(no object)");
	}

	/**
	 * @param noValueString
	 *            the String to display if no value is chosen.
	 */
	public NamedObjectChooser(String noValueString) {
		this.noValueString = noValueString;
	}

	/**
	 * Converts between values and Strings
	 */
	private StringConverter<E> valueConverter = new StringConverter<E>() {
		@Override
		public String toString(E e) {
			if (e == null) {
				return noValueString;
			} else {
				/** See the implementation of NamedObject.toString() */
				return e.toString();
			}
		}

		@Override
		public E fromString(String s) {
			if (s.equals(noValueString)) {
				return null;
			}
			Pattern regex = Pattern.compile(".*?(\\d+)$");
			Matcher matcher = regex.matcher(s);

			if (matcher.matches()) {
				String match = matcher.group(1);

				int id = Integer.parseInt(match);
				// Go through the available values and select the one with the
				// found ID
				for (E val : availableValues) {
					if (val != null && val.getId() == id) {
						return val;
					}
				}
			}
			return null;
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
	public void setObjectValue(E e) {
		this.currentSelection = e;
		this.setText(this.valueConverter.toString(this.currentSelection));
	}

	/**
	 * Initializes this chooser.
	 * 
	 * @param initialValue
	 *            the initial value to choose. {@code null} allowed, even if
	 *            {@code allowNull} is set to false
	 * @param allowNull
	 *            whether null is an allowed value in the given context
	 * @param updateAvailableValuesOnFocus
	 *            whether the available values should be re-obtained each time
	 *            the chooser gets focused.
	 * @param getAvailableValues
	 *            a method to supply the available values
	 * @param newValueChosenAction
	 *            the action executed when a new location is chosen
	 */
	public void initialize(E initialValue, boolean allowNull, boolean updateAvailableValuesOnFocus,
			ValuesSupplier<E> getAvailableValues, Consumer<E> newValueChosenAction) {
		this.allowNull = allowNull;
		this.getAvailableValues = getAvailableValues;

		// Set initial location
		this.currentSelection = initialValue;
		this.setText(this.valueConverter.toString(initialValue));

		// Retrieve all available values
		try {
			setAvailableValues();
		} catch (DBClosedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
			return;
		}

		// Enable autocompletion with all available values
		TextFields.bindAutoCompletion(this,
				(isr) -> this.availableValues.stream()
						.filter(val -> (val == null && isr.getUserText().isEmpty()) || (val != null
								&& val.getName().toLowerCase().contains(isr.getUserText().toLowerCase())))
				.collect(Collectors.toList()), this.valueConverter);

		// Listen for text changes
		this.textProperty().addListener((f, o, n) -> {
			E newVal = this.valueConverter.fromString(n);
			if (newVal != null || allowNull) {
				this.currentSelection = newVal;
				// Execute the action when a new value is chosen
				newValueChosenAction.accept(newVal);
			}
		});

		// Listen for focus changes
		this.focusedProperty().addListener((f, o, n) -> {
			if (!n) {
				// textField lost focus
				// Make sure a value is currently typed
				E newVal = this.valueConverter.fromString(this.getText());
				if (newVal == null && !allowNull) {
					// No valid value, restore the old selection
					this.setText(this.valueConverter.toString(this.currentSelection));
				} else {
					// Valid value, but the user might have typed just the
					// id: Put the correct string in place
					this.setText(this.valueConverter.toString(newVal));
				}
			} else {
				// textField got focus
				// Update available values if specified
				if (updateAvailableValuesOnFocus) {
					try {
						setAvailableValues();
					} catch (DBClosedException e) {
						Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Abort: DB closed");
					}
				}
				// In case the current value is null, we want to clear the field
				if (this.currentSelection == null) {
					this.setText("");
				}
			}
		});
	}

	/**
	 * Sets the list of available values.
	 * 
	 * @throws DBClosedException
	 *             if the DB is closed.
	 */
	private void setAvailableValues() throws DBClosedException {
		// Copy into another list, so that changes we make, or others make,
		// are not reflected somewhere else.
		this.availableValues = new ArrayList<E>(getAvailableValues.get());
		// If we allow null, we must add it to the list
		if (allowNull) {
			this.availableValues.add(0, null);
		}
	}

}