package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * A game that can be played. Contains all configuration and (default) texts.
 * There should be only one game per database.
 * 
 * @author Satia
 */
@Entity
public class Game {
	/**
	 * All commands that make the game exit.
	 */
	@ElementCollection
	private List<String> exitCommands;

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * All commands that let the player look into his inventory.
	 */
	@ElementCollection
	private List<String> inventoryCommands;

	/**
	 * The text being displayed, when the player looks into his empty inventory.
	 */
	private String inventoryEmptyText;

	/**
	 * All move commands. Must contain at least one word and exactly one
	 * parameter for the target: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> moveCommands;

	/**
	 * The text being displayed, when any entered text is not recognized as a
	 * valid command.
	 */
	private String noCommandText;

	/**
	 * The text being displayed, when the player tries to use, take, etc. a
	 * non-existing item. Valid placeholders: {@literal <item>}
	 */
	private String noSuchItemText;

	/**
	 * The default text, when the player tries to take a non-takeable item. May
	 * be overwritten for each individual item. Valid placeholders:
	 * {@literal <item>}
	 */
	private String notTakeableText;

	/**
	 * The starting location of the game.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location startLocation;

	/**
	 * The text being displayed, when the game starts.
	 */
	private String startText;

	/**
	 * All take commands. Must contain at least one word and exactly one
	 * parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> takeCommands;

	/**
	 * The default text, when the player takes an item. May be overwritten for
	 * each individual item. Valid placeholders: {@literal <item>}
	 */
	private String takenText;

	/**
	 * Constructs a new game object.
	 */
	public Game() {
		exitCommands = new ArrayList<String>();
		inventoryCommands = new ArrayList<String>();
		moveCommands = new ArrayList<String>();
		takeCommands = new ArrayList<String>();
	}

	/**
	 * Adds an exit command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addExitCommand(String cmd) {
		this.exitCommands.add(cmd);
	}

	/**
	 * Adds an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addInventoryCommand(String cmd) {
		this.inventoryCommands.add(cmd);
	}

	/**
	 * Adds a move command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addMoveCommand(String cmd) {
		this.moveCommands.add(cmd);
	}

	/**
	 * Adds a take command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addTakeCommand(String cmd) {
		this.takeCommands.add(cmd);
	}

	/**
	 * @return the exitCommands
	 */
	public List<String> getExitCommands() {
		return exitCommands;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the inventoryCommands
	 */
	public List<String> getInventoryCommands() {
		return inventoryCommands;
	}

	/**
	 * @return the inventoryEmptyText
	 */
	public String getInventoryEmptyText() {
		return inventoryEmptyText;
	}

	/**
	 * @return the moveCommands
	 */
	public List<String> getMoveCommands() {
		return moveCommands;
	}

	/**
	 * @return the noCommandText
	 */
	public String getNoCommandText() {
		return noCommandText;
	}

	/**
	 * @return the noSuchItemText
	 */
	public String getNoSuchItemText() {
		return noSuchItemText;
	}

	/**
	 * @return the notTakeableText
	 */
	public String getNotTakeableText() {
		return notTakeableText;
	}

	/**
	 * @return the startLocation
	 */
	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * @return the startText
	 */
	public String getStartText() {
		return startText;
	}

	/**
	 * @return the takeCommands
	 */
	public List<String> getTakeCommands() {
		return takeCommands;
	}

	/**
	 * @return the takenText
	 */
	public String getTakenText() {
		return takenText;
	}

	/**
	 * Removes an exit command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeExitCommand(String cmd) {
		this.exitCommands.remove(cmd);
	}

	/**
	 * Removes an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeInventoryCommand(String cmd) {
		this.inventoryCommands.remove(cmd);
	}

	/**
	 * Removes a move command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeMoveCommand(String cmd) {
		this.moveCommands.remove(cmd);
	}

	/**
	 * Removes a move command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeTakeCommand(String cmd) {
		this.takeCommands.remove(cmd);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param inventoryEmptyText
	 *            the inventoryEmptyText to set
	 */
	public void setInventoryEmptyText(String inventoryEmptyText) {
		this.inventoryEmptyText = inventoryEmptyText;
	}

	/**
	 * @param noCommandText
	 *            the noCommandText to set
	 */
	public void setNoCommandText(String noCommandText) {
		this.noCommandText = noCommandText;
	}

	/**
	 * @param noSuchItemText
	 *            the noSuchItemText to set
	 */
	public void setNoSuchItemText(String noSuchItemText) {
		this.noSuchItemText = noSuchItemText;
	}

	/**
	 * @param notTakeableText
	 *            the notTakeableText to set
	 */
	public void setNotTakeableText(String notTakeableText) {
		this.notTakeableText = notTakeableText;
	}

	/**
	 * @param startLocation
	 *            the startLocation to set
	 */
	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	/**
	 * @param startText
	 *            the startText to set
	 */
	public void setStartText(String startText) {
		this.startText = startText;
	}

	/**
	 * @param takenText
	 *            the takenText to set
	 */
	public void setTakenText(String takenText) {
		this.takenText = takenText;
	}
}