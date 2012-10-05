package data.interfaces;

import java.util.List;

import data.action.AbstractAction;
import data.action.TakeAction;

/**
 * Anything takeable in the game.
 * 
 * @author Satia
 */
public interface Takeable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToTake(AbstractAction action);
	
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
	public List<AbstractAction> getAdditionalActionsFromTake();
	
	/**
	 * @return the identifiers.
	 */
	public List<String> getIdentifiers();
	
	/**
	 * @return the takeAction
	 */
	public TakeAction getTakeAction();

	/**
	 * @return the forbiddenText or {@code null}.
	 */
	public String getTakeForbiddenText();

	/**
	 * @return the successfulText or {@code null}.
	 */
	public String getTakeSuccessfulText();

	/**
	 * @return if taking is enabled.
	 */
	public boolean isTakingEnabled();

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromTake(AbstractAction action);

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
	 * Sets the forbidden text. If {@code null} passed, the default text will be
	 * used when the action is triggered.
	 * 
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setTakeForbiddenText(String forbiddenText);

	/**
	 * Sets the successful text. If {@code null} passed, the default text will
	 * be used when the action is triggered.
	 * 
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setTakeSuccessfulText(String successfulText);

	/**
	 * @param enabled
	 *            if taking should be enabled
	 */
	public void setTakingEnabled(boolean enabled);

	/**
	 * Triggers the {@link TakeAction} and all additional actions.
	 */
	public void take();
}