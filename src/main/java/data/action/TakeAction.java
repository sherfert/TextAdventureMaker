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
 * @author Satia
 * 
 */
@Entity
public class TakeAction extends AbstractAction {

	public TakeAction() {
		pickUpItems = new ArrayList<InventoryItem>();
		deleteItem = true;
	}

	public TakeAction(Item item) {
		this();
		this.item = item;
	}

	@OneToOne(mappedBy = "primaryAction", cascade = CascadeType.PERSIST)
	private Item item;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<InventoryItem> pickUpItems;

	private boolean deleteItem;

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
	 * @param deleteItem
	 *            the deleteItem to set
	 */
	public void setDeleteItem(boolean deleteItem) {
		this.deleteItem = deleteItem;
	}

	public void addPickUpItem(InventoryItem item) {
		this.pickUpItems.add(item);
	}

	public void removePickUpItem(InventoryItem item) {
		this.pickUpItems.remove(item);
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			if (deleteItem) {
				this.item.getLocation().removeItem(this.item);
				ItemManager.removeItem(this.item);
			}
			for (InventoryItem item : pickUpItems) {
				PlayerManager.getPlayer().addInventoryItem(item);
			}
			Main.updateChanges();
		} else {
			// TODO
		}
	}
}