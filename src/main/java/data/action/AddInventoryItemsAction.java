package data.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import persistence.Main;
import persistence.PlayerManager;
import data.InventoryItem;

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
	 * @param enabled
	 *            if the action should be enabled
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public AddInventoryItemsAction(boolean enabled, String forbiddenText,
			String successfulText) {
		super(enabled, forbiddenText, successfulText);
		init();
	}

	/**
	 * 
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public AddInventoryItemsAction(String forbiddenText, String successfulText) {
		super(forbiddenText, successfulText);
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
	public void triggerAction() {
		if (enabled) {
			for (InventoryItem item : pickUpItems) {
				PlayerManager.getPlayer().addInventoryItem(item);
			}
		}
		Main.updateChanges();
	}

	/**
	 * Initializes the fields
	 */
	private void init() {
		this.pickUpItems = new ArrayList<InventoryItem>();
	}
}