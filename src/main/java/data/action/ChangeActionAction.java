/**
 *
 */
package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Game;

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
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGEACTIONACTION_ACTION", foreignKeyDefinition = "FOREIGN KEY (ACTION_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	private AbstractAction action;

	/**
	 * Enabling or disabling the action
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enabling;

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
	 * @param enabling
	 *            if the other action should be enabled, disabled, or left
	 *            unchanged
	 */
	public ChangeActionAction(AbstractAction action, Enabling enabling) {
		this.action = action;
		this.enabling = enabling;
	}

	/**
	 * 
	 * @param action
	 *            the action to change
	 * @param enabling
	 *            if the other action should be enabled, disabled, or left
	 *            unchanged
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeActionAction(AbstractAction action, Enabling enabling, boolean enabled) {
		super(enabled);
		this.action = action;
		this.enabling = enabling;
	}

	/**
	 * @return the action
	 */
	public AbstractAction getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(AbstractAction action) {
		this.action = action;
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
	protected void doAction(Game game) {
		if (enabling == Enabling.ENABLE) {
			action.setEnabled(true);
		} else if (enabling == Enabling.DISABLE) {
			action.setEnabled(false);
		}
	}

	@Override
	public String toString() {
		return "ChangeActionAction{" + "actionID=" + action.getId() + ", enabling=" + enabling + " " + super.toString()
				+ '}';
	}

	@Override
	public String getActionDescription() {
		return enabling.description + " action " + action.getId();
	}
}
