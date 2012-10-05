package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.RemoveItemAction;
import data.interfaces.Takeable;

/**
 * Any item in the game world. This items are stored in locations but cannot be
 * in your inventory.
 * 
 * @author Satia
 */
@Entity
public class Item extends NamedObject implements Takeable {

	/**
	 * The {@link AddInventoryItemsAction}. The successfulText and forbiddenText
	 * by it are being used.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private AddInventoryItemsAction addInventoryItemsAction;

	/**
	 * All additional actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalTakeActions;

	/**
	 * The current location of the item.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	/**
	 * The {@link RemoveItemAction}.
	 */
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "item")
	private RemoveItemAction removeAction;

	/**
	 * No-arg constructor for the database.
	 * 
	 * By default taking will be disabled, but the removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @deprecated Use {@link Item#Item(String, String)} or
	 *             {@link Item#Item(Location, String, String)} instead.
	 */
	@Deprecated
	public Item() {
		this.addInventoryItemsAction = new AddInventoryItemsAction(false);
		this.removeAction = new RemoveItemAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
		setRemoveItem(true);
	}

	/**
	 * Copies name, description, identifiers and inspectionText from the given
	 * item.
	 * 
	 * By default taking will be disabled, but the removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @param item
	 *            the item to use name, description, identifiers and
	 *            inspectionText from
	 */
	public Item(InventoryItem item) {
		super(item.getName(), item.getDescription());
		this.addInventoryItemsAction = new AddInventoryItemsAction(false);
		this.removeAction = new RemoveItemAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
		setRemoveItem(true);

		setIdentifiers(item.getIdentifiers());
		setInspectionText(item.getInspectionText());
	}

	/**
	 * By default taking will be disabled, but the removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(Location location, String name, String description) {
		super(name, description);
		setLocation(location);
		this.addInventoryItemsAction = new AddInventoryItemsAction(false);
		this.removeAction = new RemoveItemAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
		setRemoveItem(true);
	}

	/**
	 * By default taking will be disabled, but the removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(String name, String description) {
		super(name, description);
		this.addInventoryItemsAction = new AddInventoryItemsAction(false);
		this.removeAction = new RemoveItemAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
		setRemoveItem(true);
	}

	@Override
	public void addAdditionalActionToTake(AbstractAction action) {
		additionalTakeActions.add(action);
	}

	@Override
	public AddInventoryItemsAction getAddInventoryItemsAction() {
		return addInventoryItemsAction;
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromTake() {
		return additionalTakeActions;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	@Override
	public String getTakeForbiddenText() {
		return addInventoryItemsAction.getForbiddenText();
	}

	@Override
	public String getTakeSuccessfulText() {
		return addInventoryItemsAction.getSuccessfulText();
	}

	@Override
	public boolean isRemoveItem() {
		return removeAction.isEnabled();
	}

	@Override
	public boolean isTakingEnabled() {
		return addInventoryItemsAction.isEnabled();
	}

	@Override
	public void removeAdditionalActionFromTake(AbstractAction action) {
		additionalTakeActions.remove(action);
	}

	/**
	 * Sets a new location for this item. The item is removed from the old
	 * location's list and added to the new one.
	 * 
	 * @param location
	 *            the location to be set
	 */
	public void setLocation(Location location) {
		if (this.location != null) {
			this.location.removeItem(this);
		}
		if (location != null) {
			location.addItem(this);
		}
		this.location = location;
	}

	@Override
	public void setRemoveItem(boolean removeItem) {
		removeAction.setEnabled(removeItem);
	}

	@Override
	public void setTakeForbiddenText(String forbiddenText) {
		addInventoryItemsAction.setForbiddenText(forbiddenText);
	}

	@Override
	public void setTakeSuccessfulText(String successfulText) {
		addInventoryItemsAction.setSuccessfulText(successfulText);
	}

	@Override
	public void setTakingEnabled(boolean enabled) {
		addInventoryItemsAction.setEnabled(enabled);
	}

	@Override
	public void take() {
		//  Check just for performance.
		if (isTakingEnabled()) {
			addInventoryItemsAction.triggerAction();
			removeAction.triggerAction();
		}
		for (AbstractAction abstractAction : additionalTakeActions) {
			abstractAction.triggerAction();
		}
	}
}