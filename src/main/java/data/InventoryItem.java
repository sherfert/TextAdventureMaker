package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;

import data.action.AbstractAction;
import data.action.RemoveInventoryItemAction;
import data.action.RemoveItemAction;
import data.interfaces.UsableWithItem;

/**
 * Any item that can appear in your inventory. These items are not in locations.
 * 
 * @author Satia
 */
@Entity
public class InventoryItem extends UsableObject implements UsableWithItem {
	/**
	 * Attributes of an {@link Item} that can be used with an
	 * {@link InventoryItem}.
	 * 
	 * JPA requires this inner class to be static. A back-reference to the
	 * owning {@link InventoryItem} could optionally be added.
	 * 
	 * @author Satia
	 */
	@Entity
	public static class UsableItem {
		/**
		 * The id.
		 */
		@Id
		@GeneratedValue
		private int id;

		/**
		 * All actions triggered when the {@link InventoryItem} is used with the
		 * mapped {@link Item}. They are triggered regardless of the enabled
		 * status.
		 */
		@ManyToMany(cascade = CascadeType.PERSIST)
		@JoinTable
		private List<AbstractAction> additionalUseWithActions;

		/**
		 * Whether using of this {@link InventoryItem} with the mapped
		 * {@link Item} should be enabled.
		 */
		private boolean enabled;

		/**
		 * The text displayed when using is disabled but the user tries to
		 * trigger the connection.
		 */
		private String useWithForbiddenText;

		/**
		 * The text displayed when using is enabled and the user tries to
		 * trigger the connection.
		 */
		private String useWithSuccessfulText;

		/**
		 * Disabled by default and no forbidden/successful texts.
		 */
		public UsableItem() {
			additionalUseWithActions = new ArrayList<AbstractAction>();
			enabled = false;
		}
	}

	/**
	 * An inventory item can be used with items. For each item there are
	 * additional informations about the usability, etc. The method
	 * {@link InventoryItem#getUsableItem(Item)} adds key and value, if it was
	 * not stored before.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	@MapKeyJoinColumn
	private Map<Item, UsableItem> usableItems;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link InventoryItem#InventoryItem(String, String)}
	 *             instead.
	 */
	@Deprecated
	public InventoryItem() {
		init();
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

		init();
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public InventoryItem(String name, String description) {
		super(name, description);
		init();
	}

	@Override
	public void addAdditionalActionToUseWith(Item item, AbstractAction action) {
		getUsableItem(item).additionalUseWithActions.add(action);
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromUseWith(Item item) {
		return getUsableItem(item).additionalUseWithActions;
	}

	@Override
	public String getUseWithForbiddenText(Item item) {
		return getUsableItem(item).useWithForbiddenText;
	}

	@Override
	public String getUseWithSuccessfulText(Item item) {
		return getUsableItem(item).useWithSuccessfulText;
	}

	@Override
	public boolean isUsingEnabledWith(Item item) {
		return getUsableItem(item).enabled;
	}

	@Override
	public void removeAdditionalActionFromUseWith(Item item,
			AbstractAction action) {
		getUsableItem(item).additionalUseWithActions.remove(action);
	}

	@Override
	public void setUseWithForbiddenText(Item item, String forbiddenText) {
		getUsableItem(item).useWithForbiddenText = forbiddenText;
	}

	@Override
	public void setUseWithSuccessfulText(Item item, String successfulText) {
		getUsableItem(item).useWithSuccessfulText = successfulText;
	}

	@Override
	public void setUsingEnabledWith(Item item, boolean enabled) {
		getUsableItem(item).enabled = enabled;
	}

	@Override
	public void useWith(Item item) {
		// Trigger all additional actions
		for (AbstractAction abstractAction : getUsableItem(item).additionalUseWithActions) {
			abstractAction.triggerAction();
		}
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

	/**
	 * Gets the {@link UsableItem} associated with the given {@link Item}. If no
	 * mapping exists, one will be created.
	 * 
	 * @param item
	 *            the item
	 * @return the associated {@link UsableItem}.
	 */
	private UsableItem getUsableItem(Item item) {
		UsableItem result = usableItems.get(item);
		if (result == null) {
			usableItems.put(item, result = new UsableItem());
		}
		return result;
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		usableItems = new HashMap<Item, UsableItem>();
	}
}