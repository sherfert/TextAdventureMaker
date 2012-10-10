package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private Way way;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link MoveAction#MoveAction(Way)} instead.
	 */
	@Deprecated
	public MoveAction() {
	}

	/**
	 * Note: The way {@link MoveAction} will be overwritten. You can also just
	 * get and modify the current by {@link Way#getMoveAction()}.
	 * 
	 * @param way
	 *            the way where the player should move
	 */
	public MoveAction(Way way) {
		setWay(way);
	}

	/**
	 * @return the way
	 */
	public Way getWay() {
		return way;
	}

	/**
	 * Sets the way and sets {@code this} as the way's {@link MoveAction}.
	 * 
	 * @param way
	 *            the way to set
	 */
	public void setWay(Way way) {
		this.way = way;
		if (way.getMoveAction() != this) {
			way.setMoveAction(this);
		}
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			PlayerManager.getPlayer().setLocation(way.getDestination());
		}
		Main.updateChanges();
	}
}