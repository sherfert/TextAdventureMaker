package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import data.action.ChangeInspectableObjectAction;
import data.action.ChangeNamedObjectAction;
import data.action.RemoveInventoryItemAction;
import data.action.ChangeItemAction;
import data.interfaces.Combinable;
import data.interfaces.HasId;
import data.interfaces.HasLocation;
import data.interfaces.UsableWithHasLocation;

/**
 * Any item that can appear in your inventory. These items are not in locations.
 * 
 * @author Satia
 */
@Entity
public class InventoryItem extends UsableObject implements
		UsableWithHasLocation, Combinable<InventoryItem> {

	/**
	 * Attributes of an {@link InventoryItem} that can be used with an
	 * {@link InventoryItem}.
	 * 
	 * @author Satia
	 */
	@Entity
	private static class CombinableInventoryItem implements HasId {

		/**
		 * The action adding the new inventory items.
		 */
		@ManyToOne(cascade = CascadeType.PERSIST)
		@JoinColumn(nullable = false)
		private final AddInventoryItemsAction addInventoryItemsAction;

		/**
		 * All actions triggered when the two {@link InventoryItem}s are
		 * combined. They are triggered regardless of the enabled status.
		 */
		@ManyToMany(cascade = CascadeType.PERSIST)
		@JoinTable
		private final List<AbstractAction> additionalCombineWithActions;

		/**
		 * The text displayed when combining is disabled but the user tries to
		 * trigger the connection or suggests the default text to be displayed
		 * if {@code null}.
		 */
		@Column(nullable = true)
		private String combineWithForbiddenText;

		/**
		 * The text displayed when combining is enabled and the user tries to
		 * trigger the connection or suggests the default text to be displayed
		 * if {@code null}.
		 */
		@Column(nullable = true)
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
			additionalCombineWithActions = new ArrayList<>();
			addInventoryItemsAction = new AddInventoryItemsAction();
			enabled = false;
			removeCombinables = false;
		}

		@Override
		public String toString() {
			return "CombinableInventoryItem{" + "addInventoryItemsActionID="
					+ addInventoryItemsAction.getId()
					+ ", additionalCombineWithActionsIDs="
					+ NamedObject.getIDList(additionalCombineWithActions)
					+ ", combineWithForbiddenText=" + combineWithForbiddenText
					+ ", combineWithSuccessfulText="
					+ combineWithSuccessfulText + ", enabled=" + enabled
					+ ", id=" + id + ", removeCombinables=" + removeCombinables
					+ '}';
		}

		@Override
		public int getId() {
			return id;
		}
	}

	/**
	 * Attributes of an {@link HasLocation} that can be used with an
	 * {@link InventoryItem}.
	 * 
	 * JPA requires this inner class to be static. A back-reference to the
	 * owning {@link InventoryItem} could optionally be added.
	 * 
	 * @author Satia
	 */
	@Entity
	private static class UsableHasLocation implements HasId {

		/**
		 * All actions triggered when the {@link InventoryItem} is used with the
		 * mapped {@link HasLocation}. They are triggered regardless of the
		 * enabled status.
		 */
		@ManyToMany(cascade = CascadeType.PERSIST)
		@JoinTable
		private final List<AbstractAction> additionalUseWithActions;

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
		 * trigger the connection or suggests the default text to be displayed
		 * if {@code null}.
		 */
		@Column(nullable = true)
		private String useWithForbiddenText;

		/**
		 * The text displayed when using is enabled and the user tries to
		 * trigger the connection or suggests the default text to be displayed
		 * if {@code null}.
		 */
		@Column(nullable = true)
		private String useWithSuccessfulText;

		/**
		 * Disabled by default and no forbidden/successful texts.
		 */
		public UsableHasLocation() {
			additionalUseWithActions = new ArrayList<>();
			enabled = false;
		}

		@Override
		public String toString() {
			return "UsableHasLocation{" + "additionalUseWithActionsIDs="
					+ NamedObject.getIDList(additionalUseWithActions)
					+ ", enabled=" + enabled + ", id=" + id
					+ ", useWithForbiddenText=" + useWithForbiddenText
					+ ", useWithSuccessfulText=" + useWithSuccessfulText + '}';
		}

		@Override
		public int getId() {
			return id;
		}
	}

	/**
	 * An inventory item can be combined with others. For each inventory item
	 * there are additional informations about the usability, etc. The method
	 * {@link InventoryItem#getCombinableInventoryItem(Combinable)} adds key and
	 * value, if it was not stored before. The other inventory item's map will
	 * be synchronized, too.
	 */
	// XXX why Maps not unnullable?
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	@MapKeyJoinColumn
	private Map<InventoryItem, CombinableInventoryItem> combinableInventoryItems;

	/**
	 * An inventory item can be used with {@link Item}s. For each object there
	 * are additional informations about the usability, etc. The method
	 * {@link InventoryItem#getUsableHasLocation(HasLocation)} adds key and
	 * value, if it was not stored before. It will choose the right map from the
	 * two.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	@MapKeyJoinColumn
	private Map<Item, UsableHasLocation> usableItems;

	/**
	 * An inventory item can be used with {@link Person}s. For each object there
	 * are additional informations about the usability, etc. The method
	 * {@link InventoryItem#getUsableHasLocation(HasLocation)} adds key and
	 * value, if it was not stored before. It will choose the right map from the
	 * two.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	@MapKeyJoinColumn
	private Map<Person, UsableHasLocation> usablePersons;

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
	 * Copies name, inspection text, and identifiers from the Item.
	 * 
	 * Any additional inspect or use action have to converted and added
	 * manually. Also the using enabled status, forbidden and successfull texts
	 * have to be set manually.
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
		// // Additional inspect actions
		// for (AbstractAction action : item.getAdditionalActionsFromInspect())
		// {
		// addAdditionalActionToInspect(convertInventoryItemActionToItemAction(
		// item, action));
		// }
		// // Additional use actions
		// for (AbstractAction action : item.getAdditionalActionsFromUse()) {
		// addAdditionalActionToUse(convertInventoryItemActionToItemAction(
		// item, action));
		// }
		// // Use forbidden/successful text
		// setUseForbiddenText(item.getUseForbiddenText());
		// setUseSuccessfulText(item.getUseSuccessfulText());
		// // Using enabled status
		// setUsingEnabled(item.isUsingEnabled());

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
	public void addAdditionalActionToCombineWith(
			Combinable<InventoryItem> partner, AbstractAction action) {
		getCombinableInventoryItem(partner).additionalCombineWithActions
				.add(action);
	}

	@Override
	public void addAdditionalActionToUseWith(HasLocation object,
			AbstractAction action) {
		getUsableHasLocation(object).additionalUseWithActions.add(action);
	}

	@Override
	public void addNewCombinableWhenCombinedWith(
			Combinable<InventoryItem> partner, InventoryItem newItem) {
		getCombinableInventoryItem(partner).addInventoryItemsAction
				.addPickUpItem(newItem);
	}

	@Override
	public void combineWith(Combinable<InventoryItem> partner) {
		CombinableInventoryItem combination = getCombinableInventoryItem(partner);
		if (combination.enabled) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Combining {0} with {1}", new Object[] { this, partner });

			// Add new inventory items
			combination.addInventoryItemsAction.triggerAction();
			if (combination.removeCombinables) {
				/*
				 * Remove both partners with temporary
				 * RemoveInventoryItemActions.
				 */
				new RemoveInventoryItemAction(this).triggerAction();
				if (partner instanceof InventoryItem) {
					new RemoveInventoryItemAction((InventoryItem) partner)
							.triggerAction();
				} else {
					Logger.getLogger(this.getClass().getName())
							.log(Level.WARNING,
									"Not supported Combinable subclass: {0} Cannot remove it from inventory thus.",
									partner.getClass().getName());
				}

			}
		}
		// Trigger all additional actions
		for (AbstractAction abstractAction : combination.additionalCombineWithActions) {
			abstractAction.triggerAction();
		}
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromCombineWith(
			Combinable<InventoryItem> partner) {
		return getCombinableInventoryItem(partner).additionalCombineWithActions;
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromUseWith(
			HasLocation object) {
		return getUsableHasLocation(object).additionalUseWithActions;
	}

	@Override
	public String getCombineWithForbiddenText(Combinable<InventoryItem> partner) {
		return getCombinableInventoryItem(partner).combineWithForbiddenText;
	}

	@Override
	public String getCombineWithSuccessfulText(Combinable<InventoryItem> partner) {
		return getCombinableInventoryItem(partner).combineWithSuccessfulText;
	}

	@Override
	public List<InventoryItem> getNewCombinablesWhenCombinedWith(
			Combinable<InventoryItem> partner) {
		return getCombinableInventoryItem(partner).addInventoryItemsAction
				.getPickUpItems();
	}

	@Override
	public boolean getRemoveCombinablesWhenCombinedWith(
			Combinable<InventoryItem> partner) {
		return getCombinableInventoryItem(partner).removeCombinables;
	}

	@Override
	public String getUseWithForbiddenText(HasLocation object) {
		return getUsableHasLocation(object).useWithForbiddenText;
	}

	@Override
	public String getUseWithSuccessfulText(HasLocation object) {
		return getUsableHasLocation(object).useWithSuccessfulText;
	}

	@Override
	public boolean isCombiningEnabledWith(Combinable<InventoryItem> partner) {
		return getCombinableInventoryItem(partner).enabled;
	}

	@Override
	public boolean isUsingEnabledWith(HasLocation object) {
		return getUsableHasLocation(object).enabled;
	}

	@Override
	public void removeAdditionalActionFromCombineWith(
			Combinable<InventoryItem> partner, AbstractAction action) {
		getCombinableInventoryItem(partner).additionalCombineWithActions
				.remove(action);
	}

	@Override
	public void removeAdditionalActionFromUseWith(HasLocation object,
			AbstractAction action) {
		getUsableHasLocation(object).additionalUseWithActions.remove(action);
	}

	@Override
	public void removeNewCombinableWhenCombinedWith(
			Combinable<InventoryItem> partner, InventoryItem newItem) {
		getCombinableInventoryItem(partner).addInventoryItemsAction
				.removePickUpItem(newItem);
	}

	@Override
	public void setCombineWithForbiddenText(Combinable<InventoryItem> partner,
			String forbiddenText) {
		getCombinableInventoryItem(partner).combineWithForbiddenText = forbiddenText;
	}

	@Override
	public void setCombineWithSuccessfulText(Combinable<InventoryItem> partner,
			String successfulText) {
		getCombinableInventoryItem(partner).combineWithSuccessfulText = successfulText;
	}

	@Override
	public void setCombiningEnabledWith(Combinable<InventoryItem> partner,
			boolean enabled) {
		getCombinableInventoryItem(partner).enabled = enabled;
	}

	@Override
	public void setRemoveCombinablesWhenCombinedWith(
			Combinable<InventoryItem> partner, boolean remove) {
		getCombinableInventoryItem(partner).removeCombinables = remove;
	}

	@Override
	public void setUseWithForbiddenText(HasLocation object, String forbiddenText) {
		getUsableHasLocation(object).useWithForbiddenText = forbiddenText;
	}

	@Override
	public void setUseWithSuccessfulText(HasLocation object,
			String successfulText) {
		getUsableHasLocation(object).useWithSuccessfulText = successfulText;
	}

	@Override
	public void setUsingEnabledWith(HasLocation object, boolean enabled) {
		getUsableHasLocation(object).enabled = enabled;
	}

	@Override
	public void useWith(HasLocation object) {
		// There is no "primary" action, so no "isEnabled" check
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Using {0} with {1}", new Object[] { this, object });

		// Trigger all additional actions
		for (AbstractAction abstractAction : getUsableHasLocation(object).additionalUseWithActions) {
			abstractAction.triggerAction();
		}
	}

	/**
	 * This is incomplete and should not be used.
	 * 
	 * Converts any action connected with the given {@link Item} into an
	 * equivalent action connected to this InventoryItem.
	 * 
	 * @param item
	 *            the {@link Item}
	 * @param action
	 *            the action to be converted
	 * @return a converted action or the action itself.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private AbstractAction convertInventoryItemActionToItemAction(Item item,
			AbstractAction action) {
		if (action instanceof ChangeItemAction
				&& ((ChangeItemAction) action).getObject() == item
				&& ((ChangeItemAction) action).getNewLocation() == null) {
			// This action "removes" the item
			RemoveInventoryItemAction result = new RemoveInventoryItemAction(
					this);
			result.setEnabled(action.isEnabled());
			return result;
		} else if (action instanceof ChangeNamedObjectAction
				&& ((ChangeNamedObjectAction) action).getObject() == item) {
			ChangeNamedObjectAction result;
			if (action instanceof ChangeInspectableObjectAction) {
				result = new ChangeInspectableObjectAction(this);

				((ChangeInspectableObjectAction) result)
						.setIdentifiersToAdd(((ChangeInspectableObjectAction) action)
								.getIdentifiersToAdd());
				((ChangeInspectableObjectAction) result)
						.setIdentifiersToRemove(((ChangeInspectableObjectAction) action)
								.getIdentifiersToRemove());
				((ChangeInspectableObjectAction) result)
						.setNewInspectionText(((ChangeInspectableObjectAction) action)
								.getNewInspectionText());
			} else {
				result = new ChangeNamedObjectAction(this);
			}

			result.setEnabled(action.isEnabled());
			result.setNewDescription(((ChangeNamedObjectAction) action)
					.getNewDescription());
			result.setNewName(((ChangeNamedObjectAction) action).getNewName());
			return result;
		}
		return action;
	}

	/**
	 * Gets the {@link CombinableInventoryItem} associated with the given
	 * {@link Combinable}. If no mapping exists, one will be created on both
	 * sides.
	 * 
	 * @param item
	 *            the item
	 * @return the associated {@link CombinableInventoryItem}.
	 */
	private CombinableInventoryItem getCombinableInventoryItem(
			Combinable<InventoryItem> item) {
		if (item instanceof InventoryItem) {
			CombinableInventoryItem result = combinableInventoryItems
					.get((InventoryItem) item);
			if (result == null) {
				combinableInventoryItems.put((InventoryItem) item,
						result = new CombinableInventoryItem());
				// Synchronize other map
				((InventoryItem) item).combinableInventoryItems.put(this,
						result);
			}
			return result;
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Not supported Combinable subclass: {0}",
					item.getClass().getName());
			return null;
		}
	}

	/**
	 * Gets the {@link UsableHasLocation} associated with the given
	 * {@link HasLocation} . If no mapping exists, one will be created.
	 * 
	 * @param object
	 *            the object
	 * @return the associated {@link UsableHasLocation}.
	 */
	private UsableHasLocation getUsableHasLocation(HasLocation object) {
		UsableHasLocation result;

		if (object instanceof Item) {
			result = usableItems.get((Item) object);
			if (result == null) {
				usableItems
						.put((Item) object, result = new UsableHasLocation());
			}
		} else if (object instanceof Person) {
			result = usablePersons.get((Person) object);
			if (result == null) {
				usablePersons.put((Person) object,
						result = new UsableHasLocation());
			}
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Not supported HasLocation subclass: {0}",
					object.getClass().getName());
			return null;
		}
		return result;
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		usableItems = new HashMap<>();
		usablePersons = new HashMap<>();
		combinableInventoryItems = new HashMap<>();
	}

	@Override
	public String toString() {
		return "InventoryItem{" + "combinableInventoryItems="
				+ NamedObject.getIDMap(combinableInventoryItems)
				+ ", usableItems=" + NamedObject.getIDMap(usableItems)
				+ ", usablePersons=" + NamedObject.getIDMap(usablePersons)
				+ " " + super.toString() + '}';
	}

}
