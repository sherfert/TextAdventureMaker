package data.action;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import data.InspectableObject;
import data.UsableObject;

/**
 * An action changing properties of a {@link UsableObject}.
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ChangeUsableObjectAction extends ChangeInspectableObjectAction {

	/**
	 * The new useForbiddenText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newUseForbiddenText;

	/**
	 * The new useSuccessfulText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newUseSuccessfulText;

	/**
	 * Enabling or disabling if the UsableObject is actually usable.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enabling;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link ChangeUsableObjectAction#ChangeUsableObjectAction(InspectableObject)}
	 *             instead.
	 */
	@Deprecated
	public ChangeUsableObjectAction() {
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeUsableObjectAction(UsableObject object) {
		super(object);
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeUsableObjectAction(UsableObject object, boolean enabled) {
		super(object, enabled);
		init();
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	@Override
	public UsableObject getObject() {
		return (UsableObject) super.getObject();
	}

	/**
	 * @return the newUseForbiddenText
	 */
	public String getNewUseForbiddenText() {
		return newUseForbiddenText;
	}

	/**
	 * @param newUseForbiddenText
	 *            the newUseForbiddenText to set
	 */
	public void setNewUseForbiddenText(String newUseForbiddenText) {
		this.newUseForbiddenText = newUseForbiddenText;
	}

	/**
	 * @return the newUseSuccessfulText
	 */
	public String getNewUseSuccessfulText() {
		return newUseSuccessfulText;
	}

	/**
	 * @param newUseSuccessfulText
	 *            the newUseSuccessfulText to set
	 */
	public void setNewUseSuccessfulText(String newUseSuccessfulText) {
		this.newUseSuccessfulText = newUseSuccessfulText;
	}

	/**
	 * @return the enabling
	 */
	public Enabling getEnabling() {
		return enabling;
	}

	/**
	 * @param enabling
	 *            the enabling to set
	 */
	public void setEnabling(Enabling enabling) {
		this.enabling = enabling;
	}

	@Override
	protected void doAction() {
		// Call the super method
		super.doAction();

		// Change fields
		if (newUseForbiddenText != null) {
			getObject().setUseForbiddenText(newUseForbiddenText);
		}
		if (newUseSuccessfulText != null) {
			getObject().setUseSuccessfulText(newUseSuccessfulText);
		}
		if (enabling == Enabling.ENABLE) {
			getObject().setUsingEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			getObject().setUsingEnabled(false);
		}
	}

	@Override
	public String toString() {
		return "ChangeUsableObjectAction{" + "newUseForbiddenText="
				+ newUseForbiddenText + ", newUseSuccessfulText="
				+ newUseSuccessfulText + ", enabling=" + enabling + " "
				+ super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder(super.getActionDescription());
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" usage.");
		}
		if (newUseSuccessfulText != null) {
			builder.append(" Setting use successful text to '")
					.append(newUseSuccessfulText).append("'.");
		}
		if (newUseForbiddenText != null) {
			builder.append(" Setting use forbidden text to '")
					.append(newUseForbiddenText).append("'.");
		}
		return builder.toString();
	}
}
