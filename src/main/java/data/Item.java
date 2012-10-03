package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.action.TakeAction;

@Entity
public class Item extends HasActions<TakeAction> {
	
	public Item() {
		this.primaryAction = new TakeAction(this);
	}
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	Location location;

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
}