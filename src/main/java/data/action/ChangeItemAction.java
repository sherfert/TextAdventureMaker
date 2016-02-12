package data.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import data.Game;
import data.InventoryItem;
import data.Item;
import data.Location;
import data.NamedObject;

/**
 * An action changing attributes of an {@link Item}.
 * 
 * It can also be used to ADD items to a location, if the former location was
 * {@code null} or to REMOVE items from a location, if the new location is
 * {@code null}.
 * 
 * @author Satia
 */
@Entity
public class ChangeItemAction extends ChangeUsableObjectAction {

	/**
	 * Only if this is {@code true}, the location will be changed. This is
	 * necessary, as {@code null} is a valid location and cannot be used to
	 * identify no changes to be applied.
	 */
	@Column(nullable = false)
	private boolean changeLocation;

	/**
	 * The new location of the item. Can be {@code null}, which means the Item
	 * will be removed. To apply these changes, {@link #changeLocation} has to
	 * be set to {@code true}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true)
	private Location newLocation;

	/**
	 * The new takeForbiddenText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newTakeForbiddenText;

	/**
	 * The new takeSuccessfulText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newTakeSuccessfulText;

	/**
	 * Enabling or disabling if the item is takeable.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enablingTakeable;

	/**
	 * All pick up items to be added.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<InventoryItem> pickUpItemsToAdd;

	/**
	 * All pick up items to be removed.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<InventoryItem> pickUpItemsToRemove;

	/**
	 * Enabling or disabling if the item will be removed when taken.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enablingRemoveItem;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link ChangeItemAction#ChangeItemAction(Item)} instead.
	 */
	@Deprecated
	public ChangeItemAction() {
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeItemAction(Item object) {
		super(object);
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param newLocation
	 *            The new location of the item. Can be {@code null}, which means
	 *            the Item will be removed.
	 */
	public ChangeItemAction(Item object, Location newLocation) {
		super(object);
		init();
		setNewLocation(newLocation);
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeItemAction(Item object, boolean enabled) {
		super(object, enabled);
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param newLocation
	 *            The new location of the item. Can be {@code null}, which means
	 *            the Item will be removed.
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeItemAction(Item object, Location newLocation, boolean enabled) {
		super(object, enabled);
		init();
		setNewLocation(newLocation);
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.enablingTakeable = Enabling.DO_NOT_CHANGE;
		this.enablingRemoveItem = Enabling.DO_NOT_CHANGE;
		this.pickUpItemsToAdd = new ArrayList<>();
		this.pickUpItemsToRemove = new ArrayList<>();
	}

	@Override
	public Item getObject() {
		return (Item) super.getObject();
	}

	/**
	 * @return the newLocation
	 */
	public Location getNewLocation() {
		return newLocation;
	}

	/**
	 * This also sets changeLocation to true. If you want to undo this,
	 * explicitly call {@code setChangeLocation(false)}.
	 * 
	 * @param newLocation
	 *            the newLocation to set
	 */
	public final void setNewLocation(Location newLocation) {
		this.newLocation = newLocation;
		this.changeLocation = true;
	}

	/**
	 * @return the changeLocation
	 */
	public boolean isChangeLocation() {
		return changeLocation;
	}

	/**
	 * @param changeLocation
	 *            the changeLocation to set
	 */
	public void setChangeLocation(boolean changeLocation) {
		this.changeLocation = changeLocation;
	}

	/**
	 * @return the newTakeForbiddenText
	 */
	public String getNewTakeForbiddenText() {
		return newTakeForbiddenText;
	}

	/**
	 * @param newTakeForbiddenText
	 *            the newTakeForbiddenText to set
	 */
	public void setNewTakeForbiddenText(String newTakeForbiddenText) {
		this.newTakeForbiddenText = newTakeForbiddenText;
	}

	/**
	 * @return the newTakeSuccessfulText
	 */
	public String getNewTakeSuccessfulText() {
		return newTakeSuccessfulText;
	}

	/**
	 * @param newTakeSuccessfulText
	 *            the newTakeSuccessfulText to set
	 */
	public void setNewTakeSuccessfulText(String newTakeSuccessfulText) {
		this.newTakeSuccessfulText = newTakeSuccessfulText;
	}

	/**
	 * @return the enablingTakeable
	 */
	public Enabling getEnablingTakeable() {
		return enablingTakeable;
	}

	/**
	 * @param enablingTakeable the enablingTakeable to set
	 */
	public void setEnablingTakeable(Enabling enablingTakeable) {
		this.enablingTakeable = enablingTakeable;
	}

	/**
	 * @return the pickUpItemsToAdd
	 */
	public List<InventoryItem> getPickUpItemsToAdd() {
		return pickUpItemsToAdd;
	}

	/**
	 * @param pickUpItemsToAdd the pickUpItemsToAdd to set
	 */
	public void setPickUpItemsToAdd(List<InventoryItem> pickUpItemsToAdd) {
		this.pickUpItemsToAdd = pickUpItemsToAdd;
	}

	/**
	 * @return the pickUpItemsToRemove
	 */
	public List<InventoryItem> getPickUpItemsToRemove() {
		return pickUpItemsToRemove;
	}

	/**
	 * @param pickUpItemsToRemove the pickUpItemsToRemove to set
	 */
	public void setPickUpItemsToRemove(List<InventoryItem> pickUpItemsToRemove) {
		this.pickUpItemsToRemove = pickUpItemsToRemove;
	}

	/**
	 * @return the enablingRemoveItem
	 */
	public Enabling getEnablingRemoveItem() {
		return enablingRemoveItem;
	}

	/**
	 * @param enablingRemoveItem the enablingRemoveItem to set
	 */
	public void setEnablingRemoveItem(Enabling enablingRemoveItem) {
		this.enablingRemoveItem = enablingRemoveItem;
	}
	
	/**
	 * Adds a pick up item to be added.
	 * 
	 * @param item
	 *            the item
	 */
	public void addPickUpItemToAdd(InventoryItem item) {
		pickUpItemsToAdd.add(item);
	}

	/**
	 * Adds a pick up item to be removed.
	 * 
	 * @param item
	 *            the item
	 */
	public void addPickUpItemToRemove(InventoryItem item) {
		pickUpItemsToAdd.add(item);
	}
	
	/**
	 * Removes a pick up item to be added.
	 * 
	 * @param item
	 *            the item
	 */
	public void removePickUpItemToAdd(InventoryItem item) {
		pickUpItemsToRemove.remove(item);
	}

	/**
	 * Removes a pick up item to be removed.
	 * 
	 * @param item
	 *            the item
	 */
	public void removePickUpItemToRemove(InventoryItem item) {
		pickUpItemsToRemove.remove(item);
	}

	@Override
	protected void doAction(Game game) {
		// Call the super method
		super.doAction(game);

		if (changeLocation) {
			getObject().setLocation(newLocation);
		}
		// Change fields
		if (newTakeForbiddenText != null) {
			getObject().setTakeForbiddenText(newTakeForbiddenText);
		}
		if (newTakeSuccessfulText != null) {
			getObject().setTakeSuccessfulText(newTakeSuccessfulText);
		}
		if (enablingTakeable == Enabling.ENABLE) {
			getObject().setTakingEnabled(true);
		} else if (enablingTakeable == Enabling.DISABLE) {
			getObject().setTakingEnabled(false);
		}
		if (enablingRemoveItem == Enabling.ENABLE) {
			getObject().setRemoveItem(true);
		} else if (enablingRemoveItem == Enabling.DISABLE) {
			getObject().setRemoveItem(false);
		}

		// Add and remove pick up items
		for (InventoryItem item : pickUpItemsToAdd) {
			getObject().addPickUpItem(item);
		}
		for (InventoryItem item : pickUpItemsToRemove) {
			getObject().removePickUpItem(item);
		}
	}

	@Override
	public String toString() {
		return "ChangeItemAction{changeLocation=" + changeLocation
				+ ", newLocationID="
				+ (newLocation != null ? newLocation.getId() : "null")
				+ ", newTakeForbiddenText=" + newTakeForbiddenText
				+ ", newTakeSuccessfulText=" + newTakeSuccessfulText
				+ ", enablingTakeable=" + enablingTakeable
				+ ", pickUpItemsToAddIDs="
				+ NamedObject.getIDList(pickUpItemsToAdd)
				+ ", pickUpItemsToRemoveIDs="
				+ NamedObject.getIDList(pickUpItemsToRemove)
				+ ", enablingRemoveItem=" + enablingRemoveItem + " "
				+ super.toString() + "}";
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder(super.getActionDescription());
		if (changeLocation) {
			builder.append(" Setting location to '")
					.append(newLocation != null ? newLocation.getName()
							: "null").append("'.");
		}
		if (newTakeSuccessfulText != null) {
			builder.append(" Setting take successful text to '")
					.append(newTakeSuccessfulText).append("'.");
		}
		if (newTakeForbiddenText != null) {
			builder.append(" Setting take forbidden text to '")
					.append(newTakeForbiddenText).append("'.");
		}
		if (enablingTakeable != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enablingTakeable.description)
					.append(" taking.");
		}
		if (enablingRemoveItem != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enablingRemoveItem.description)
					.append(" removing the item when taken.");
		}
		if (!pickUpItemsToAdd.isEmpty()) {
			builder.append(" Adding pick up items: ");
			for (InventoryItem item : pickUpItemsToAdd) {
				builder.append(item.getName()).append(", ");
			}
			builder.setLength(builder.length() - 2);
			builder.append(".");
		}
		if (!pickUpItemsToRemove.isEmpty()) {
			builder.append(" Removing pick up items: ");
			for (InventoryItem item : pickUpItemsToRemove) {
				builder.append(item.getName()).append(", ");
			}
			builder.setLength(builder.length() - 2);
			builder.append(".");
		}
		return builder.toString();
	}

}
