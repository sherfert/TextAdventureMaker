/**
 * 
 */
package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import persistence.Main;

/**
 * Another action can be changed with this action. Currently this means
 * disabling or enabling it.
 * 
 * @author Satia Herfert
 */
@Entity
public class ChangeActionAction extends AbstractAction {

	/**
	 * The action to change
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private AbstractAction action;

	/**
	 * Currently, a ChangeActionAction is only good to enable or disable an
	 * action. Therefore, any of the two has to be done. If AbstractAction
	 * should get more fields, that should be changeable with this class, this
	 * has to be replaced with two booleans. If this is true, the action will be
	 * enabled, and disabled otherwise.
	 */
	private boolean enableOrDisableAction;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link ChangeActionAction#ChangeActionAction(AbstractAction)}
	 *             instead.
	 */
	@Deprecated
	public ChangeActionAction() {
	}

	/**
	 * An action changing another action.
	 * 
	 * @param action
	 *            the action to change
	 * @param enableOrDisableAction
	 *            if the other action should be enabled ({@code true}) or
	 *            disabled ({@code false})
	 */
	public ChangeActionAction(AbstractAction action,
			boolean enableOrDisableAction) {
		this.action = action;
		this.enableOrDisableAction = enableOrDisableAction;
	}

	/**
	 * 
	 * @param action
	 *            the action to change
	 * @param enableOrDisableAction
	 *            if the other action should be enabled ({@code true}) or
	 *            disabled ({@code false})
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeActionAction(AbstractAction action,
			boolean enableOrDisableAction, boolean enabled) {
		super(enabled);
		this.action = action;
		this.enableOrDisableAction = enableOrDisableAction;
	}

	@Override
	public void triggerAction() {
		if(enabled) {
			action.setEnabled(enableOrDisableAction);
		}
		Main.updateChanges();
	}
}
