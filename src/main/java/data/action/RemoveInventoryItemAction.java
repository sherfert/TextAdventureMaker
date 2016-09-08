package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Game;
import data.InventoryItem;

/**
 * An action removing an {@link InventoryItem}.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class RemoveInventoryItemAction extends AbstractAction {

	/**
	 * The item to be removed.
	 */
	private InventoryItem item;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link RemoveInventoryItemAction#RemoveInventoryItemAction(String, InventoryItem)}
	 *             instead.
	 */
	@Deprecated
	public RemoveInventoryItemAction() {
	}

	/**
	 * @param name
	 *            the name
	 * @param item
	 *            the item to be removed
	 */
	public RemoveInventoryItemAction(String name, InventoryItem item) {
		super(name);
		this.item = item;
	}

	/**
	 * @return the item
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_REMOVEINVENTORYITEMACTION_ITEM", //
	foreignKeyDefinition = "FOREIGN KEY (ITEM_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public InventoryItem getItem() {
		return item;
	}

	/**
	 * @param item
	 *            the item to set
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
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Removing  ").append(item.getName()).append(".");
		return builder.toString();
	}
}
