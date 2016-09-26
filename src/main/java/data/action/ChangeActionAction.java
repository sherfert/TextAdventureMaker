/**
 *
 */
package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
 * Another action can be changed with this action. This means disabling or
 * enabling it.
 * 
 * @author Satia Herfert
 */
@Entity
@Access(AccessType.PROPERTY)
public class ChangeActionAction extends AbstractAction {

	/**
	 * The action to change
	 */
	private AbstractAction action;

	/**
	 * Enabling or disabling the action
	 */
	private Enabling enabling;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link ChangeActionAction#ChangeActionAction(String, AbstractAction, Enabling))}
	 *             instead.
	 */
	@Deprecated
	public ChangeActionAction() {
	}
	
	/**
	 * An action changing another action.
	 * 
	 * @param name
	 *            the name
	 * @param action
	 *            the action to change
	 */
	public ChangeActionAction(String name, AbstractAction action) {
		super(name);
		this.action = action;
		this.enabling = Enabling.DO_NOT_CHANGE;
	}

	/**
	 * An action changing another action.
	 * 
	 * @param name
	 *            the name
	 * @param action
	 *            the action to change
	 * @param enabling
	 *            if the other action should be enabled, disabled, or left
	 *            unchanged
	 */
	public ChangeActionAction(String name, AbstractAction action, Enabling enabling) {
		super(name);
		this.action = action;
		this.enabling = enabling;
	}

	/**
	 * @return the action
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGEACTIONACTION_ACTION", //
	foreignKeyDefinition = "FOREIGN KEY (ACTION_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	public AbstractAction getAction() {
		return action;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setAction(AbstractAction action) {
		this.action = action;
	}

	/**
	 * @return the enabling
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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
	public String actionDescription() {
		return enabling.description + " action " + action.getId();
	}
}
