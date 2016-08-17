package data.action;

import java.util.ArrayList;
import java.util.List;

import data.Game;
import data.InventoryItem;
import data.NamedObject;
import data.interfaces.HasLocation;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * This action modifies the usage behavior of two {@link InventoryItem} with one
 * another.
 * 
 * @author Satia
 * 
 */
@Entity
@Access(AccessType.PROPERTY)
public class ChangeInvItemCombinationAction extends AbstractAction {

	/**
	 * The first inventory item.
	 */
	private InventoryItem inventoryItem1;

	/**
	 * The second inventory item.
	 */
	private InventoryItem inventoryItem2;

	/**
	 * The new combineWithForbiddenText. If {@code null}, the old will not be
	 * changed.
	 */
	private String newCombineWithForbiddenText;

	/**
	 * The new combineWithSuccessfulText. If {@code null}, the old will not be
	 * changed.
	 */
	private String newCombineWithSuccessfulText;

	/**
	 * All combinables to be added.
	 */
	private List<InventoryItem> combinablesToAdd;

	/**
	 * All combinables to be removed.
	 */
	private List<InventoryItem> combinablesToRemove;

	/**
	 * Enabling or disabling if this combination is actually usable.
	 */
	private Enabling enablingCombinable;

	/**
	 * Enabling or disabling if triggering this combination will remove the
	 * items from the inventory.
	 */
	private Enabling enablingRemoveCombinables;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeInvItemCombinationAction(String, InventoryItem, HasLocation)}
	 *             instead.
	 */
	@Deprecated
	public ChangeInvItemCombinationAction() {
		init();
	}

	/**
	 * @param name
	 *            the name
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the person or item
	 */
	public ChangeInvItemCombinationAction(String name, InventoryItem inventoryItem1, InventoryItem inventoryItem2) {
		super(name);
		init();
		this.inventoryItem1 = inventoryItem1;
		this.inventoryItem2 = inventoryItem2;
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.enablingCombinable = Enabling.DO_NOT_CHANGE;
		this.enablingRemoveCombinables = Enabling.DO_NOT_CHANGE;
		this.combinablesToAdd = new ArrayList<>();
		this.combinablesToRemove = new ArrayList<>();
	}

	/**
	 * @return the inventoryItem1
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGEINVITEMCOMBINATIONACTION_INVENTORYITEM1", //
	foreignKeyDefinition = "FOREIGN KEY (INVENTORYITEM1_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public InventoryItem getInventoryItem1() {
		return inventoryItem1;
	}

	/**
	 * @param inventoryItem1
	 *            the inventoryItem1 to set
	 */
	public void setInventoryItem1(InventoryItem inventoryItem1) {
		this.inventoryItem1 = inventoryItem1;
	}

	/**
	 * @return the inventoryItem2
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_CHANGEINVITEMCOMBINATIONACTION_INVENTORYITEM2", //
	foreignKeyDefinition = "FOREIGN KEY (INVENTORYITEM2_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public InventoryItem getInventoryItem2() {
		return inventoryItem2;
	}

	/**
	 * @param inventoryItem2
	 *            the inventoryItem2 to set
	 */
	public void setInventoryItem2(InventoryItem inventoryItem2) {
		this.inventoryItem2 = inventoryItem2;
	}

	/**
	 * @return the newCombineWithForbiddenText
	 */
	@Column(nullable = true)
	public String getNewCombineWithForbiddenText() {
		return newCombineWithForbiddenText;
	}

	/**
	 * @param newCombineWithForbiddenText
	 *            the newCombineWithForbiddenText to set
	 */
	public void setNewCombineWithForbiddenText(String newCombineWithForbiddenText) {
		this.newCombineWithForbiddenText = newCombineWithForbiddenText;
	}

	/**
	 * @return the newCombineWithSuccessfulText
	 */
	@Column(nullable = true)
	public String getNewCombineWithSuccessfulText() {
		return newCombineWithSuccessfulText;
	}

	/**
	 * @param newCombineWithSuccessfulText
	 *            the newCombineWithSuccessfulText to set
	 */
	public void setNewCombineWithSuccessfulText(String newCombineWithSuccessfulText) {
		this.newCombineWithSuccessfulText = newCombineWithSuccessfulText;
	}

	/**
	 * @return the combinablesToAdd
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "CIICA_CTA", foreignKey = @ForeignKey(name = "FK_CIICA_combinablesToAdd_S", //
	foreignKeyDefinition = "FOREIGN KEY (ChangeInvItemCombinationAction_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_CIICA_combinablesToAdd_D", //
	foreignKeyDefinition = "FOREIGN KEY (combinablesToAdd_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public List<InventoryItem> getCombinablesToAdd() {
		return combinablesToAdd;
	}

	/**
	 * @param combinablesToAdd
	 *            the combinablesToAdd to set
	 */
	public void setCombinablesToAdd(List<InventoryItem> combinablesToAdd) {
		this.combinablesToAdd = combinablesToAdd;
	}

	/**
	 * @return the combinablesToRemove
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "CIICA_CTR", foreignKey = @ForeignKey(name = "FK_CIICA_combinablesToRemove_S", //
	foreignKeyDefinition = "FOREIGN KEY (ChangeInvItemCombinationAction_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_CIICA_combinablesToRemove_D", //
	foreignKeyDefinition = "FOREIGN KEY (combinablesToRemove_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public List<InventoryItem> getCombinablesToRemove() {
		return combinablesToRemove;
	}

	/**
	 * @param combinablesToRemove
	 *            the combinablesToRemove to set
	 */
	public void setCombinablesToRemove(List<InventoryItem> combinablesToRemove) {
		this.combinablesToRemove = combinablesToRemove;
	}

	/**
	 * @return the enablingCombinable
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Enabling getEnablingCombinable() {
		return enablingCombinable;
	}

	/**
	 * @param enablingCombinable
	 *            the enablingCombinable to set
	 */
	public void setEnablingCombinable(Enabling enablingCombinable) {
		this.enablingCombinable = enablingCombinable;
	}

	/**
	 * @return the enablingRemoveCombinables
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Enabling getEnablingRemoveCombinables() {
		return enablingRemoveCombinables;
	}

	/**
	 * @param enablingRemoveCombinables
	 *            the enablingRemoveCombinables to set
	 */
	public void setEnablingRemoveCombinables(Enabling enablingRemoveCombinables) {
		this.enablingRemoveCombinables = enablingRemoveCombinables;
	}

	/**
	 * Adds a combinable to be added.
	 * 
	 * @param item
	 *            the item
	 */
	public void addCombinableToAdd(InventoryItem item) {
		combinablesToAdd.add(item);
	}

	/**
	 * Adds a combinable to be removed.
	 * 
	 * @param item
	 *            the item
	 */
	public void addCombinableToRemove(InventoryItem item) {
		combinablesToAdd.add(item);
	}

	/**
	 * Removes a combinable to be added.
	 * 
	 * @param item
	 *            the item
	 */
	public void removeCombinableToAdd(InventoryItem item) {
		combinablesToRemove.remove(item);
	}

	/**
	 * Removes a combinable to be removed.
	 * 
	 * @param item
	 *            the item
	 */
	public void removeCombinableToRemove(InventoryItem item) {
		combinablesToRemove.remove(item);
	}

	@Override
	protected void doAction(Game game) {
		if (newCombineWithSuccessfulText != null) {
			inventoryItem1.setCombineWithSuccessfulText(inventoryItem2, newCombineWithSuccessfulText);
		}
		if (newCombineWithForbiddenText != null) {
			inventoryItem1.setCombineWithForbiddenText(inventoryItem2, newCombineWithForbiddenText);
		}
		if (enablingCombinable == Enabling.ENABLE) {
			inventoryItem1.setCombiningEnabledWith(inventoryItem2, true);
		} else if (enablingCombinable == Enabling.DISABLE) {
			inventoryItem1.setCombiningEnabledWith(inventoryItem2, false);
		}
		if (enablingRemoveCombinables == Enabling.ENABLE) {
			inventoryItem1.setRemoveCombinablesWhenCombinedWith(inventoryItem2, true);
		} else if (enablingRemoveCombinables == Enabling.DISABLE) {
			inventoryItem1.setRemoveCombinablesWhenCombinedWith(inventoryItem2, false);
		}
		// Add and remove combinables
		for (InventoryItem item : combinablesToAdd) {
			inventoryItem1.addNewCombinableWhenCombinedWith(inventoryItem2, item);
		}
		for (InventoryItem item : combinablesToRemove) {
			inventoryItem1.removeNewCombinableWhenCombinedWith(inventoryItem2, item);
		}
	}

	@Override
	public String toString() {
		return "ChangeInvItemCombinationAction{inventoryItem1ID=" + inventoryItem1.getId() + ", inventoryItem2ID="
				+ inventoryItem2.getId() + ", newCombineWithForbiddenText=" + newCombineWithForbiddenText
				+ ", newCombineWithSuccessfulText=" + newCombineWithSuccessfulText + ", combinablesToAddIDs="
				+ NamedObject.getIDList(combinablesToAdd) + ", combinablesToRemoveIDs="
				+ NamedObject.getIDList(combinablesToRemove) + ", enablingCombinable=" + enablingCombinable
				+ ", enablingRemoveCombinables=" + enablingRemoveCombinables + " " + super.toString() + "}";
	}

	@Override
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing combining of ").append(inventoryItem1.getName()).append(" with ")
				.append(inventoryItem2.getName()).append(".");
		if (enablingCombinable != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enablingCombinable.description).append(" combining.");
		}
		if (enablingRemoveCombinables != Enabling.DO_NOT_CHANGE) {
			builder.append(" ").append(enablingCombinable.description)
					.append(" that items will be removed when combined.");
		}
		if (newCombineWithSuccessfulText != null) {
			builder.append(" Setting combine with successful text to '").append(newCombineWithSuccessfulText)
					.append("'.");
		}
		if (newCombineWithForbiddenText != null) {
			builder.append(" Setting combine with forbidden text to '").append(newCombineWithForbiddenText)
					.append("'.");
		}
		if (!combinablesToAdd.isEmpty()) {
			builder.append(" Adding combinable items: ");
			for (InventoryItem item : combinablesToAdd) {
				builder.append(item.getName()).append(", ");
			}
			builder.setLength(builder.length() - 2);
			builder.append(".");
		}
		if (!combinablesToRemove.isEmpty()) {
			builder.append(" Removing combinable items: ");
			for (InventoryItem item : combinablesToRemove) {
				builder.append(item.getName()).append(", ");
			}
			builder.setLength(builder.length() - 2);
			builder.append(".");
		}
		return builder.toString();
	}
}
