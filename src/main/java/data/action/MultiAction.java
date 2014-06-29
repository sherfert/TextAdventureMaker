package data.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import data.NamedObject;

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
public class MultiAction extends AbstractAction {

	/**
	 * All actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> actions;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #MultiAction(AbstractAction...)} instead.
	 */
	@Deprecated
	public MultiAction() {
		this.actions = new ArrayList<>();
	}

	/**
	 * Creates an enabled MultiAction. Also enables all embedded actions.
	 * 
	 * @param actions
	 *            the actions
	 */
	public MultiAction(AbstractAction... actions) {
		// Copy the array to a new list, so it can be modified afterwards
		this.actions = new ArrayList<>(Arrays.asList(actions));
		setEnabled(true);
	}

	/**
	 * Enables/disables all embedded actions.
	 * 
	 * @param enabled
	 *            if the action should be enabled
	 * @param actions
	 *            the actions
	 */
	public MultiAction(boolean enabled, AbstractAction... actions) {
		super(enabled);
		// Copy the array to a new list, so it can be modified afterwards
		this.actions = new ArrayList<>(Arrays.asList(actions));
		setEnabled(enabled);
	}

	/**
	 * @return the actions
	 */
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
	protected void doAction() {
		for (AbstractAction action : actions) {
			action.triggerAction();
		}
	}

	@Override
	public String toString() {
		return "MultiAction{actionsIDs=" + NamedObject.getIDList(actions) + " "
				+ super.toString() + "}";
	}

	/**
	 * {@inheritDoc} This method includes the action description of all embedded
	 * actions. Therefore it has to be ensured, that no other action does the
	 * same.
	 */
	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Combining actions: ");
		for (AbstractAction action : actions) {
			builder.append(action.getActionDescription());
		}
		return builder.toString();
	}

}
