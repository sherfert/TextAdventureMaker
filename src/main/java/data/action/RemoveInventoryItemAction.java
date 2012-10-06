package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import persistence.Main;
import persistence.PlayerManager;
import data.InventoryItem;
import data.Item;

/**
 * An action removing an {@link InventoryItem}.
 * 
 * @author Satia
 */
@Entity
public class RemoveInventoryItemAction extends AbstractAction {

	/**
	 * The item to be removed.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private InventoryItem item;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link RemoveInventoryItemAction#RemoveInventoryItemAction(Item)} instead.
	 */
	@Deprecated
	public RemoveInventoryItemAction() {
	}

	/**
	 * @param item
	 *            the item to be removed
	 */
	public RemoveInventoryItemAction(InventoryItem item) {
		this.item = item;
	}

	/**
	 * @param item
	 *            the item to be removed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public RemoveInventoryItemAction(InventoryItem item, boolean enabled) {
		super(enabled);
		this.item = item;
	}

	/**
	 * @param item
	 *            the item to be removed
	 * @param enabled
	 *            if the action should be enabled
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public RemoveInventoryItemAction(InventoryItem item, boolean enabled, String forbiddenText,
			String successfulText) {
		super(enabled, forbiddenText, successfulText);
		this.item = item;
	}

	/**
	 * @param item
	 *            the item to be removed
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public RemoveInventoryItemAction(InventoryItem item, String forbiddenText,
			String successfulText) {
		super(forbiddenText, successfulText);
		this.item = item;
	}

	/**
	 * @return the item
	 */
	public InventoryItem getItem() {
		return item;
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			// Remove the item from the inventory
			PlayerManager.getPlayer().removeInventoryItem(item);
		}
		Main.updateChanges();
	}
}