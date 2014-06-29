package data.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import persistence.PlayerManager;
import data.InventoryItem;
import data.NamedObject;

/**
 * None, one or multiple {@link InventoryItem}s may be added to the inventory.
 * 
 * @author Satia
 */
@Entity
public class AddInventoryItemsAction extends AbstractAction {

	/**
	 * All {@link InventoryItem}s that will be added to the inventory.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<InventoryItem> pickUpItems;

	/**
	 * A new {@link AddInventoryItemsAction}.
	 */
	public AddInventoryItemsAction() {
		init();
	}

	/**
	 * 
	 * @param enabled
	 *            if the action should be enabled
	 */
	public AddInventoryItemsAction(boolean enabled) {
		super(enabled);
		init();
	}

	/**
	 * Adds an {@link InventoryItem} to the items to be added to the inventory.
	 * 
	 * @param item
	 *            the item
	 */
	public void addPickUpItem(InventoryItem item) {
		this.pickUpItems.add(item);
	}

	/**
	 * @return the pickUpItems.
	 */
	public List<InventoryItem> getPickUpItems() {
		return pickUpItems;
	}

	/**
	 * @param pickUpItems
	 *            the pickUpItems to set
	 */
	public void setPickUpItems(List<InventoryItem> pickUpItems) {
		this.pickUpItems = pickUpItems;
	}

	/**
	 * Removes an {@link InventoryItem} from the items to be added to the
	 * inventory.
	 * 
	 * @param item
	 *            the item
	 */
	public void removePickUpItem(InventoryItem item) {
		this.pickUpItems.remove(item);
	}

	@Override
	protected void doAction() {
		for (InventoryItem item : pickUpItems) {
			PlayerManager.getPlayer().addInventoryItem(item);
		}
	}

	/**
	 * Initializes the fields
	 */
	private void init() {
		this.pickUpItems = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "AddInventoryItemsAction{" + "pickUpItemsIDs="
				+ NamedObject.getIDList(pickUpItems) + " " + super.toString()
				+ '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Picking up: ");
		for (InventoryItem item : pickUpItems) {
			builder.append(item.getName()).append(", ");
		}
		builder.setLength(builder.length() - 2);
		return builder.toString();
	}
}
