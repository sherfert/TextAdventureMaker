package data.interfaces;

import java.util.List;

import data.action.AbstractAction;

/**
 * Anything inspectable in the game.
 * 
 * @author Satia
 */
public interface Inspectable extends Identifiable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToInspect(AbstractAction action);

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalActionsFromInspect();

	/**
	 * @return the text being displayed when inspected or {@code null} if the
	 *         default text is to be used.
	 */
	public String getInspectionText();

	/**
	 * Triggers all additional actions.
	 */
	public void inspect();

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromInspect(AbstractAction action);

	/**
	 * Sets the inspection text. If {@code null} passed, the default text will
	 * be used.
	 * 
	 * @param inspectionText
	 *            the inspectionText to set
	 */
	public void setInspectionText(String inspectionText);
}