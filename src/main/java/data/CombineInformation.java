package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.interfaces.HasId;

/**
 * Attributes of an {@link InventoryItem} that can be used with an
 * {@link InventoryItem}.
 * 
 * @author Satia
 */
@Entity
class CombineInformation implements HasId {

	/**
	 * The action adding the new inventory items.
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(nullable = false)
	final AddInventoryItemsAction addInventoryItemsAction;

	/**
	 * All actions triggered when the two {@link InventoryItem}s are combined.
	 * They are triggered regardless of the enabled status.
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "CII_ACWA", foreignKey = @ForeignKey(name = "FK_CII_additionalCombineWithActions_S", //
	foreignKeyDefinition = "FOREIGN KEY (CombineInformation_ID) REFERENCES CombineInformation (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_CII_additionalCombineWithActions_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalCombineWithActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	@OrderColumn
	List<AbstractAction> additionalCombineWithActions;

	/**
	 * The text displayed when combining is disabled but the user tries to
	 * trigger the connection or suggests the default text to be displayed if
	 * {@code null}.
	 */
	@Column(nullable = true)
	String combineWithForbiddenText;

	/**
	 * The text displayed when combining is enabled and the user tries to
	 * trigger the connection or suggests the default text to be displayed if
	 * {@code null}.
	 */
	@Column(nullable = true)
	String combineWithSuccessfulText;

	/**
	 * Whether combining of the two {@link InventoryItem}s should be enabled.
	 */
	@Column(nullable = false)
	boolean enabled;

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * Whether the two partners should be removed when combined successfully.
	 */
	@Column(nullable = false)
	boolean removeCombinables;

	/**
	 * Disabled by default and no forbidden/successful texts. Removing items
	 * also disabled by default.
	 */
	public CombineInformation() {
		additionalCombineWithActions = new ArrayList<>();
		addInventoryItemsAction = new AddInventoryItemsAction("");
		addInventoryItemsAction.setHidden(true);
		enabled = false;
		removeCombinables = false;
	}

	@Override
	public String toString() {
		return "CombineInformation{" + "addInventoryItemsActionID=" + addInventoryItemsAction.getId()
				+ ", additionalCombineWithActionsIDs=" + NamedObject.getIDList(additionalCombineWithActions)
				+ ", combineWithForbiddenText=" + combineWithForbiddenText + ", combineWithSuccessfulText="
				+ combineWithSuccessfulText + ", enabled=" + enabled + ", id=" + id + ", removeCombinables="
				+ removeCombinables + '}';
	}

	@Override
	public int getId() {
		return id;
	}
}