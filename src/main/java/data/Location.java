package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Location extends NamedObject {

	public Location() {
		waysOut = new ArrayList<Way>();
		items = new ArrayList<Item>();
	}

	@OneToMany(mappedBy = "origin", cascade = CascadeType.ALL)
	private List<Way> waysOut;

	@OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST)
	private List<Item> items;

	public void addWayOut(Way wayOut) {
		this.waysOut.add(wayOut);
		wayOut.origin = this;
	}

	public void removeWayOut(Way wayOut) {
		this.waysOut.remove(wayOut);
		wayOut.origin = null;
	}

	public void addItem(Item item) {
		this.items.add(item);
		item.location = this;
	}

	public void removeItem(Item item) {
		this.items.remove(item);
		item.location = null;
	}

	/**
	 * @return the waysOut
	 */
	public List<Way> getWaysOut() {
		return waysOut;
	}

	/**
	 * @return the items
	 */
	public List<Item> getItems() {
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location [waysOut=" + waysOut
				+ ", items=" + items + ", toString()=" + super.toString() + "]";
	}
}