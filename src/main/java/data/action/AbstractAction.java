package data.action;

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
public abstract class AbstractAction {
	/**
	 * The Id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * If the action is enabled and therefore triggerable.
	 */
	protected boolean enabled;

	/**
	 * An personalized error message displayed if the action is triggered but
	 * disabled.
	 */
	protected String forbiddenText;

	/**
	 * By default actions are enabled.
	 */
	protected AbstractAction() {
		enabled = true;
	}

	/**
	 * @return the forbiddenText
	 */
	public String getForbiddenText() {
		return forbiddenText;
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
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setForbiddenText(String forbiddenText) {
		this.forbiddenText = forbiddenText;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Triggers the associated action without checking preconditions. Should act
	 * according to the enabled status.
	 */
	public abstract void triggerAction();
}