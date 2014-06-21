package data.interfaces;

import java.util.List;

import data.action.AbstractAction;

import data.InventoryItem;

/**
 * Anything combineable with other InventoryItems. The InventoryItems may
 * disappear and new {@link InventoryItem}s can be added.
 * 
 * @author Satia
 */
public interface Combinable extends UsableOrPassivelyUsable {
	/**
	 * Adds an additional action for that combineable.
	 * 
	 * @param partner
	 *            the partner
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToCombineWith(Combinable partner,
			AbstractAction action);

	/**
	 * Adds a new {@link InventoryItem} to be added when combined with the given
	 * partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param newItem
	 *            the new inventory item to be added
	 */
	public void addNewInventoryItemWhenCombinedWith(Combinable partner,
			InventoryItem newItem);

	/**
	 * Adds new inventory items, removes both partners if removeCombinables is
	 * enabled andtriggers all additional actions for that item, if any.
	 * 
	 * @param partner
	 *            the partner
	 */
	public void combineWith(Combinable partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the additional actions for that item.
	 */
	public List<AbstractAction> getAdditionalActionsFromCombineWith(
			Combinable partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the forbiddenText for that item or {@code null}.
	 */
	public String getCombineWithForbiddenText(Combinable partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the successfulText for that item or {@code null}.
	 */
	public String getCombineWithSuccessfulText(Combinable partner);

	/**
	 * The list of {@link InventoryItem}s that get added, when this Combinable
	 * is combined with the given partner.
	 * 
	 * @param partner
	 *            the parter
	 * @return the added {@link InventoryItem}s.
	 */
	public List<InventoryItem> getNewInventoryItemsWhenCombinedWith(
			Combinable partner);

	/**
	 * Gets if the two Combinables should be removed when combined with the
	 * given partner.
	 * 
	 * @param partner
	 *            the partner
	 * @return whether it should be removed.
	 */
	public boolean getRemoveCombinablesWhenCombinedWith(Combinable partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return if using is enabled with the given item.
	 */
	public boolean isCombiningEnabledWith(Combinable partner);

	/**
	 * Removes an additional action for that item.
	 * 
	 * @param partner
	 *            the partner
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromCombineWith(Combinable partner,
			AbstractAction action);

	/**
	 * Removes a new {@link InventoryItem} to be added when combined with the
	 * given partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param newItem
	 *            the new inventory item to be removed
	 */
	public void removeNewInventoryItemWhenCombinedWith(Combinable partner,
			InventoryItem newItem);

	/**
	 * Sets the forbidden text for that item. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param partner
	 *            the partner
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setCombineWithForbiddenText(Combinable partner,
			String forbiddenText);

	/**
	 * Sets the successful text for that item. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param partner
	 *            the partner
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setCombineWithSuccessfulText(Combinable partner,
			String successfulText);

	/**
	 * @param partner
	 *            the partner
	 * @param enabled
	 *            if using should be enabled with that item
	 */
	public void setCombiningEnabledWith(Combinable partner, boolean enabled);

	/**
	 * Sets if the two Combinables should be removed when combined with the
	 * given partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param remove
	 *            whether it should be removed
	 */
	public void setRemoveCombinablesWhenCombinedWith(Combinable partner,
			boolean remove);
}