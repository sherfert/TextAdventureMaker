package data.interfaces;

import java.util.List;

import data.action.AbstractAction;

/**
 * Anything inspectable in the game.
 * 
 * @author Satia
 */
public interface Inspectable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToInspect(AbstractAction action);

	/**
	 * Adds an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void addIdentifier(String name);

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalActionsFromInspect();

	/**
	 * @return the identifiers.
	 */
	public List<String> getIdentifiers();

	/**
	 * @return the text being displayed when inspected.
	 */
	public String getLongDescription();

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
	 * Removes an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeIdentifier(String name);

	/**
	 * @param identifiers
	 *            the identifiers to set
	 */
	public void setIdentifiers(List<String> identifiers);

	/**
	 * @param longDescription
	 *            the longDescription to set
	 */
	public void setLongDescription(String longDescription);
}