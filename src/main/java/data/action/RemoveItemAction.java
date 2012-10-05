package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import persistence.NamedObjectManager;
import data.Item;
import data.NamedObject;

/**
 * An action removing an {@link NamedObject}.
 * 
 * @author Satia
 */
@Entity
public class RemoveItemAction extends AbstractAction {

	/**
	 * The item to be removed.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Item item;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link RemoveItemAction#TakeAction(Item)} instead.
	 */
	@Deprecated
	public RemoveItemAction() {
	}

	/**
	 * @param item
	 *            the item to be removed
	 */
	public RemoveItemAction(Item item) {
		this.item = item;
	}

	/**
	 * @param item
	 *            the item to be removed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public RemoveItemAction(Item item, boolean enabled) {
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
	public RemoveItemAction(Item item, boolean enabled,
			String forbiddenText, String successfulText) {
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
	public RemoveItemAction(Item item, String forbiddenText,
			String successfulText) {
		super(forbiddenText, successfulText);
		this.item = item;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			item.getLocation().removeItem(item);
			NamedObjectManager.removeObject(item);
		}
	}
}