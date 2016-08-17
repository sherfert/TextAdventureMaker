package data.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import data.Game;
import data.NamedObject;

/**
 * Any action that changes something in the game (if enabled).
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.PROPERTY)
public abstract class AbstractAction extends NamedObject {

	/**
	 * When an action changes something that can be disabled or enabled, a
	 * three-value-type is needed to model that.
	 * 
	 * @author Satia
	 */
	public enum Enabling {
		/** Disable the object */
		DISABLE("Disabling"),
		/** Do not change if the object is enabled */
		DO_NOT_CHANGE("Neither enabling or disabling"),
		/** Enable the object */
		ENABLE("Enabling");

		/**
		 * A text describing what this Enabling value does.
		 */
		public final String description;

		/**
		 * @param description
		 *            a text describing what this Enabling value does
		 */
		private Enabling(String description) {
			this.description = description;
		}
	}

	/**
	 * If the action is enabled and therefore triggerable.
	 */
	protected boolean enabled;
	
	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #AbstractAction(String)} instead.
	 */
	@Deprecated
	protected AbstractAction() {
		enabled = true;
	}

	/**
	 * An enabled action.
	 * 
	 * @param name
	 *            the name
	 */
	protected AbstractAction(String name) {
		super(name);
		enabled = true;
	}

	/**
	 * @return the enabled
	 */
	@Column(nullable = false)
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables an action.
	 * 
	 * @param enabled
	 *            whether to enable or disable the action
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "AbstractAction{" + "enabled=" + enabled + " " + super.toString() + '}';
	}

	/**
	 * Triggers the associated action. This logs, calls doAction if enabled, and
	 * updates changes.
	 * 
	 * @param game
	 *            a reference to the game, needed for some actions.
	 */
	public final void triggerAction(Game game) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Triggering action {0}", this);

		if (enabled) {
			doAction(game);
		}
	}

	/**
	 * Actually perform the action.
	 * 
	 * @param game
	 *            a reference to the game, needed for some actions.
	 */
	protected abstract void doAction(Game game);

	/**
	 * Constructs a string explaining the action. This is for the GUI, whereas
	 * {@link #toString()} should be preferred for debugging.
	 * 
	 * @return a description of the action.
	 */
	public abstract String actionDescription();
}