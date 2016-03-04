package data.action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Game;
import data.Location;
import data.Way;

/**
 * An action changing the location of the player.
 * 
 * @author Satia
 */
@Entity
public class MoveAction extends AbstractAction {

	/**
	 * The target where to move.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey=@ForeignKey(name="FK_MOVEACTION_TARGET", foreignKeyDefinition="FOREIGN KEY (TARGET_ID) REFERENCES NAMEDOBJECT (ID) ON DELETE CASCADE"))
	private Location target;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link MoveAction#MoveAction(Way)} instead.
	 */
	@Deprecated
	public MoveAction() {
	}

	/**
	 * @param target
	 *            the target where to move.
	 */
	public MoveAction(Location target) {
		this.target = target;
	}

	/**
	 * @param target
	 *            the target where to move.
	 * @param enabled
	 *            if the action should be enabled
	 */
	public MoveAction(Location target, boolean enabled) {
		super(enabled);
		this.target = target;
	}

	/**
	 * @return the target
	 */
	public Location getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Location target) {
		this.target = target;
	}

	@Override
	protected void doAction(Game game) {
		game.getPlayer().setLocation(target);
	}

	@Override
	public String toString() {
		return "MoveAction{" + "targetID=" + target.getId() + " "
				+ super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Moving to ").append(target.getName()).append(".");
		return builder.toString();
	}
}
