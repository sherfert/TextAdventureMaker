package gui.itemEditing.action;

import java.util.function.Consumer;
import java.util.function.Supplier;

import data.action.AbstractAction;
import data.action.AbstractAction.Enabling;
import data.interfaces.HasName;
import gui.GameDataController;
import gui.MainWindowController;
import gui.customui.NamedObjectChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import logic.CurrentGameManager;

/**
 * Abstract base class for all controllers editing a single action. They all
 * share the same remove button handler.
 * 
 * XXX style change action views better!
 * 
 * @author Satia
 *
 * @param <E>
 *            the concrete action subclass
 */
public abstract class ActionController<E extends AbstractAction> extends GameDataController {

	/** The action */
	protected E action;

	@FXML
	protected Button removeButton;

	/**
	 * @param currentGameManager
	 *            the game manager
	 * @param mwController
	 *            the main window controller
	 * @param action
	 *            the action to edit
	 */
	public ActionController(CurrentGameManager currentGameManager, MainWindowController mwController, E action) {
		super(currentGameManager, mwController);
		this.action = action;
	}

	@FXML
	protected void initialize() {
		removeButton.setOnMouseClicked((e) -> removeObject(action, "Deleting an action",
				"Do you really want to delete this action?", "Other action enabling or disabling this action will also be deleted."));
	}

	/**
	 * Initializes a radio button group that controls one {@link Enabling} of an
	 * action.
	 * 
	 * @param group
	 *            the group
	 * @param doNotChangeRB
	 *            the radio button to select {@link Enabling#DO_NOT_CHANGE}
	 * @param enableRB
	 *            the radio button to select {@link Enabling#ENABLE}
	 * @param disableRB
	 *            the radio button to select {@link Enabling#DISABLE}
	 * @param getter
	 *            the getter for the current value
	 * @param setter
	 *            the setter to propagate a new value
	 */
	public static void initRadioButtonEnablingGroup(ToggleGroup group, RadioButton doNotChangeRB, RadioButton enableRB,
			RadioButton disableRB, Supplier<Enabling> getter, Consumer<Enabling> setter) {
		switch (getter.get()) {
		case DISABLE:
			group.selectToggle(disableRB);
			break;
		case DO_NOT_CHANGE:
			group.selectToggle(doNotChangeRB);
			break;
		case ENABLE:
			group.selectToggle(enableRB);
			break;
		}
		group.selectedToggleProperty().addListener((f, o, n) -> {
			if (n == doNotChangeRB) {
				setter.accept(Enabling.DO_NOT_CHANGE);
			} else if (n == enableRB) {
				setter.accept(Enabling.ENABLE);
			} else if (n == disableRB) {
				setter.accept(Enabling.DISABLE);
			}
		});
	}

	/**
	 * Initializes a CheckBox and a TextField that work together to change or
	 * not change a String value.
	 * 
	 * @param checkBox
	 *            the CheckBox
	 * @param textField
	 *            the TextField
	 * @param getter
	 *            the getter for the current value
	 * @param setter
	 *            the setter to propagate a new value
	 */
	public static void initCheckBoxAndTextFieldSetter(CheckBox checkBox, TextInputControl textField,
			Supplier<String> getter, Consumer<String> setter) {
		textField.setText(getter.get());
		textField.disableProperty().bind(checkBox.selectedProperty().not());
		textField.textProperty().addListener((f, o, n) -> {
			setter.accept(n);
		});
		checkBox.setSelected(getter.get() != null);
		checkBox.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				setter.accept(null);
				textField.setText("");
			}
		});
	}

	/**
	 * Initializes a CheckBox and a Chooser that work together to change or not
	 * change a NamedObject value.
	 * 
	 * @param checkBox
	 *            the CheckBox
	 * @param chooser
	 *            the Chooser
	 * @param getter
	 *            the getter for the current value
	 * @param setter
	 *            the setter to propagate a new value
	 */
	public static void initCheckBoxAndChooser(CheckBox checkBox, NamedObjectChooser<?> chooser,
			Supplier<Boolean> changeGetter, Consumer<Boolean> changeSetter) {
		chooser.disableProperty().bind(checkBox.selectedProperty().not());
		checkBox.setSelected(changeGetter.get());
		checkBox.selectedProperty().addListener((f, o, n) -> {
			changeSetter.accept(n);
		});
	}

	/**
	 * Initializes a CheckBox and a Chooser that work together to change or not
	 * change a NamedObject value, where {@code null} is not a valid new value
	 * but an indicator to not change the field.
	 * 
	 * @param checkBox
	 *            the CheckBox
	 * @param chooser
	 *            the Chooser
	 * @param getter
	 *            the getter to see if changes should be applied
	 * @param setter
	 *            the setter to tell if changes should be applied
	 */
	public static <E extends HasName> void initCheckBoxAndChooserNoNull(CheckBox checkBox,
			NamedObjectChooser<E> chooser, Supplier<E> getter, Consumer<E> setter) {
		chooser.disableProperty().bind(checkBox.selectedProperty().not());
		checkBox.setSelected(getter.get() != null);
		checkBox.selectedProperty().addListener((f, o, n) -> {
			if (!n) {
				setter.accept(null);
			}
		});
	}
	
	@Override
	public boolean isObsolete() {
		return !currentGameManager.getPersistenceManager().isManaged(action);
	}

}