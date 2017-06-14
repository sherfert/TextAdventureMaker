package data.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

import data.Game;

/**
 * A MultiAction simply combines multiple actions into one. One should only
 * combine actions that are always meant to be triggered together. Enabling or
 * disabling this action will enable or disable all embedded actions. If this is
 * not the desired behavior, one should not use a MultiAction.
 * 
 * Triggering this action will trigger all embedded actions.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class MultiAction extends AbstractAction {

	/**
	 * All actions.
	 */
	private List<AbstractAction> actions;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #MultiAction(String, AbstractAction...)} instead.
	 */
	@Deprecated
	public MultiAction() {
		this.actions = new ArrayList<>();
	}

	/**
	 * Creates an enabled MultiAction. Also enables all embedded actions.
	 * @param name
	 *            the name
	 * @param actions
	 *            the actions
	 */
	public MultiAction(String name, AbstractAction... actions) {
		super(name);
		// Copy the array to a new list, so it can be modified afterwards
		this.actions = new ArrayList<>(Arrays.asList(actions));
		setEnabled(true);
	}

	/**
	 * @return the actions
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "MULTIACTION_ACTIONS", foreignKey = @ForeignKey(name = "FK_MultiAction_actions_S", //
	foreignKeyDefinition = "FOREIGN KEY (MultiAction_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_MultiAction_actions_D", //
	foreignKeyDefinition = "FOREIGN KEY (actions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	@OrderColumn
	public List<AbstractAction> getActions() {
		return actions;
	}

	/**
	 * @param actions
	 *            the actions to set
	 */
	public void setActions(List<AbstractAction> actions) {
		this.actions = actions;
	}

	/**
	 * Adds an action.
	 * 
	 * @param action
	 *            the action to add.
	 */
	public void addAction(AbstractAction action) {
		actions.add(action);
	}

	/**
	 * Removes an action.
	 * 
	 * @param action
	 *            the action to remove.
	 */
	public void removeAction(AbstractAction action) {
		actions.remove(action);
	}

	/**
	 * {@inheritDoc} Also enables or disables all embedded actions.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (AbstractAction action : actions) {
			action.setEnabled(enabled);
		}
	}

	@Override
	protected void doAction(Game game) {
		for (AbstractAction action : actions) {
			// doAction instead of triggerAction:
			// like this enabled/disabled does not matter and
			// if the action IDs of triggered actions are saved
			// there are no multiple entries
			action.doAction(game);
		}
	}

	/**
	 * {@inheritDoc} This method includes the action description of all embedded
	 * actions. Therefore it has to be ensured, that no other action does the
	 * same.
	 */
	@Override
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Combining actions:");
		int i = 1;
		for (AbstractAction action : actions) {
			builder.append(' ').append(i).append(") ").append(action.actionDescription());
			i++;
		}
		return builder.toString();
	}

}
