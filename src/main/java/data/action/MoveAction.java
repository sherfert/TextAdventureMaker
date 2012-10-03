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
 * An action changing the location of the player.
 * 
 * @author Satia
 */
@Entity
public class MoveAction extends AbstractAction {

	/**
	 * The way where the player should move.
	 */
	@OneToOne(mappedBy = "primaryAction", cascade = CascadeType.ALL)
	private Way way;

	/**
	 * No-arg constructor for the database. Use
	 * {@link MoveAction#MoveAction(Way)} instead.
	 */
	@Deprecated
	public MoveAction() {
	}

	/**
	 * @param way
	 *            the way where the player should move
	 */
	public MoveAction(Way way) {
		this.way = way;
	}

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
		}
	}
}