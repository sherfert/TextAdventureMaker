package data.action;

import data.interfaces.HasId;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import persistence.PersistenceManager;

/**
 * Any action that changes something in the game (if enabled).
 * 
 * TODO ChangeInventoryItemAction
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractAction implements HasId {

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
	 * The Id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * If the action is enabled and therefore triggerable.
	 */
	@Column(nullable = false)
	protected boolean enabled;

	/**
	 * An enabled action.
	 */
	protected AbstractAction() {
		enabled = true;
	}

	/**
	 * An action.
	 * 
	 * @param enabled
	 *            if the action should be enabled
	 */
	protected AbstractAction(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
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

	/**
	 * @return the class name, id and enabled status.
	 */
	@Override
	public String toString() {
		return "AbstractAction{" + "id=" + id + ", enabled=" + enabled + '}';
	}

	/**
	 * Triggers the associated action. This logs, calls doAction if enabled, and
	 * updates changes.
	 */
	public final void triggerAction() {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Triggering action {0}", this);

		if (enabled) {
			doAction();
		}
		PersistenceManager.updateChanges();
	}

	/**
	 * Actually perform the action.
	 */
	protected abstract void doAction();

	/**
	 * Constructs a string explaining the action. This is for the GUI, whereas
	 * {@link #toString()} should be preferred for debugging.
	 * 
	 * @return a description of the action.
	 */
	public abstract String getActionDescription();
}