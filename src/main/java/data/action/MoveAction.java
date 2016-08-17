package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
@Access(AccessType.PROPERTY)
public class MoveAction extends AbstractAction {

	/**
	 * The target where to move.
	 */
	private Location target;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link MoveAction#MoveAction(String, Way)} instead.
	 */
	@Deprecated
	public MoveAction() {
	}

	/**
	 * @param name
	 *            the name
	 * @param target
	 *            the target where to move.
	 */
	public MoveAction(String name, Location target) {
		super(name);
		this.target = target;
	}

	/**
	 * @return the target
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_MOVEACTION_TARGET", //
	foreignKeyDefinition = "FOREIGN KEY (TARGET_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public Location getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
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
		return "MoveAction{" + "targetID=" + target.getId() + " " + super.toString() + '}';
	}

	@Override
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Moving to ").append(target.getName()).append(".");
		return builder.toString();
	}
}
