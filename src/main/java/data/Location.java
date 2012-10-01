package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Location extends NamedObject {
	
	public Location() {
		// FIXME is this ok?
		waysIn = new ArrayList<Way>();
		waysOut = new ArrayList<Way>();
		items = new ArrayList<Item>();
	}
	
	@OneToMany(mappedBy = "destiny")
	private List<Way> waysIn;

	@OneToMany(mappedBy = "origin")
	private List<Way> waysOut;

	@OneToMany(mappedBy = "location")
	private List<Item> items;

	public void addWayIn(Way wayIn) {
		this.waysIn.add(wayIn);
		wayIn.destiny = this;
	}

	public void addWayOut(Way wayOut) {
		this.waysOut.add(wayOut);
		wayOut.origin = this;
	}

	public void addItem(Item item) {
		this.items.add(item);
		item.location = this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location [waysIn=" + waysIn + ", waysOut=" + waysOut
				+ ", items=" + items + ", toString()=" + super.toString() + "]";
	}
}