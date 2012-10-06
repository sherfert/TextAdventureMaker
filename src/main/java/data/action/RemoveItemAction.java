package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import persistence.Main;
import data.Item;

/**
 * An action removing an {@link Item}.
 * 
 * @author Satia
 */
@Entity
public class RemoveItemAction extends AbstractAction {

	/**
	 * The item to be removed.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Item item;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link RemoveItemAction#RemoveItemAction(Item)} instead.
	 */
	@Deprecated
	public RemoveItemAction() {
	}

	/**
	 * Note: The item's {@link RemoveItemAction} will be overwritten. You can
	 * also just get and modify the current by
	 * {@link Item#getRemoveItemAction()}.
	 * 
	 * @param item
	 *            the item to be removed
	 */
	public RemoveItemAction(Item item) {
		setItem(item);
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Sets the item and sets {@code this} as the item's
	 * {@link RemoveItemAction}.
	 * 
	 * @param item
	 *            the item
	 */
	public void setItem(Item item) {
		this.item = item;
		if (item.getRemoveItemAction() != this) {
			item.setRemoveItemAction(this);
		}
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			// Only if this item is actually in a location
			if (item.getLocation() != null) {
				item.getLocation().removeItem(item);
			}
		}
		Main.updateChanges();
	}
}