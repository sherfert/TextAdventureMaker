package data.interfaces;

import java.util.List;

import data.Game;
import data.action.AbstractAction;

/**
 * TODO check which of the Interface methods (for all interfaces) are actually used!
 * 
 * Anything combineable with another class. Usually the same class. Namely
 * {@link InventoryItem}.
 * 
 * The InventoryItems may disappear and new ones can be added.
 * 
 * @author Satia
 * 
 * @param <E>
 *            the class to be combinable with. Usually the implementing class
 *            itself.
 */
public interface Combinable<E> extends UsableOrPassivelyUsable {

	/**
	 * Adds an additional action for that combineable.
	 * 
	 * @param partner
	 *            the partner
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToCombineWith(Combinable<E> partner, AbstractAction action);

	/**
	 * Adds a new {@link Combinable} to be added when combined with the given
	 * partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param newItem
	 *            the new combinable to be added
	 */
	public void addNewCombinableWhenCombinedWith(Combinable<E> partner, E newItem);

	/**
	 * Adds an additional command that can be used to combine with the partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param command
	 *            the command
	 */
	public void addAdditionalCombineCommand(Combinable<E> partner, String command);

	/**
	 * Adds new combinables, removes both partners if removeCombinables is
	 * enabled and triggers all additional actions for that item, if any.
	 * 
	 * @param partner
	 *            the partner
	 * @param game
	 *            the game
	 */
	public void combineWith(Combinable<E> partner, Game game);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the additional actions for that item.
	 */
	public List<AbstractAction> getAdditionalActionsFromCombineWith(Combinable<E> partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the additional combine commands.
	 */
	public List<String> getAdditionalCombineCommands(Combinable<E> partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the forbiddenText for that item or {@code null}.
	 */
	public String getCombineWithForbiddenText(Combinable<E> partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return the successfulText for that item or {@code null}.
	 */
	public String getCombineWithSuccessfulText(Combinable<E> partner);

	/**
	 * The list of combinables that get added, when this Combinable is combined
	 * with the given partner.
	 * 
	 * @param partner
	 *            the parter
	 * @return the added combinables.
	 */
	public List<E> getNewCombinablesWhenCombinedWith(Combinable<E> partner);

	/**
	 * Gets if the two Combinables should be removed when combined with the
	 * given partner.
	 * 
	 * @param partner
	 *            the partner
	 * @return whether it should be removed.
	 */
	public boolean getRemoveCombinablesWhenCombinedWith(Combinable<E> partner);

	/**
	 * @param partner
	 *            the partner
	 * 
	 * @return if using is enabled with the given item.
	 */
	public boolean isCombiningEnabledWith(Combinable<E> partner);

	/**
	 * Removes an additional action for that item.
	 * 
	 * @param partner
	 *            the partner
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromCombineWith(Combinable<E> partner, AbstractAction action);

	/**
	 * Removes an additional combine command
	 * 
	 * @param partner
	 *            the partner
	 * @param command
	 *            the command
	 */
	public void removeAdditionalCombineCommand(Combinable<E> partner, String command);

	/**
	 * Removes a new {@link Combinable} to be added when combined with the given
	 * partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param newItem
	 *            the new combinable to be removed
	 */
	public void removeNewCombinableWhenCombinedWith(Combinable<E> partner, E newItem);

	/**
	 * Sets the forbidden text for that item. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param partner
	 *            the partner
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setCombineWithForbiddenText(Combinable<E> partner, String forbiddenText);

	/**
	 * Sets the successful text for that item. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param partner
	 *            the partner
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setCombineWithSuccessfulText(Combinable<E> partner, String successfulText);

	/**
	 * @param partner
	 *            the partner
	 * @param enabled
	 *            if using should be enabled with that item
	 */
	public void setCombiningEnabledWith(Combinable<E> partner, boolean enabled);

	/**
	 * Sets if the two Combinables should be removed when combined with the
	 * given partner.
	 * 
	 * @param partner
	 *            the partner
	 * @param remove
	 *            whether it should be removed
	 */
	public void setRemoveCombinablesWhenCombinedWith(Combinable<E> partner, boolean remove);
}