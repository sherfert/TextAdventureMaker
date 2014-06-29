package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Item;
import data.Location;

/**
 * An action changing attributes of an {@link Item}.
 * 
 * It can also be used to ADD items to a location, if the former location was
 * {@code null} or to REMOVE items from a location, if the new location is
 * {@code null}.
 * 
 * TODO all the stuff that is embedded into addInventoryItemsAction and
 * removeAction must be changeable too!
 * 
 * @author Satia
 */
@Entity
public class ChangeItemAction extends ChangeUsableObjectAction {

	/**
	 * Only if this is {@code true}, the location will be changed. This is
	 * necessary, as {@code null} is a valid location and cannot be used to
	 * identify no changes to be applied.
	 */
	@Column(nullable = false)
	private boolean changeLocation;

	/**
	 * The new location of the item. Can be {@code null}, which means the Item
	 * will be removed. To apply these changes, {@link #changeLocation} has to
	 * be set to {@code true}.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = true)
	private Location newLocation;

	/**
	 * The new takeForbiddenText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newTakeForbiddenText;

	/**
	 * The new takeSuccessfulText. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newTakeSuccessfulText;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link ChangeItemAction#ChangeItemAction(Item)} instead.
	 */
	@Deprecated
	public ChangeItemAction() {
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeItemAction(Item object) {
		super(object);
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param newLocation
	 *            The new location of the item. Can be {@code null}, which means
	 *            the Item will be removed.
	 */
	public ChangeItemAction(Item object, Location newLocation) {
		super(object);
		setNewLocation(newLocation);
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeItemAction(Item object, boolean enabled) {
		super(object, enabled);
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param newLocation
	 *            The new location of the item. Can be {@code null}, which means
	 *            the Item will be removed.
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeItemAction(Item object, Location newLocation, boolean enabled) {
		super(object, enabled);
		setNewLocation(newLocation);
	}

	@Override
	public Item getObject() {
		return (Item) super.getObject();
	}

	/**
	 * @return the newLocation
	 */
	public Location getNewLocation() {
		return newLocation;
	}

	/**
	 * This also sets changeLocation to true. If you want to undo this,
	 * explicitly call {@code setChangeLocation(false)}.
	 * 
	 * @param newLocation
	 *            the newLocation to set
	 */
	public final void setNewLocation(Location newLocation) {
		this.newLocation = newLocation;
		this.changeLocation = true;
	}

	/**
	 * @return the changeLocation
	 */
	public boolean isChangeLocation() {
		return changeLocation;
	}

	/**
	 * @param changeLocation
	 *            the changeLocation to set
	 */
	public void setChangeLocation(boolean changeLocation) {
		this.changeLocation = changeLocation;
	}

	/**
	 * @return the newTakeForbiddenText
	 */
	public String getNewTakeForbiddenText() {
		return newTakeForbiddenText;
	}

	/**
	 * @param newTakeForbiddenText
	 *            the newTakeForbiddenText to set
	 */
	public void setNewTakeForbiddenText(String newTakeForbiddenText) {
		this.newTakeForbiddenText = newTakeForbiddenText;
	}

	/**
	 * @return the newTakeSuccessfulText
	 */
	public String getNewTakeSuccessfulText() {
		return newTakeSuccessfulText;
	}

	/**
	 * @param newTakeSuccessfulText
	 *            the newTakeSuccessfulText to set
	 */
	public void setNewTakeSuccessfulText(String newTakeSuccessfulText) {
		this.newTakeSuccessfulText = newTakeSuccessfulText;
	}

	@Override
	protected void doAction() {
		// Call the super method
		super.doAction();

		if (changeLocation) {
			getObject().setLocation(newLocation);
		}
		// Change fields
		if (newTakeForbiddenText != null) {
			getObject().setTakeForbiddenText(newTakeForbiddenText);
		}
		if (newTakeSuccessfulText != null) {
			getObject().setTakeSuccessfulText(newTakeSuccessfulText);
		}
	}

	@Override
	public String toString() {
		return "ChangeItemAction{" + "changeLocation=" + changeLocation
				+ ", newLocationID="
				+ (newLocation != null ? newLocation.getId() : "null")
				+ ", newTakeForbiddenText=" + newTakeForbiddenText
				+ ", newTakeSuccessfulText=" + newTakeSuccessfulText + " "
				+ super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder(super.getActionDescription());
		if (changeLocation) {
			builder.append(" Setting location to '")
					.append(newLocation != null ? newLocation.getName()
							: "null").append("'.");
		}
		if (newTakeSuccessfulText != null) {
			builder.append(" Setting take successful text to '")
					.append(newTakeSuccessfulText).append("'.");
		}
		if (newTakeForbiddenText != null) {
			builder.append(" Setting take forbidden text to '")
					.append(newTakeForbiddenText).append("'.");
		}
		return builder.toString();
	}

}
