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
 * A game that can be played. 
 * 
 * @author Satia
 */
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
	
	private String noCommandText;
	
	private String inventoryEmptyText;
	
	private String noSuchItemText;
	
	private String takenText;
	
	private String notTakeableText;
	
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

	/**
	 * @return the inventoryEmptyText
	 */
	public String getInventoryEmptyText() {
		return inventoryEmptyText;
	}

	/**
	 * @param inventoryEmptyText the inventoryEmptyText to set
	 */
	public void setInventoryEmptyText(String inventoryEmptyText) {
		this.inventoryEmptyText = inventoryEmptyText;
	}

	/**
	 * @return the noSuchItemText
	 */
	public String getNoSuchItemText() {
		return noSuchItemText;
	}

	/**
	 * @param noSuchItemText the noSuchItemText to set
	 */
	public void setNoSuchItemText(String noSuchItemText) {
		this.noSuchItemText = noSuchItemText;
	}

	/**
	 * @return the takenText
	 */
	public String getTakenText() {
		return takenText;
	}

	/**
	 * @param takenText the takenText to set
	 */
	public void setTakenText(String takenText) {
		this.takenText = takenText;
	}

	/**
	 * @return the notTakeableText
	 */
	public String getNotTakeableText() {
		return notTakeableText;
	}

	/**
	 * @param notTakeableText the notTakeableText to set
	 */
	public void setNotTakeableText(String notTakeableText) {
		this.notTakeableText = notTakeableText;
	}

	/**
	 * @return the noCommandText
	 */
	public String getNoCommandText() {
		return noCommandText;
	}

	/**
	 * @param noCommandText the noCommandText to set
	 */
	public void setNoCommandText(String noCommandText) {
		this.noCommandText = noCommandText;
	}
}