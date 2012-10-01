/**
 * 
 */
package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import persistence.Main;
import persistence.PlayerManager;

import data.Way;

/**
 * @author Satia
 * 
 */
@Entity
public class MoveAction extends AbstractAction {

	public MoveAction() {
	}

	public MoveAction(Way way) {
		this.way = way;
	}

	@OneToOne(mappedBy = "primaryAction", cascade = CascadeType.ALL)
	private Way way;

	/**
	 * @return the way
	 */
	public Way getWay() {
		return way;
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			PlayerManager.getPlayer().setLocation(way.getDestination());
			Main.updateChanges();
		} else {
			// TODO
		}
	}
}