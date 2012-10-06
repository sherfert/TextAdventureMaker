package data.interfaces;

import java.util.List;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.RemoveItemAction;

/**
 * Anything takeable in the game.
 * 
 * @author Satia
 */
public interface Takeable extends Identifiable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToTake(AbstractAction action);

	/**
	 * @return the addInventoryItemsAction
	 */
	public AddInventoryItemsAction getAddInventoryItemsAction();

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalActionsFromTake();

	/**
	 * @return the removeItemAction
	 */
	public RemoveItemAction getRemoveItemAction();

	/**
	 * @return the forbiddenText or {@code null}.
	 */
	public String getTakeForbiddenText();

	/**
	 * @return the successfulText or {@code null}.
	 */
	public String getTakeSuccessfulText();

	/**
	 * @return if the item will be removed when taken.
	 */
	public boolean isRemoveItem();

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
	 * @param removeItem
	 *            if the item should be removed when taken
	 */
	public void setRemoveItem(boolean removeItem);

	/**
	 * @param removeAction
	 *            the removeAction to set
	 */
	public void setRemoveItemAction(RemoveItemAction removeAction);

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
	 * Triggers the {@link AddInventoryItemsAction}, the
	 * {@link RemoveItemAction} and all additional actions.
	 */
	public void take();
}