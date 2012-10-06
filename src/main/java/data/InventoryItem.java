package data;

import javax.persistence.Basic;
import javax.persistence.Entity;

import data.action.AbstractAction;
import data.action.RemoveInventoryItemAction;
import data.action.RemoveItemAction;

/**
 * Any item that can appear in your inventory. These items are not in locations.
 * 
 * @author Satia
 * 
 * TODO implemets UsableWithItem
 */
@SuppressWarnings("unused")
@Entity
public class InventoryItem extends UsableObject {

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link InventoryItem#InventoryItem(String, String)}
	 *             instead.
	 */
	@Deprecated
	public InventoryItem() {
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public InventoryItem(String name, String description) {
		super(name, description);
	}

	/**
	 * Copies everything from the given item.
	 * 
	 * @param item
	 *            the item to use fields from
	 */
	public InventoryItem(Item item) {
		// Name and description
		super(item.getName(), item.getDescription());
		// Inspection text
		setInspectionText(item.getInspectionText());
		// Identifiers
		for (String identifier : item.getIdentifiers()) {
			addIdentifier(identifier);
		}
		// Additional inspect actions
		for (AbstractAction action : item.getAdditionalActionsFromInspect()) {
			addAdditionalActionToInspect(convertInventoryItemActionToItemAction(
					item, action));
		}
		// Additional use actions
		for (AbstractAction action : item.getAdditionalActionsFromUse()) {
			addAdditionalActionToUse(convertInventoryItemActionToItemAction(
					item, action));
		}
		// Use forbidden/successful text
		setUseForbiddenText(item.getUseForbiddenText());
		setUseSuccessfulText(item.getUseSuccessfulText());
		// Using enabled status
		setUsingEnabled(item.isUsingEnabled());
	}

	/**
	 * Converts any action connected with the given {@link Item} into an
	 * equivalent action connected to this InventoryItem.
	 * 
	 * @param item
	 *            the {@link Item}
	 * @param action
	 *            the action to be converted
	 * @return a converted action or the action itself.
	 */
	private AbstractAction convertInventoryItemActionToItemAction(Item item,
			AbstractAction action) {
		if (action instanceof RemoveItemAction
				&& ((RemoveItemAction) action).getItem() == item) {
			RemoveInventoryItemAction result = new RemoveInventoryItemAction(
					this);
			result.setEnabled(action.isEnabled());
			result.setForbiddenText(action.getForbiddenText());
			result.setSuccessfulText(action.getSuccessfulText());
			return result;
		}
		return action;
	}
}