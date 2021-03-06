package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

import data.action.AbstractAction;
import data.action.AddInventoryItemsAction;
import data.action.ChangeItemAction;
import data.interfaces.HasLocation;
import data.interfaces.PassivelyUsable;
import data.interfaces.Takeable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Any item in the game world. These items are stored in locations but cannot be
 * in your inventory.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class Item extends UsableObject implements Takeable, HasLocation, PassivelyUsable {

	/**
	 * The {@link AddInventoryItemsAction} that adds item to your inventory when
	 * invoking a pick up command on this Item.
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 */
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(nullable = false)
	@Access(AccessType.FIELD)
	private AddInventoryItemsAction addInventoryItemsAction;

	/**
	 * All additional take actions.
	 */
	private List<AbstractAction> additionalTakeActions;

	/**
	 * All additional take commands.
	 */
	private List<String> additionalTakeCommands;

	/**
	 * The current location of the item. May be {@code null}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true, foreignKey = @ForeignKey(name = "FK_ITEM_LOCATION", //
	foreignKeyDefinition = "FOREIGN KEY (LOCATION_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE SET NULL") )
	@Access(AccessType.FIELD)
	private Location location;

	/**
	 * The {@link ChangeItemAction} which would set the location to {@code null}
	 * .
	 * 
	 * Note: This is NOT the Inverse connection of {@link ChangeItemAction#item}
	 * .
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 * 
	 * XXX This should be nullable = false, which is not possible.
	 */
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn
	@Access(AccessType.FIELD)
	private ChangeItemAction removeAction;

	/**
	 * A personalized error message displayed if taking this item was forbidden
	 * or suggests the default text to be displayed if {@code null}.
	 */
	private final StringProperty takeForbiddenText;

	/**
	 * A personalized error message displayed if taking this item was successful
	 * or suggests the default text to be displayed if {@code null}.
	 */
	private final StringProperty takeSuccessfulText;

	/**
	 * No-arg constructor for the database.
	 * 
	 * By default taking will be disabled, but removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @deprecated Use {@link Item#Item(String, String)} or
	 *             {@link Item#Item(Location, String, String)} instead.
	 */
	@Deprecated
	public Item() {
		takeSuccessfulText = new SimpleStringProperty();
		takeForbiddenText = new SimpleStringProperty();
		init();
	}

	/**
	 * By default taking will be disabled, but removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @param location
	 *            the location
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(Location location, String name, String description) {
		super(name, description);
		takeSuccessfulText = new SimpleStringProperty();
		takeForbiddenText = new SimpleStringProperty();
		init();
		setLocation(location);
	}

	/**
	 * By default taking will be disabled, but removeItem (when taking is
	 * enabled) is enabled.
	 * 
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	public Item(String name, String description) {
		super(name, description);
		takeSuccessfulText = new SimpleStringProperty();
		takeForbiddenText = new SimpleStringProperty();
		init();
	}

	@Override
	public void addAdditionalTakeAction(AbstractAction action) {
		additionalTakeActions.add(action);
	}

	@Override
	public void addAdditionalTakeCommand(String command) {
		additionalTakeCommands.add(command);
	}

	@Override
	public void addPickUpItem(InventoryItem item) {
		addInventoryItemsAction.addPickUpItem(item);
	}
	
	@Override
	@Transient
	public List<InventoryItem> getPickUpItems() {
		return addInventoryItemsAction.getPickUpItems();
	}

	@Override
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "ITEM_ATA", foreignKey = @ForeignKey(name = "FK_ITEM_ADDITIONALTAKEACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (Item_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_ITEM_ADDITIONALTAKEACTIONS_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalTakeActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	@OrderColumn
	public List<AbstractAction> getAdditionalTakeActions() {
		return additionalTakeActions;
	}

	@Override
	@ElementCollection
	public List<String> getAdditionalTakeCommands() {
		return additionalTakeCommands;
	}

	@Override
	@Transient
	public Location getLocation() {
		return location;
	}

	@Override
	@Column(nullable = true)
	public String getTakeForbiddenText() {
		return takeForbiddenText.get();
	}

	@Override
	@Column(nullable = true)
	public String getTakeSuccessfulText() {
		return takeSuccessfulText.get();
	}

	/**
	 * Initializes the fields.
	 */
	private final void init() {
		this.addInventoryItemsAction = new AddInventoryItemsAction("");
		this.addInventoryItemsAction.setHidden(true);
		this.addInventoryItemsAction.setEnabled(false);
		
		this.removeAction = new ChangeItemAction("", this);
		this.removeAction.setHidden(true);
		this.removeAction.setNewLocation(null);
		this.removeAction.setChangeLocation(true);
		
		this.additionalTakeActions = new ArrayList<>();
		this.additionalTakeCommands = new ArrayList<>();
		setRemoveItem(true);
	}

	@Override
	@Transient
	public boolean isRemoveItem() {
		return removeAction.getEnabled();
	}

	@Override
	@Transient
	public boolean isTakingEnabled() {
		return addInventoryItemsAction.getEnabled();
	}

	@Override
	public void removeAdditionalTakeAction(AbstractAction action) {
		additionalTakeActions.remove(action);
	}

	@Override
	public void removeAdditionalTakeCommand(String command) {
		additionalTakeCommands.remove(command);
	}

	@Override
	public void removePickUpItem(InventoryItem item) {
		addInventoryItemsAction.removePickUpItem(item);
	}

	@Override
	public void setAdditionalTakeActions(List<AbstractAction> additionalTakeActions) {
		this.additionalTakeActions = additionalTakeActions;
	}

	@Override
	public void setAdditionalTakeCommands(List<String> additionalTakeCommands) {
		this.additionalTakeCommands = additionalTakeCommands;
	}
	
	/**
	 * Called to remove an item from its location prior to deletion.
	 */
	@PreRemove
	private void removeFromLocation() {
		setLocation(null);
	}

	// final as called in constructor.
	@Override
	public final void setLocation(Location location) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Setting location of {0} to {1}",
				new Object[] { this, location });

		if (this.location != null) {
			this.location.removeItem(this);
		}
		if (location != null) {
			location.addItem(this);
		}
		this.location = location;
	}

	@Override
	public void setRemoveItem(boolean removeItem) {
		removeAction.setEnabled(removeItem);
	}

	@Override
	public void setTakeForbiddenText(String forbiddenText) {
		takeForbiddenText.set(forbiddenText);
	}

	@Override
	public void setTakeSuccessfulText(String successfulText) {
		takeSuccessfulText.set(successfulText);
	}

	@Override
	public void setTakingEnabled(boolean enabled) {
		addInventoryItemsAction.setEnabled(enabled);
	}

	@Override
	public void take(Game game) {
		if (isTakingEnabled()) {
			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Taking {0}", this);

			addInventoryItemsAction.triggerAction(game);
			removeAction.triggerAction(game);
		}
		for (AbstractAction abstractAction : additionalTakeActions) {
			abstractAction.triggerAction(game);
		}
	}

	/**
	 * @return the take forbidden text property
	 */
	public StringProperty takeForbiddenTextProperty() {
		return takeForbiddenText;
	}

	/**
	 * @return the take successful text property
	 */
	public StringProperty takeSuccessfulTextProperty() {
		return takeSuccessfulText;
	}
}
