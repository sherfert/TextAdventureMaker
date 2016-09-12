package data.interfaces;

import java.util.List;

import data.Game;
import data.InventoryItem;
import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.ChangeItemAction;

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
	public void addAdditionalTakeAction(AbstractAction action);

	/**
	 * Adds an additional command that can be used to take the object.
	 * 
	 * @param command
	 *            the command
	 */
	public void addAdditionalTakeCommand(String command);

	/**
	 * @return the additional take commands.
	 */
	public List<String> getAdditionalTakeCommands();

	/**
	 * Adds an {@link InventoryItem} to the items to be added to the inventory.
	 *
	 * @param item
	 *            the item
	 */
	public void addPickUpItem(InventoryItem item);

	/**
	 * Removes an {@link InventoryItem} from the items to be added to the
	 * inventory.
	 *
	 * @param item
	 *            the item
	 */
	public void removePickUpItem(InventoryItem item);

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalTakeActions();

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
	public void removeAdditionalTakeAction(AbstractAction action);

	/**
	 * Removes an additional take command.
	 * 
	 * @param command
	 *            the command
	 */
	public void removeAdditionalTakeCommand(String command);

	/**
	 * @param removeItem
	 *            if the item should be removed when taken
	 */
	public void setRemoveItem(boolean removeItem);

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
	 * Triggers all additional actions and, if enabled, the
	 * {@link AddInventoryItemsAction} and, if also removeItem, the
	 * {@link ChangeItemAction}.
	 * 
	 * @param game
	 *            the game
	 */
	public void take(Game game);

	/**
	 * Sets additional take commands.
	 * 
	 * @param additionalTakeCommands
	 *            the additionalTakeCommands to set
	 */
	public void setAdditionalTakeCommands(List<String> additionalTakeCommands);

	/**
	 * Sets additional take actions.
	 * 
	 * @param additionalTakeActions
	 *            the additionalTakeActions to set
	 */
	public void setAdditionalTakeActions(List<AbstractAction> additionalTakeActions);

	/**
	 * Gets the {@link InventoryItem}s to to be added to the inventory.
	 * 
	 * @return the pickUpItems
	 */
	public List<InventoryItem> getPickUpItems();
}