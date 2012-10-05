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
	@OneToOne(mappedBy = "moveAction", cascade = CascadeType.ALL)
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
	 * @param way
	 *            the way where the player should move
	 */
	public MoveAction(Way way) {
		this.way = way;
	}

	/**
	 * @param way
	 *            the way where the player should move
	 * @param enabled
	 *            if the action should be enabled
	 */
	public MoveAction(Way way, boolean enabled) {
		super(enabled);
		this.way = way;
	}

	/**
	 * @param way
	 *            the way where the player should move
	 * @param enabled
	 *            if the action should be enabled
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public MoveAction(Way way, boolean enabled, String forbiddenText,
			String successfulText) {
		super(enabled, forbiddenText, successfulText);
		this.way = way;
	}

	/**
	 * @param way
	 *            the way where the player should move
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public MoveAction(Way way, String forbiddenText, String successfulText) {
		super(forbiddenText, successfulText);
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