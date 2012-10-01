package data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.action.TakeAction;

@Entity
public class Item extends HasActions<TakeAction> {
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	Location location;

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
	
	public void addTakeAction() {
		this.primaryAction = new TakeAction(this);
	}
	
	public void removeTakeAction() {
		// TODO del from db
		this.primaryAction = null;
	}
	
	public boolean isTakable() {
		// TODO
		return false;
	}
}