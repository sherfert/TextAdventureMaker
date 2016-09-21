package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import data.action.AbstractAction;
import data.interfaces.HasId;
import data.interfaces.PassivelyUsable;

/**
 * Attributes of an {@link PassivelyUsable} that can be used with an
 * {@link InventoryItem}.
 * 
 * @author Satia
 */
@Entity
class UseWithInformation implements HasId {

	/**
	 * All actions triggered when the {@link InventoryItem} is used with the
	 * mapped {@link PassivelyUsable}. They are triggered regardless of the
	 * enabled status.
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "UHL_AUWA", foreignKey = @ForeignKey(name = "FK_UHL_additionalUseWithActions_S", //
	foreignKeyDefinition = "FOREIGN KEY (UseWithInformation_ID) REFERENCES UseWithInformation (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_UHL_additionalUseWithActions_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalUseWithActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	final List<AbstractAction> additionalUseWithActions;

	/**
	 * All additional useWith commands.
	 */
	@ElementCollection
	List<String> additionalUseWithCommands;

	/**
	 * Whether using of this {@link InventoryItem} with the mapped {@link Item}
	 * should be enabled.
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
	 * The text displayed when using is disabled but the user tries to trigger
	 * the connection or suggests the default text to be displayed if
	 * {@code null}.
	 */
	@Column(nullable = true)
	String useWithForbiddenText;

	/**
	 * The text displayed when using is enabled and the user tries to trigger
	 * the connection or suggests the default text to be displayed if
	 * {@code null}.
	 */
	@Column(nullable = true)
	String useWithSuccessfulText;

	/**
	 * Disabled by default and no forbidden/successful texts.
	 */
	public UseWithInformation() {
		additionalUseWithActions = new ArrayList<>();
		additionalUseWithCommands = new ArrayList<>();
		enabled = false;
	}

	@Override
	public String toString() {
		return "UseWithInformation{" + "additionalUseWithActionsIDs=" + NamedObject.getIDList(additionalUseWithActions)
				+ ", enabled=" + enabled + ", id=" + id + ", useWithForbiddenText=" + useWithForbiddenText
				+ ", useWithSuccessfulText=" + useWithSuccessfulText + '}';
	}

	@Override
	public int getId() {
		return id;
	}
}