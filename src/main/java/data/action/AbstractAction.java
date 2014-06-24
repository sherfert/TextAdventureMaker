package data.action;

import data.interfaces.HasId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Any action that changes something in the game (if enabled).
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractAction implements HasId{

	/**
	 * When an action changes something that can be disabled or enabled, a
	 * three-value-type is needed to model that.
	 * 
	 * @author Satia
	 */
	public enum Enabling {
		DISABLE, DO_NOT_CHANGE, ENABLE;
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

	/**
	 * @return the id
	 */
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
	 * @param enabled
	 *            the enabled to set
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
	 * Triggers the associated action. Should act according to the enabled
	 * status.
	 */
	public abstract void triggerAction();
}