package data.action;

import data.Game;
import data.InventoryItem;
import data.NamedDescribedObject;
import data.interfaces.PassivelyUsable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * This action modifies the usage behavior of an {@link InventoryItem} with a
 * {@link PassivelyUsable}.
 * 
 * @author Satia
 * 
 */
@Entity
@Access(AccessType.PROPERTY)
public class ChangeUseWithInformationAction extends AbstractAction {

	/**
	 * The inventory item.
	 */
	private InventoryItem inventoryItem;

	/**
	 * The thing to use with.
	 */
	private PassivelyUsable object;

	/**
	 * The new useWithForbiddenText. If {@code null}, the old will not be
	 * changed.
	 */
	private String newUseWithForbiddenText;

	/**
	 * The new useWithSuccessfulText. If {@code null}, the old will not be
	 * changed.
	 */
	private String newUseWithSuccessfulText;

	/**
	 * Enabling or disabling if this combination is actually usable.
	 */
	private Enabling enabling;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeUseWithInformationAction(String, InventoryItem, PassivelyUsable)}
	 *             instead.
	 */
	@Deprecated
	public ChangeUseWithInformationAction() {
		init();
	}

	/**
	 * @param name
	 *            the name
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the thing to use with
	 */
	public ChangeUseWithInformationAction(String name, InventoryItem inventoryItem, PassivelyUsable object) {
		super(name);
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
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGEINVITEMUSAGEACTION_INVENTORYITEM", //
	foreignKeyDefinition = "FOREIGN KEY (INVENTORYITEM_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	/**
	 * @param inventoryItem
	 *            the inventoryItem to set
	 */
	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	/**
	 * @return the newUseWithForbiddenText
	 */
	@Column(nullable = true)
	public String getNewUseWithForbiddenText() {
		return newUseWithForbiddenText;
	}

	/**
	 * @param newUseWithForbiddenText
	 *            the newUseWithForbiddenText to set
	 */
	public void setNewUseWithForbiddenText(String newUseWithForbiddenText) {
		this.newUseWithForbiddenText = newUseWithForbiddenText;
	}

	/**
	 * @return the newUseWithSuccessfulText
	 */
	@Column(nullable = true)
	public String getNewUseWithSuccessfulText() {
		return newUseWithSuccessfulText;
	}

	/**
	 * @param newUseWithSuccessfulText
	 *            the newUseWithSuccessfulText to set
	 */
	public void setNewUseWithSuccessfulText(String newUseWithSuccessfulText) {
		this.newUseWithSuccessfulText = newUseWithSuccessfulText;
	}

	/**
	 * @return the enabling
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Enabling getEnabling() {
		return enabling;
	}

	/**
	 * @param enabling
	 *            the enabling to set
	 */
	public void setEnabling(Enabling enabling) {
		this.enabling = enabling;
	}

	/**
	 * @return the object
	 */
	// Specify the common supertype of Person and Item: NamedDescribedObject
	@ManyToOne(cascade = CascadeType.PERSIST, targetEntity = NamedDescribedObject.class)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGEINVITEMUSAGEACTION_OBJECT", //
	foreignKeyDefinition = "FOREIGN KEY (OBJECT_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public PassivelyUsable getObject() {
		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(PassivelyUsable object) {
		this.object = object;
	}

	@Override
	protected void doAction(Game game) {
		if (newUseWithForbiddenText != null) {
			inventoryItem.setUseWithForbiddenText(object, newUseWithForbiddenText);
		}
		if (newUseWithSuccessfulText != null) {
			inventoryItem.setUseWithSuccessfulText(object, newUseWithSuccessfulText);
		}
		if (enabling == Enabling.ENABLE) {
			inventoryItem.setUsingEnabledWith(object, true);
		} else if (enabling == Enabling.DISABLE) {
			inventoryItem.setUsingEnabledWith(object, false);
		}
	}

	@Override
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing usage of ").append(inventoryItem.getName()).append(" with ").append(object.getName())
				.append(".");
		if (enabling != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enabling.description).append(" usage.");
		}
		if (newUseWithSuccessfulText != null) {
			builder.append(" Setting use with successful text to '").append(newUseWithSuccessfulText).append("'.");
		}
		if (newUseWithForbiddenText != null) {
			builder.append(" Setting use with forbidden text to '").append(newUseWithForbiddenText).append("'.");
		}
		return builder.toString();
	}
}
