package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.InventoryItem;
import data.Item;

@Entity
public class ChangeInventoryItemItemUsageAction extends
		ChangeInventoryItemHasLocationUsageAction {

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeInventoryItemItemUsageChange(InventoryItem, Item)}
	 *             instead.
	 */
	@Deprecated
	public ChangeInventoryItemItemUsageAction() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the item
	 */
	public ChangeInventoryItemItemUsageAction(InventoryItem inventoryItem,
			Item object) {
		super(inventoryItem, object);
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the item
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeInventoryItemItemUsageAction(InventoryItem inventoryItem,
			Item object, boolean enabled) {
		super(inventoryItem, object, enabled);
	}

	/**
	 * @return the item
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	public Item getItem() {
		return (Item) object;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(Item item) {
		object = item;
	}

}
