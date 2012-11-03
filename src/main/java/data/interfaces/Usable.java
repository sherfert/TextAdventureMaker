package data.interfaces;

import java.util.List;

import data.action.AbstractAction;

/**
 * Anything useable for itself (without other objects) in the game.
 * 
 * @author Satia
 */
public interface Usable extends UsableOrPassivelyUsable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToUse(AbstractAction action);
	
	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalActionsFromUse();

	/**
	 * @return the forbiddenText or {@code null}.
	 */
	public String getUseForbiddenText();

	/**
	 * @return the successfulText or {@code null}.
	 */
	public String getUseSuccessfulText();

	/**
	 * @return if using is enabled.
	 */
	public boolean isUsingEnabled();

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromUse(AbstractAction action);

	/**
	 * Sets the forbidden text. If {@code null} passed, the default text will be
	 * used when the action is triggered.
	 * 
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setUseForbiddenText(String forbiddenText);

	/**
	 * Sets the successful text. If {@code null} passed, the default text will
	 * be used when the action is triggered.
	 * 
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setUseSuccessfulText(String successfulText);

	/**
	 * @param enabled
	 *            if using should be enabled
	 */
	public void setUsingEnabled(boolean enabled);

	/**
	 * Triggers all additional actions.
	 */
	public void use();
}