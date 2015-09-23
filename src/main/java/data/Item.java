package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.ChangeItemAction;
import data.interfaces.HasLocation;
import data.interfaces.Takeable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Any item in the game world. This items are stored in locations but cannot be
 * in your inventory.
 * 
 * @author Satia
 */
@Entity
public class Item extends UsableObject implements Takeable, HasLocation {

	/**
	 * The {@link AddInventoryItemsAction}.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private AddInventoryItemsAction addInventoryItemsAction;

	/**
	 * All additional take actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalTakeActions;
	
	/**
	 * All additional take commands.
	 */
	@ElementCollection
	private List<String> additionalTakeCommands;

	/**
	 * The current location of the item. May be {@code null}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	/**
	 * The {@link ChangeItemAction} which would set the location to {@code null}
	 * .
	 * 
	 * Note: This is NOT the Inverse connection of {@link ChangeItemAction#item}
	 * .
	 */
	// XXX why not unnullable?
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	// (nullable = false)
	private ChangeItemAction removeAction;

	/**
	 * A personalized error message displayed if taking this item was forbidden
	 * or suggests the default text to be displayed if {@code null}.
	 */
	@Column(nullable = true)
	private String takeForbiddenText;

	/**
	 * A personalized error message displayed if taking this item was successful
	 * or suggests the default text to be displayed if {@code null}.
	 */
	@Column(nullable = true)
	private String takeSuccessfulText;

	/**
	 * No-arg constructor for the database.
	 * 
	 * By default taking will be disabled, but removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @deprecated Use {@link Item#Item(String, String)} or
	 *             {@link Item#Item(Location, String, String)} instead.
	 */
	@Deprecated
	public Item() {
		init();
	}

	/**
	 * By default taking will be disabled, but removeItem (when taking is
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
		init();
		setLocation(location);
	}

	/**
	 * By default taking will be disabled, but removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(String name, String description) {
		super(name, description);
		init();
	}

	@Override
	public void addAdditionalActionToTake(AbstractAction action) {
		additionalTakeActions.add(action);
	}
	
	@Override
	public void addAdditionalTakeCommand(String command) {
		additionalTakeCommands.add(command);
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromTake() {
		return additionalTakeActions;
	}
	
	@Override
	public List<String> getAdditionalTakeCommands() {
		return additionalTakeCommands;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getTakeForbiddenText() {
		return takeForbiddenText;
	}

	@Override
	public String getTakeSuccessfulText() {
		return takeSuccessfulText;
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
	
	@Override
	public void removeAdditionalTakeCommand(String command) {
		additionalTakeCommands.remove(command);
	}

	// final as called in constructor.
	@Override
	public final void setLocation(Location location) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Setting location of {0} to {1}",
				new Object[] { this, location });

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
		takeForbiddenText = forbiddenText;
	}

	@Override
	public void setTakeSuccessfulText(String successfulText) {
		takeSuccessfulText = successfulText;
	}

	@Override
	public void setTakingEnabled(boolean enabled) {
		addInventoryItemsAction.setEnabled(enabled);
	}

	@Override
	public void setPickUpItems(List<InventoryItem> pickUpItems) {
		addInventoryItemsAction.setPickUpItems(pickUpItems);

	}

	@Override
	public void addPickUpItem(InventoryItem item) {
		addInventoryItemsAction.addPickUpItem(item);

	}

	@Override
	public void removePickUpItem(InventoryItem item) {
		addInventoryItemsAction.removePickUpItem(item);

	}

	@Override
	public void take() {
		if (isTakingEnabled()) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINE,
					"Taking {0}", this);

			addInventoryItemsAction.triggerAction();
			removeAction.triggerAction();
		}
		for (AbstractAction abstractAction : additionalTakeActions) {
			abstractAction.triggerAction();
		}
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		this.addInventoryItemsAction = new AddInventoryItemsAction(false);
		this.removeAction = new ChangeItemAction(this);
		this.removeAction.setNewLocation(null);
		this.additionalTakeActions = new ArrayList<>();
		this.additionalTakeCommands = new ArrayList<>();
		setRemoveItem(true);
	}

	@Override
	public String toString() {
		return "Item{" + "addInventoryItemsActionID="
				+ addInventoryItemsAction.getId()
				+ ", additionalTakeActionsIDs="
				+ NamedObject.getIDList(additionalTakeActions)
				+ ", locationID=" + location.getId() + ", removeActionID="
				+ removeAction.getId() + ", takeForbiddenText="
				+ takeForbiddenText + ", takeSuccessfulText="
				+ takeSuccessfulText + " " + super.toString() + '}';
	}
}
