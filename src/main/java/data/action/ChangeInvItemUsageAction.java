package data.action;

import data.Game;
import data.InventoryItem;
import data.NamedObject;
import data.interfaces.HasLocation;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * This action modifies the usage behavior of an {@link InventoryItem} with a
 * {@link HasLocation}.
 * 
 * TODO inverse cascading
 * 
 * @author Satia
 * 
 */
@Entity
public class ChangeInvItemUsageAction extends
		AbstractAction {

	/**
	 * The inventory item.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private InventoryItem inventoryItem;

	// Specify the common supertype
	/**
	 * The person or item.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST, targetEntity = NamedObject.class)
	@JoinColumn(nullable = false)
	private HasLocation object;

	/**
	 * The new useWithForbiddenText. If {@code null}, the old will not be
	 * changed.
	 */
	@Column(nullable = true)
	private String newUseWithForbiddenText;

	/**
	 * The new useWithSuccessfulText. If {@code null}, the old will not be
	 * changed.
	 */
	@Column(nullable = true)
	private String newUseWithSuccessfulText;

	/**
	 * Enabling or disabling if this combination is actually usable.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enabling enabling;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeInvItemUsageAction(InventoryItem, HasLocation)}
	 *             instead.
	 */
	@Deprecated
	public ChangeInvItemUsageAction() {
		init();
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the person or item
	 */
	public ChangeInvItemUsageAction(
			InventoryItem inventoryItem, HasLocation object) {
		init();
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
	public ChangeInvItemUsageAction(
			InventoryItem inventoryItem, HasLocation object, boolean enabled) {
		super(enabled);
		init();
		this.inventoryItem = inventoryItem;
		this.object = object;
	}
	
	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.enabling = Enabling.DO_NOT_CHANGE;
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

	/**
	 * @return the object
	 */
	public HasLocation getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(HasLocation object) {
		this.object = object;
	}

	@Override
	protected void doAction(Game game) {
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
		return "ChangeInvItemUsageAction{inventoryItemID="
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
