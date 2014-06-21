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
import data.action.SetItemLocationAction;
import data.interfaces.HasLocation;
import data.interfaces.Takeable;

/**
 * Any item in the game world. This items are stored in locations but cannot be
 * in your inventory.
 * 
 * @author Satia
 */
@Entity
public class Item extends UsableObject implements Takeable, HasLocation {
	/**
	 * The {@link AddInventoryItemsAction}. The successfulText and forbiddenText
	 * by it are being used.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private AddInventoryItemsAction addInventoryItemsAction;

	/**
	 * All additional actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalTakeActions;

	/**
	 * The current location of the item. May be {@code null}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location location;

	/**
	 * The {@link SetItemLocationAction} which would set the location to
	 * {@code null}.
	 * 
	 * Note: This is NOT the Inverse connection of
	 * {@link SetItemLocationAction#item}.
	 */
	// TODO why not unnullable?
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn//(nullable = false)
	private SetItemLocationAction removeAction;

	/**
	 * A personalized error message displayed if taking this item was forbidden.
	 */
	private String takeForbiddenText;

	/**
	 * A personalized error message displayed if taking this item was successful.
	 */
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
	public AddInventoryItemsAction getAddInventoryItemsAction() {
		return addInventoryItemsAction;
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromTake() {
		return additionalTakeActions;
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
	public void take() {
		if (isTakingEnabled()) {
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
		this.removeAction = new SetItemLocationAction(this, null);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
		setRemoveItem(true);
	}
}