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
import data.action.TakeAction;
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
	 * The take action.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private TakeAction takeAction;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link Item#Item(String, String, String)} or
	 *             {@link Item#Item(Location, String, String, String)} instead.
	 */
	@Deprecated
	public Item() {
		this.takeAction = new TakeAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
	}

	/**
	 * Copies name, identifiers, short and long description from the given
	 * inventory item.
	 * 
	 * @param item
	 *            the item to use name, short and long description from
	 */
	public Item(InventoryItem item) {
		super(item.getName(), item.getShortDescription(), item
				.getLongDescription());
		setIdentifiers(item.getIdentifiers());
		this.takeAction = new TakeAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
	}

	/**
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param shortDescription
	 *            the shortDescription
	 * @param longDescription
	 *            the longDescription
	 */
	public Item(Location location, String name, String shortDescription,
			String longDescription) {
		super(name, shortDescription, longDescription);
		setLocation(location);
		this.takeAction = new TakeAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
	}

	/**
	 * @param name
	 *            the name
	 * @param shortDescription
	 *            the shortDescription
	 * @param longDescription
	 *            the longDescription
	 */
	public Item(String name, String shortDescription, String longDescription) {
		super(name, shortDescription, longDescription);
		this.takeAction = new TakeAction(this);
		this.additionalTakeActions = new ArrayList<AbstractAction>();
	}

	@Override
	public void addAdditionalActionToTake(AbstractAction action) {
		additionalTakeActions.add(action);
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
	public TakeAction getTakeAction() {
		return takeAction;
	}

	@Override
	public String getTakeForbiddenText() {
		return takeAction.getForbiddenText();
	}

	@Override
	public String getTakeSuccessfulText() {
		return takeAction.getSuccessfulText();
	}

	@Override
	public boolean isTakingEnabled() {
		return takeAction.isEnabled();
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
	public void setTakeForbiddenText(String forbiddenText) {
		takeAction.setForbiddenText(forbiddenText);
	}

	@Override
	public void setTakeSuccessfulText(String successfulText) {
		takeAction.setSuccessfulText(successfulText);
	}

	@Override
	public void setTakingEnabled(boolean enabled) {
		takeAction.setEnabled(enabled);
	}

	@Override
	public void take() {
		takeAction.triggerAction();
		for (AbstractAction abstractAction : additionalTakeActions) {
			abstractAction.triggerAction();
		}
	}
}