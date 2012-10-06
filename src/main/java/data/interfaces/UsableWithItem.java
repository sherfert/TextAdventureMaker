package data.interfaces;

import java.util.List;

import data.action.AbstractAction;

import data.Item;

/**
 * Anything useable with an Item in the game.
 * 
 * @author Satia
 */
public interface UsableWithItem extends Identifiable {
	/**
	 * Adds an additional action for that item
	 * 
	 * @param item
	 *            the item
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToUseWith(Item item, AbstractAction action);

	/**
	 * @param item
	 *            the item
	 * 
	 * @return the additional actions for that item.
	 */
	public List<AbstractAction> getAdditionalActionsFromUseWith(Item item);

	/**
	 * @param item
	 *            the item
	 * 
	 * @return the forbiddenText for that item or {@code null}.
	 */
	public String getUseWithForbiddenText(Item item);

	/**
	 * @param item
	 *            the item
	 * 
	 * @return the successfulText for that item or {@code null}.
	 */
	public String getUseWithSuccessfulText(Item item);

	/**
	 * @param item
	 *            the item
	 * 
	 * @return if using is enabled with the given item.
	 */
	public boolean isUsingEnabledWith(Item item);

	/**
	 * Removes an additional action for that item.
	 * 
	 * @param item
	 *            the item
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromUseWith(Item item,
			AbstractAction action);

	/**
	 * Sets the forbidden text for that item. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param item
	 *            the item
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setUseWithForbiddenText(Item item, String forbiddenText);

	/**
	 * Sets the successful text for that item. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param item
	 *            the item
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setUseWithSuccessfulText(Item item, String successfulText);

	/**
	 * @param item
	 *            the item
	 * @param enabled
	 *            if using should be enabled with that item
	 */
	public void setUsingEnabledWith(Item item, boolean enabled);

	/**
	 * Triggers all additional actions for that item, if any.
	 * 
	 * @param item
	 *            the item
	 */
	public void useWith(Item item);
}