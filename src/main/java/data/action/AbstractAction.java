package data.action;

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
	@Column(nullable = false)
	protected boolean enabled;

	/**
	 * A personalized error message displayed if the action is triggered but
	 * disabled.
	 */
	protected String forbiddenText;

	/**
	 * A personalized message displayed if the action is triggered successfully.
	 */
	protected String successfulText;

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
	 * An action.
	 * 
	 * @param enabled
	 *            if the action should be enabled
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	protected AbstractAction(boolean enabled, String forbiddenText,
			String successfulText) {
		this.enabled = enabled;
		this.forbiddenText = forbiddenText;
		this.successfulText = successfulText;
	}

	/**
	 * An enabled action.
	 * 
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	protected AbstractAction(String forbiddenText, String successfulText) {
		this.forbiddenText = forbiddenText;
		this.successfulText = successfulText;
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
	 * @return the successfulText
	 */
	public String getSuccessfulText() {
		return successfulText;
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
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setSuccessfulText(String successfulText) {
		this.successfulText = successfulText;
	}

	/**
	 * Triggers the associated action. Should act according to the enabled
	 * status.
	 */
	public abstract void triggerAction();
}