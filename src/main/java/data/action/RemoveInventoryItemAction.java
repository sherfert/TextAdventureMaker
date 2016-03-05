package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Game;
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
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_REMOVEINVENTORYITEMACTION_ITEM", //
	foreignKeyDefinition = "FOREIGN KEY (ITEM_ID) REFERENCES NAMEDOBJECT (ID) ON DELETE CASCADE") )
	private InventoryItem item;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link RemoveInventoryItemAction#RemoveInventoryItemAction(Item)}
	 *             instead.
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
	 * @return the item
	 */
	public InventoryItem getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(InventoryItem item) {
		this.item = item;
	}

	@Override
	protected void doAction(Game game) {
		// Remove the item from the inventory
		game.getPlayer().removeInventoryItem(item);
	}

	@Override
	public String toString() {
		return "RemoveInventoryItemAction{" + "itemID=" + item.getId() + " "
				+ super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Removing  ").append(item.getName()).append(".");
		return builder.toString();
	}
}
