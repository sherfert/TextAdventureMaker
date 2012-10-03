/**
 * 
 */
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
 * 
 * @author Satia
 * 
 * @param <E>
 */
@MappedSuperclass
public abstract class HasActions<E extends AbstractAction> extends NamedObject {

	protected HasActions() {
		additionalActions = new ArrayList<AbstractAction>();
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	protected E primaryAction;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	List<AbstractAction> additionalActions;

	/**
	 * @return the primaryAction
	 */
	public E getPrimaryAction() {
		return primaryAction;
	}

	public boolean isPrimaryActionEnabled() {
		return primaryAction.isEnabled();
	}

	public void setPrimaryActionEnabled(boolean enabled) {
		primaryAction.setEnabled(enabled);
	}

	/**
	 * @return the forbiddenText
	 */
	public String getForbiddenText() {
		return primaryAction.getForbiddenText();
	}

	/**
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setForbiddenText(String forbiddenText) {
		primaryAction.setForbiddenText(forbiddenText);
	}

	public void addAction(AbstractAction action) {
		this.additionalActions.add(action);
	}

	public void removeAction(AbstractAction action) {
		this.additionalActions.remove(action);
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