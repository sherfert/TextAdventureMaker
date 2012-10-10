package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.RemoveInventoryItemAction;
import data.action.SetItemLocationAction;
import data.interfaces.Combinable;
import data.interfaces.UsableWithItem;

/**
 * Any item that can appear in your inventory. These items are not in locations.
 * 
 * @author Satia
 */
@Entity
public class InventoryItem extends UsableObject implements UsableWithItem,
		Combinable {
	/**
	 * Attributes of an {@link InventoryItem} that can be used with an
	 * {@link InventoryItem}.
	 * 
	 * @author Satia
	 */
	@Entity
	private static class CombinableInventoryItem {
		/**
		 * The action adding the new inventory items.
		 */
		@ManyToOne(cascade = CascadeType.PERSIST)
		@JoinColumn(nullable = false)
		private AddInventoryItemsAction addInventoryItemsAction;

		/**
		 * All actions triggered when the two {@link InventoryItem}s are
		 * combined. They are triggered regardless of the enabled status.
		 */
		@ManyToMany(cascade = CascadeType.PERSIST)
		@JoinTable
		private List<AbstractAction> additionalCombineWithActions;

		/**
		 * The text displayed when combining is disabled but the user tries to
		 * trigger the connection.
		 */
		private String combineWithForbiddenText;

		/**
		 * The text displayed when combining is enabled and the user tries to
		 * trigger the connection.
		 */
		private String combineWithSuccessfulText;

		/**
		 * Whether combining of the two {@link InventoryItem}s should be
		 * enabled.
		 */
		@Column(nullable = false)
		private boolean enabled;

		/**
		 * The id.
		 */
		@Id
		@GeneratedValue
		private int id;

		/**
		 * Whether the two partners should be removed when combined
		 * successfully.
		 */
		@Column(nullable = false)
		private boolean removeCombinables;

		/**
		 * Disabled by default and no forbidden/successful texts. Removing items
		 * also disabled by default.
		 */
		public CombinableInventoryItem() {
			additionalCombineWithActions = new ArrayList<AbstractAction>();
			addInventoryItemsAction = new AddInventoryItemsAction();
			enabled = false;
			removeCombinables = false;
		}
	}

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
	private static class UsableItem {
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
		@Column(nullable = false)
		private boolean enabled;

		/**
		 * The id.
		 */
		@Id
		@GeneratedValue
		private int id;

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
	 * An inventory item can be combined with others. For each inventory item
	 * there are additional informations about the usability, etc. The method
	 * {@link InventoryItem#getCombinableInventoryItem(InventoryItem)} adds key
	 * and value, if it was not stored before. The other inventory item's map
	 * will be synchronized, too.
	 */
	// TODO why Maps not unnullable?
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	@MapKeyJoinColumn
	private Map<InventoryItem, CombinableInventoryItem> combinableInventoryItems;

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
	public void addAdditionalActionToCombineWith(InventoryItem partner,
			AbstractAction action) {
		getCombinableInventoryItem(partner).additionalCombineWithActions
				.add(action);
	}

	@Override
	public void addAdditionalActionToUseWith(Item item, AbstractAction action) {
		getUsableItem(item).additionalUseWithActions.add(action);
	}

	@Override
	public void addNewInventoryItemWhenCombinedWith(InventoryItem partner,
			InventoryItem newItem) {
		getCombinableInventoryItem(partner).addInventoryItemsAction
				.addPickUpItem(newItem);
	}

	@Override
	public void combineWith(InventoryItem partner) {
		CombinableInventoryItem combination = getCombinableInventoryItem(partner);
		if (combination.enabled) {
			// Add new inventory items
			combination.addInventoryItemsAction.triggerAction();
			if (combination.removeCombinables) {
				/*
				 * Remove both partners with temporary
				 * RemoveInventoryItemActions.
				 */
				new RemoveInventoryItemAction(this).triggerAction();
				new RemoveInventoryItemAction(partner).triggerAction();
			}
		}
		// Trigger all additional actions
		for (AbstractAction abstractAction : combination.additionalCombineWithActions) {
			abstractAction.triggerAction();
		}
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromCombineWith(
			InventoryItem partner) {
		return getCombinableInventoryItem(partner).additionalCombineWithActions;
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromUseWith(Item item) {
		return getUsableItem(item).additionalUseWithActions;
	}

	@Override
	public String getCombineWithForbiddenText(InventoryItem partner) {
		return getCombinableInventoryItem(partner).combineWithForbiddenText;
	}

	@Override
	public String getCombineWithSuccessfulText(InventoryItem partner) {
		return getCombinableInventoryItem(partner).combineWithSuccessfulText;
	}

	@Override
	public List<InventoryItem> getNewInventoryItemsWhenCombinedWith(
			InventoryItem partner) {
		return getCombinableInventoryItem(partner).addInventoryItemsAction
				.getPickUpItems();
	}

	@Override
	public boolean getRemoveCombinablesWhenCombinedWith(InventoryItem partner) {
		return getCombinableInventoryItem(partner).removeCombinables;
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
	public boolean isCombiningEnabledWith(InventoryItem partner) {
		return getCombinableInventoryItem(partner).enabled;
	}

	@Override
	public boolean isUsingEnabledWith(Item item) {
		return getUsableItem(item).enabled;
	}

	@Override
	public void removeAdditionalActionFromCombineWith(InventoryItem partner,
			AbstractAction action) {
		getCombinableInventoryItem(partner).additionalCombineWithActions
				.remove(action);
	}

	@Override
	public void removeAdditionalActionFromUseWith(Item item,
			AbstractAction action) {
		getUsableItem(item).additionalUseWithActions.remove(action);
	}

	@Override
	public void removeNewInventoryItemWhenCombinedWith(InventoryItem partner,
			InventoryItem newItem) {
		getCombinableInventoryItem(partner).addInventoryItemsAction
				.removePickUpItem(newItem);
	}

	@Override
	public void setCombineWithForbiddenText(InventoryItem partner,
			String forbiddenText) {
		getCombinableInventoryItem(partner).combineWithForbiddenText = forbiddenText;
	}

	@Override
	public void setCombineWithSuccessfulText(InventoryItem partner,
			String successfulText) {
		getCombinableInventoryItem(partner).combineWithSuccessfulText = successfulText;
	}

	@Override
	public void setCombiningEnabledWith(InventoryItem partner, boolean enabled) {
		getCombinableInventoryItem(partner).enabled = enabled;
	}

	@Override
	public void setRemoveCombinablesWhenCombinedWith(InventoryItem partner,
			boolean remove) {
		getCombinableInventoryItem(partner).removeCombinables = remove;
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
		if (action instanceof SetItemLocationAction
				&& ((SetItemLocationAction) action).getItem() == item
				&& ((SetItemLocationAction) action).getNewLocation() == null) {
			// This action "removes" the item
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
	 * Gets the {@link CombinableInventoryItem} associated with the given
	 * {@link InventoryItem}. If no mapping exists, one will be created on both
	 * sides.
	 * 
	 * @param item
	 *            the item
	 * @return the associated {@link CombinableInventoryItem}.
	 */
	private CombinableInventoryItem getCombinableInventoryItem(
			InventoryItem item) {
		CombinableInventoryItem result = combinableInventoryItems.get(item);
		if (result == null) {
			combinableInventoryItems.put(item,
					result = new CombinableInventoryItem());
			// Synchronize other map
			item.combinableInventoryItems.put(this, result);
		}
		return result;
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
		combinableInventoryItems = new HashMap<InventoryItem, CombinableInventoryItem>();
	}
}