package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.InventoryItem;
import data.Person;

@Entity
public class ChangeInvItemPersonUsageAction extends
		ChangeInvItemUsageAction {

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #ChangeInvItemPersonUsageAction(InventoryItem, Person)}
	 *             instead.
	 */
	@Deprecated
	public ChangeInvItemPersonUsageAction() {
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the item
	 */
	public ChangeInvItemPersonUsageAction(InventoryItem inventoryItem,
			Person object) {
		super(inventoryItem, object);
	}

	/**
	 * @param inventoryItem
	 *            the inventory item
	 * @param object
	 *            the item
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeInvItemPersonUsageAction(InventoryItem inventoryItem,
			Person object, boolean enabled) {
		super(inventoryItem, object, enabled);
	}

	/**
	 * @return the person
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	public Person getPerson() {
		return (Person) object;
	}

	/**
	 * @param person
	 *            the person to set
	 */
	public void setPerson(Person person) {
		object = person;
	}
}
