package data.action;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import persistence.PersistenceManager;
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
	private String newUseForbiddenText;

	/**
	 * The new useSuccessfulText. If {@code null}, the old will not be changed.
	 */
	private String newUseSuccessfulText;

	/**
	 * Enabling or disabling if the UsableObject is actually usable.
	 */
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
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeUsableObjectAction(UsableObject object) {
		super(object);
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeUsableObjectAction(UsableObject object, boolean enabled) {
		super(object, enabled);
	}
	
	/**
	 * @return the object
	 */
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
	 * @param newUseForbiddenText the newUseForbiddenText to set
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
	 * @param newUseSuccessfulText the newUseSuccessfulText to set
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
	 * @param enabling the enabling to set
	 */
	public void setEnabling(Enabling enabling) {
		this.enabling = enabling;
	}
	
	@Override
	public void triggerAction() {
		// Call the super method
		super.triggerAction();
		
		if (enabled) {
			// Change fields
			if (newUseForbiddenText != null) {
				getObject().setUseForbiddenText(newUseForbiddenText);
			}
			if (newUseSuccessfulText != null) {
				getObject().setUseSuccessfulText(newUseSuccessfulText);
			}
			if (enabling == Enabling.ENABLE) {
				getObject().setUsingEnabled(true);
			} else if(enabling == Enabling.DISABLE) {
				getObject().setUsingEnabled(false);
			}
		}
		PersistenceManager.updateChanges();
	}

}
