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

@Entity
public class Game {
	
	public Game() {
		exitCommands = new ArrayList<String>();
		inventoryCommands = new ArrayList<String>();
		moveCommands = new ArrayList<String>();
		takeCommands = new ArrayList<String>();
	}
	
	@Id
	@GeneratedValue
	private int id;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn
	private Location startLocation;
	
	private String startText;
	
	@ElementCollection
	private List<String> exitCommands;
	@ElementCollection
	private List<String> inventoryCommands;
	@ElementCollection
	private List<String> moveCommands;
	@ElementCollection
	private List<String> takeCommands;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the startLocation
	 */
	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * @param startLocation the startLocation to set
	 */
	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	/**
	 * @return the startText
	 */
	public String getStartText() {
		return startText;
	}

	/**
	 * @param startText the startText to set
	 */
	public void setStartText(String startText) {
		this.startText = startText;
	}
	
	public void addExitCommand(String cmd) {
		this.exitCommands.add(cmd);
	}
	
	public void removeExitCommand(String cmd) {
		this.exitCommands.remove(cmd);
	}
	
	public void addInventoryCommand(String cmd) {
		this.inventoryCommands.add(cmd);
	}
	
	public void removeInventoryCommand(String cmd) {
		this.inventoryCommands.remove(cmd);
	}
	
	public void addMoveCommand(String cmd) {
		this.moveCommands.add(cmd);
	}
	
	public void removeMoveCommand(String cmd) {
		this.moveCommands.remove(cmd);
	}
	
	public void addTakeCommand(String cmd) {
		this.takeCommands.add(cmd);
	}
	
	public void removeTakeCommand(String cmd) {
		this.takeCommands.remove(cmd);
	}

	/**
	 * @return the exitCommands
	 */
	public List<String> getExitCommands() {
		return exitCommands;
	}

	/**
	 * @return the inventoryCommands
	 */
	public List<String> getInventoryCommands() {
		return inventoryCommands;
	}

	/**
	 * @return the moveCommands
	 */
	public List<String> getMoveCommands() {
		return moveCommands;
	}

	/**
	 * @return the takeCommands
	 */
	public List<String> getTakeCommands() {
		return takeCommands;
	}
}