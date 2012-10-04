package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import data.action.AbstractAction;

/**
 * Anything that has a primary action and, if necessary, additional actions
 * being performed at the same time.
 * 
 * FIXME this class is "not so good".
 * 
 * @author Satia
 * 
 * @param <E>
 *            the type of the primary action. A subtype of
 *            {@link AbstractAction}.
 */
@MappedSuperclass
public abstract class HasActions<E extends AbstractAction> extends NamedObject {

	/**
	 * The primary action.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	protected E primaryAction;

	/**
	 * All additional actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	List<AbstractAction> additionalActions;

	/**
	 * No-arg constructor for the database. Use
	 * {@link HasActions#HasActions(String, String)} instead.
	 */
	@Deprecated
	protected HasActions() {
		additionalActions = new ArrayList<AbstractAction>();
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	protected HasActions(String name, String description) {
		super(name, description);
		additionalActions = new ArrayList<AbstractAction>();
	}

	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAction(AbstractAction action) {
		this.additionalActions.add(action);
	}

	/**
	 * @return the forbiddenText
	 */
	public String getForbiddenText() {
		return primaryAction.getForbiddenText();
	}

	/**
	 * @return the primaryAction
	 */
	public E getPrimaryAction() {
		return primaryAction;
	}

	/**
	 * @return the successfulText
	 */
	public String getSuccessfulText() {
		return primaryAction.getSuccessfulText();
	}

	/**
	 * @return if the primary action is enabled.
	 */
	public boolean isPrimaryActionEnabled() {
		return primaryAction.isEnabled();
	}

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAction(AbstractAction action) {
		this.additionalActions.remove(action);
	}

	/**
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setSuccessfulText(String successfulText) {
		primaryAction.setSuccessfulText(successfulText);
	}

	/**
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setForbiddenText(String forbiddenText) {
		primaryAction.setForbiddenText(forbiddenText);
	}

	/**
	 * @param enabled
	 *            if the primary action should be enabled
	 */
	public void setPrimaryActionEnabled(boolean enabled) {
		primaryAction.setEnabled(enabled);
	}

	/**
	 * Triggers all actions.
	 */
	public void triggerActions() {
		primaryAction.triggerAction();
		for (AbstractAction abstractAction : additionalActions) {
			abstractAction.triggerAction();
		}
	}
}