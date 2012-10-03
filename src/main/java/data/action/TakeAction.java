/**
 * 
 */
package data.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import persistence.ItemManager;
import persistence.Main;
import persistence.PlayerManager;

import data.InventoryItem;
import data.Item;

/**
 * An action picking up an {@link Item}. The item may or may not disappear and
 * none, one or multiple {@link InventoryItem}s may be added to the inventory.
 * 
 * @author Satia
 */
@Entity
public class TakeAction extends AbstractAction {

	/**
	 * If the item should disappear when picked up. {@code true} by default.
	 */
	private boolean deleteItem;

	/**
	 * The item to be taked.
	 */
	@OneToOne(mappedBy = "primaryAction", cascade = CascadeType.PERSIST)
	private Item item;

	/**
	 * All {@link InventoryItem}s that will be added to the inventory if the
	 * item is picked up.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<InventoryItem> pickUpItems;

	/**
	 * No-arg constructor for the database. Use
	 * {@link TakeAction#TakeAction(Item)} instead.
	 */
	@Deprecated
	public TakeAction() {
		pickUpItems = new ArrayList<InventoryItem>();
		deleteItem = true;
	}

	/**
	 * @param item
	 *            the item to be taked
	 */
	public TakeAction(Item item) {
		this();
		this.item = item;
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
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @return the deleteItem
	 */
	public boolean isDeleteItem() {
		return deleteItem;
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

	/**
	 * @param deleteItem
	 *            the deleteItem to set
	 */
	public void setDeleteItem(boolean deleteItem) {
		this.deleteItem = deleteItem;
	}

	@Override
	public void triggerAction() {
		// FIXME enabled check here ??
		if (enabled) {
			if (deleteItem) {
				this.item.getLocation().removeItem(this.item);
				ItemManager.removeItem(this.item);
			}
			for (InventoryItem item : pickUpItems) {
				PlayerManager.getPlayer().addInventoryItem(item);
			}
			Main.updateChanges();
		}
	}
}