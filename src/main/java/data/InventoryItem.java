package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import data.action.AbstractAction;
import data.action.RemoveInventoryItemAction;
import data.interfaces.Combinable;
import data.interfaces.PassivelyUsable;
import data.interfaces.UsableWithSomething;

/**
 * Any item that can appear in your inventory. These items are not in locations.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class InventoryItem extends UsableObject implements UsableWithSomething, Combinable<InventoryItem> {

	/**
	 * An inventory item can be combined with others. For each inventory item
	 * there is additional information about the usability, etc. The method
	 * {@link InventoryItem#getCombineInformation(Combinable)} adds key and
	 * value, if it was not stored before. The other inventory items map will be
	 * synchronized, too.
	 * 
	 * ManyToMany since exactly TWO InventoryItems store this CombineInformation
	 * in their map.
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH })
	@JoinTable(name = "INVITEM_CII", foreignKey = @ForeignKey(name = "FK_InvItem_combineInformation_S", //
	foreignKeyDefinition = "FOREIGN KEY (InventoryItem_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_InvItem_combineInformation_D", //
	foreignKeyDefinition = "FOREIGN KEY (combineInformation_ID) REFERENCES CombineInformation (ID) ON DELETE CASCADE") )
	@MapKeyJoinColumn(foreignKey = @ForeignKey(name = "FK_InvItem_combineInformation_KEY", //
	foreignKeyDefinition = "FOREIGN KEY (combineInformation_KEY) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	@Access(AccessType.FIELD)
	private Map<InventoryItem, CombineInformation> combineInformation;

	/**
	 * The additional combine commands for the mapped inventory item. Since the
	 * additional combine commands are asymmetric, this is placed here as a map
	 * and not in {@link CombineInformation} as a List.
	 * 
	 * OneToMany since each InventoryItem has its own CombineCommands (they are
	 * not shared).
	 */
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_InvItem_additionalCombineCommands", //
	foreignKeyDefinition = "FOREIGN KEY (ADDITIONALCOMBINECOMMANDS_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	@MapKeyJoinColumn(foreignKey = @ForeignKey(name = "FK_InvItem_additionalCombineCommands_KEY", //
	foreignKeyDefinition = "FOREIGN KEY (additionalCombineCommands_KEY) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	@Access(AccessType.FIELD)
	private Map<InventoryItem, CombineCommands> additionalCombineCommands;

	/**
	 * An inventory item can be used with {@link PassivelyUsable}s. For each
	 * object there is additional information about the usability, etc. The
	 * method {@link InventoryItem#getUseWithInformation(PassivelyUsable)} adds
	 * key and value, if it was not stored before.
	 */
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_InvItem_useWithInformation", //
	foreignKeyDefinition = "FOREIGN KEY (useWithInformation_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	@MapKeyJoinColumn(foreignKey = @ForeignKey(name = "FK_InvItem_useWithInformation_KEY", //
	foreignKeyDefinition = "FOREIGN KEY (useWithInformation_KEY) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	@Access(AccessType.FIELD)
	private Map<NamedDescribedObject, UseWithInformation> useWithInformation;

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
	 * Any additional inspect or use action have to be converted and added
	 * manually. Also the using enabled status, forbidden and successful texts
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
	public void addAdditionalActionToCombineWith(Combinable<InventoryItem> partner, AbstractAction action) {
		getCombineInformation(partner).additionalCombineWithActions.add(action);
	}

	@Override
	public void addAdditionalActionToUseWith(PassivelyUsable object, AbstractAction action) {
		getUseWithInformation(object).additionalUseWithActions.add(action);
	}

	@Override
	public void addNewCombinableWhenCombinedWith(Combinable<InventoryItem> partner, InventoryItem newItem) {
		getCombineInformation(partner).addInventoryItemsAction.addPickUpItem(newItem);
	}

	@Override
	public void addAdditionalCombineCommand(Combinable<InventoryItem> partner, String command) {
		getCombineCommands(partner).commands.add(command);
	}

	@Override
	public void addAdditionalUseWithCommand(PassivelyUsable object, String command) {
		getUseWithInformation(object).additionalUseWithCommands.add(command);
	}

	@Override
	public void combineWith(Combinable<InventoryItem> partner, Game game) {
		CombineInformation combination = getCombineInformation(partner);
		if (combination.enabled) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Combining {0} with {1}",
					new Object[] { this, partner });

			// Add new inventory items
			combination.addInventoryItemsAction.triggerAction(game);
			if (combination.removeCombinables) {
				/*
				 * Remove both partners with temporary
				 * RemoveInventoryItemActions.
				 */
				new RemoveInventoryItemAction("", this).triggerAction(game);
				if (partner instanceof InventoryItem) {
					new RemoveInventoryItemAction("", (InventoryItem) partner).triggerAction(game);
				} else {
					Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
							"Not supported Combinable subclass: {0} Cannot remove it from inventory thus.",
							partner.getClass().getName());
				}

			}
		}
		// Trigger all additional actions
		for (AbstractAction abstractAction : combination.additionalCombineWithActions) {
			abstractAction.triggerAction(game);
		}
	}

	@Override
	@Transient
	public List<AbstractAction> getAdditionalActionsFromCombineWith(Combinable<InventoryItem> partner) {
		return getCombineInformation(partner).additionalCombineWithActions;
	}

	@Override
	@Transient
	public List<AbstractAction> getAdditionalActionsFromUseWith(PassivelyUsable object) {
		return getUseWithInformation(object).additionalUseWithActions;
	}

	@Override
	@Transient
	public String getCombineWithForbiddenText(Combinable<InventoryItem> partner) {
		return getCombineInformation(partner).combineWithForbiddenText;
	}

	@Override
	@Transient
	public String getCombineWithSuccessfulText(Combinable<InventoryItem> partner) {
		return getCombineInformation(partner).combineWithSuccessfulText;
	}

	@Override
	@Transient
	public List<InventoryItem> getNewCombinablesWhenCombinedWith(Combinable<InventoryItem> partner) {
		return getCombineInformation(partner).addInventoryItemsAction.getPickUpItems();
	}

	@Override
	@Transient
	public List<String> getAdditionalCombineCommands(Combinable<InventoryItem> partner) {
		return getCombineCommands(partner).commands;
	}

	@Override
	public void setAdditionalCombineCommands(Combinable<InventoryItem> partner, List<String> commands) {
		getCombineCommands(partner).commands = commands;
	}

	@Override
	@Transient
	public List<String> getAdditionalUseWithCommands(PassivelyUsable object) {
		return getUseWithInformation(object).additionalUseWithCommands;
	}

	@Override
	@Transient
	public boolean getRemoveCombinablesWhenCombinedWith(Combinable<InventoryItem> partner) {
		return getCombineInformation(partner).removeCombinables;
	}

	@Override
	@Transient
	public String getUseWithForbiddenText(PassivelyUsable object) {
		return getUseWithInformation(object).useWithForbiddenText;
	}

	@Override
	@Transient
	public String getUseWithSuccessfulText(PassivelyUsable object) {
		return getUseWithInformation(object).useWithSuccessfulText;
	}

	@Override
	@Transient
	public boolean isCombiningEnabledWith(Combinable<InventoryItem> partner) {
		return getCombineInformation(partner).enabled;
	}

	@Override
	@Transient
	public boolean isUsingEnabledWith(PassivelyUsable object) {
		return getUseWithInformation(object).enabled;
	}

	@Override
	public void removeAdditionalActionFromCombineWith(Combinable<InventoryItem> partner, AbstractAction action) {
		getCombineInformation(partner).additionalCombineWithActions.remove(action);
	}

	@Override
	public void removeAdditionalActionFromUseWith(PassivelyUsable object, AbstractAction action) {
		getUseWithInformation(object).additionalUseWithActions.remove(action);
	}

	@Override
	@Transient
	public void setAdditionalUseWithCommands(PassivelyUsable object, List<String> commands) {
		getUseWithInformation(object).additionalUseWithCommands = commands;
	}

	@Override
	public void removeAdditionalCombineCommand(Combinable<InventoryItem> partner, String command) {
		getCombineCommands(partner).commands.remove(command);
	}

	@Override
	public void removeAdditionalUseWithCommand(PassivelyUsable object, String command) {
		getUseWithInformation(object).additionalUseWithCommands.remove(command);
	}

	@Override
	public void removeNewCombinableWhenCombinedWith(Combinable<InventoryItem> partner, InventoryItem newItem) {
		getCombineInformation(partner).addInventoryItemsAction.removePickUpItem(newItem);
	}

	@Override
	public void setCombineWithForbiddenText(Combinable<InventoryItem> partner, String forbiddenText) {
		getCombineInformation(partner).combineWithForbiddenText = forbiddenText;
	}

	@Override
	public void setCombineWithSuccessfulText(Combinable<InventoryItem> partner, String successfulText) {
		getCombineInformation(partner).combineWithSuccessfulText = successfulText;
	}

	@Override
	public void setCombiningEnabledWith(Combinable<InventoryItem> partner, boolean enabled) {
		getCombineInformation(partner).enabled = enabled;
	}

	@Override
	public void setRemoveCombinablesWhenCombinedWith(Combinable<InventoryItem> partner, boolean remove) {
		getCombineInformation(partner).removeCombinables = remove;
	}

	@Override
	public void setUseWithForbiddenText(PassivelyUsable object, String forbiddenText) {
		getUseWithInformation(object).useWithForbiddenText = forbiddenText;
	}

	@Override
	public void setUseWithSuccessfulText(PassivelyUsable object, String successfulText) {
		getUseWithInformation(object).useWithSuccessfulText = successfulText;
	}

	@Override
	public void setUsingEnabledWith(PassivelyUsable object, boolean enabled) {
		getUseWithInformation(object).enabled = enabled;
	}

	@Override
	public void useWith(PassivelyUsable object, Game game) {
		// There is no "primary" action, so no "isEnabled" check
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Using {0} with {1}",
				new Object[] { this, object });

		// Trigger all additional actions
		for (AbstractAction abstractAction : getUseWithInformation(object).additionalUseWithActions) {
			abstractAction.triggerAction(game);
		}
	}

	/**
	 * Gets the {@link CombineInformation} associated with the given
	 * {@link Combinable}. If no mapping exists, one will be created on both
	 * sides.
	 * 
	 * @param item
	 *            the item
	 * @return the associated {@link CombineInformation}.
	 */
	@Transient
	private CombineInformation getCombineInformation(Combinable<InventoryItem> item) {
		if (item instanceof InventoryItem) {
			CombineInformation result = combineInformation.get((InventoryItem) item);
			if (result == null) {
				// Create a new mapping
				combineInformation.put((InventoryItem) item, result = new CombineInformation());
				// And synchronize other map
				((InventoryItem) item).combineInformation.put(this, result);
			}
			return result;
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Not supported Combinable subclass: {0}",
					item.getClass().getName());
			return null;
		}
	}

	/**
	 * Gets the {@link CombineCommands} associated with the given
	 * {@link Combinable}.
	 * 
	 * @param item
	 *            the item
	 * @return the associated {@link CombineCommands}.
	 */
	@Transient
	private CombineCommands getCombineCommands(Combinable<InventoryItem> item) {
		if (item instanceof InventoryItem) {
			CombineCommands result = additionalCombineCommands.get(item);
			if (result == null) {
				// Create a new mapping
				additionalCombineCommands.put((InventoryItem) item, result = new CombineCommands());
			}
			return result;
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Not supported Combinable subclass: {0}",
					item.getClass().getName());
			return null;
		}
	}

	/**
	 * Gets the {@link UseWithInformation} associated with the given
	 * {@link PassivelyUsable} . If no mapping exists, one will be created.
	 * 
	 * @param object
	 *            the object
	 * @return the associated {@link UseWithInformation}.
	 */
	@Transient
	private UseWithInformation getUseWithInformation(PassivelyUsable object) {
		UseWithInformation result;

		if (object instanceof Item || object instanceof Person || object instanceof Way) {
			result = useWithInformation.get(object);
			if (result == null) {
				// Cast to NamedDescribedObject.
				useWithInformation.put((NamedDescribedObject) object, result = new UseWithInformation());
			}
		} else {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
					"Not supported PassivelyUsable subclass: {0}", object.getClass().getName());
			return null;
		}
		return result;
	}

	/**
	 * Ensure that usage information exists for a Person or Item.
	 * 
	 * @param object
	 *            the object.
	 */
	public void ensureHasUsageInformation(PassivelyUsable object) {
		getUseWithInformation(object);
	}

	/**
	 * Ensure that usage combine exists for an InventoryItem.
	 * 
	 * @param item
	 *            the item.
	 */
	public void ensureHasCombineInformation(InventoryItem item) {
		getCombineInformation(item);
		getAdditionalCombineCommands(item);
		item.getAdditionalCombineCommands(this);
	}

	/**
	 * Obtains all objects for which additional information for using them
	 * exists.
	 * 
	 * @return a list of objects.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transient
	public List<PassivelyUsable> getObjectsUsableWith() {
		// The set contains NamedDescribedObjects. By not parameterizing the
		// ArrayList, we assume that all included items implement
		// PassivelyUsable. The method getUseWithInformation should ensure this.
		return new ArrayList(useWithInformation.keySet());
	}

	/**
	 * Obtains all inventory items for which additional information for
	 * combining them exists.
	 * 
	 * @return a list of inventory items.
	 */
	@Transient
	public List<InventoryItem> getInventoryItemsCombinableWith() {
		return new ArrayList<>(combineInformation.keySet());
	}


	/**
	 * Initializes the fields.
	 */
	private final void init() {
		useWithInformation = new HashMap<>();
		combineInformation = new HashMap<>();
		additionalCombineCommands = new HashMap<>();
	}
}
