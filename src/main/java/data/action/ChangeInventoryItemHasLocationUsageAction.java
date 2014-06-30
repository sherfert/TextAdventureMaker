package data.action;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import data.InventoryItem;
import data.interfaces.HasLocation;

/**
 * This action modifies the usage behavior of an {@link InventoryItem} with a
 * {@link HasLocation}.
 * 
 * TODO Test if @Column is not @Entity supeclass is supported.
 * 
 * @author Satia
 * 
 */
public abstract class ChangeInventoryItemHasLocationUsageAction extends
		AbstractAction {

	/**
	 * The inventory item.
	 */
	protected InventoryItem inventoryItem;

	/**
	 * The person or item.
	 */
	protected HasLocation object;

	/**
	 * The new useWithForbiddenText. If {@code null}, the old will not be
	 * changed.
	 */
	@Column(nullable = true)
	protected String newUseWithForbiddenText;

	/**
	 * The new useWithSuccessfulText. If {@code null}, the old will not be
	 * changed.
	 */
	@Column(nullable = true)
	protected String newUseWithSuccessfulText;

	/**
	 * Enabling or disabling if this combination is actually usable.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	protected Enabling enabling;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeInventoryItemHasLocationUsageAction(InventoryItem, HasLocation)}
	 *             instead.
	 */
	@Deprecated
	public ChangeInventoryItemHasLocationUsageAction() {
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the person or item
	 */
	public ChangeInventoryItemHasLocationUsageAction(
			InventoryItem inventoryItem, HasLocation object) {
		this.inventoryItem = inventoryItem;
		this.object = object;
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the person or item
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeInventoryItemHasLocationUsageAction(
			InventoryItem inventoryItem, HasLocation object, boolean enabled) {
		super(enabled);
		this.inventoryItem = inventoryItem;
		this.object = object;
	}

	/**
	 * @return the inventoryItem
	 */
	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	/**
	 * @param inventoryItem the inventoryItem to set
	 */
	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	/**
	 * @return the newUseWithForbiddenText
	 */
	public String getNewUseWithForbiddenText() {
		return newUseWithForbiddenText;
	}

	/**
	 * @param newUseWithForbiddenText the newUseWithForbiddenText to set
	 */
	public void setNewUseWithForbiddenText(String newUseWithForbiddenText) {
		this.newUseWithForbiddenText = newUseWithForbiddenText;
	}

	/**
	 * @return the newUseWithSuccessfulText
	 */
	public String getNewUseWithSuccessfulText() {
		return newUseWithSuccessfulText;
	}

	/**
	 * @param newUseWithSuccessfulText the newUseWithSuccessfulText to set
	 */
	public void setNewUseWithSuccessfulText(String newUseWithSuccessfulText) {
		this.newUseWithSuccessfulText = newUseWithSuccessfulText;
	}

	/**
	 * @return the enabling
	 */
	public Enabling getEnabling() {
		return enabling;
	}

	/**
	 * @param enabling the enabling to set
	 */
	public void setEnabling(Enabling enabling) {
		this.enabling = enabling;
	}

	@Override
	protected void doAction() {
		if (newUseWithForbiddenText != null) {
			inventoryItem.setUseWithForbiddenText(object,
					newUseWithForbiddenText);
		}
		if (newUseWithSuccessfulText != null) {
			inventoryItem.setUseWithSuccessfulText(object,
					newUseWithSuccessfulText);
		}
		if (enabling == Enabling.ENABLE) {
			inventoryItem.setUsingEnabledWith(object, true);
		} else if (enabling == Enabling.DISABLE) {
			inventoryItem.setUsingEnabledWith(object, false);
		}
	}

	@Override
	public String toString() {
		return "ChangeInventoryItemHasLocationUsageAction{inventoryItemID="
				+ inventoryItem.getId() + ", objectID=" + object.getId()
				+ ", newUseWithForbiddenText=" + newUseWithForbiddenText
				+ ", newUseWithSuccessfulText=" + newUseWithSuccessfulText
				+ ", enabling=" + enabling + " " + super.toString() + "]";
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing usage of ").append(inventoryItem.getName())
				.append(" with ").append(object.getName()).append(".");
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" usage.");
		}
		if (newUseWithSuccessfulText != null) {
			builder.append(" Setting use with successful text to '")
					.append(newUseWithSuccessfulText).append("'.");
		}
		if (newUseWithForbiddenText != null) {
			builder.append(" Setting use with forbidden text to '")
					.append(newUseWithForbiddenText).append("'.");
		}
		return builder.toString();
	}
}
