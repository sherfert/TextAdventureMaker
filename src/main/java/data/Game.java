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
	 * All commands that make the game exit. Must be lowercase.
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
	 * All commands that let the player inspect something. Must be lowercase.
	 * Must contain at least one word and exactly one parameter for the object:
	 * {@literal (.+)}
	 */
	@ElementCollection
	private List<String> inspectCommands;

	/**
	 * The text being displayed when an object is inspected that does not have
	 * an individual inspection text. Valid placeholders:
	 * {@literal <identifier>}
	 */
	private String inspectionDefaultText;

	/**
	 * All commands that let the player look into his inventory. Must be
	 * lowercase.
	 */
	@ElementCollection
	private List<String> inventoryCommands;

	/**
	 * The text being displayed, when the player looks into his empty inventory.
	 */
	private String inventoryEmptyText;

	/**
	 * The text introducing a look into the inventory.
	 */
	private String inventoryText;

	/**
	 * All move commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the target: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> moveCommands;

	/**
	 * The text being displayed, when any entered text is not recognized as a
	 * valid command.
	 */
	private String noCommandText;

	/**
	 * The text being displayed, when the player tries to use, etc. a
	 * non-existing inventory item. Valid placeholders: {@literal <identifier>}
	 */
	private String noSuchInventoryItemText;

	/**
	 * The text being displayed, when the player tries to use, take, etc. a
	 * non-existing item. Valid placeholders: {@literal <identifier>}
	 */
	private String noSuchItemText;

	/**
	 * The text being displayed, when the player tries to travel by a
	 * non-existing way. Valid placeholders: {@literal <identifier>}
	 */
	private String noSuchWayText;

	/**
	 * The default text, when the player tries to take a non-takeable item. May
	 * be overwritten for each individual item. Valid placeholders:
	 * {@literal <identifier>}
	 */
	private String notTakeableText;

	/**
	 * The default text, when the player tries to travel by a non-travelable
	 * way. May be overwritten for each individual way. Valid placeholders:
	 * {@literal <identifier>}
	 */
	private String notTravelableText;

	/**
	 * The default text, when the player tries to use a non-usable object. May
	 * be overwritten for each individual object. Valid placeholders:
	 * {@literal <identifier>}
	 */
	private String notUsableText;

	/**
	 * The default text, when the player tries to use two incompatible object
	 * with one another. May be overwritten for each individual combination.
	 * Valid placeholders: {@literal <invIdentifier>},
	 * {@literal <itemIdentifier>}
	 */
	private String notUsableWithText;

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
	 * All take commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> takeCommands;

	/**
	 * The default text, when the player takes an item. May be overwritten for
	 * each individual item. Valid placeholders: {@literal <identifier>}
	 */
	private String takenText;

	/**
	 * All use commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> useCommands;

	/**
	 * The default text, when the player uses an object. May be overwritten for
	 * each individual object. Valid placeholders: {@literal <identifier>}
	 */
	private String usedText;

	/**
	 * The default text, when the player uses two compatible object with one
	 * another. May be overwritten for each individual combination. Valid
	 * placeholders: {@literal <invIdentifier>}, {@literal <itemIdentifier>}
	 */
	private String usedWithText;

	/**
	 * All useWith/combine commands. Must be lowercase. Must contain at least
	 * one word and exactly two parameters for the objects: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> useWithCombineCommands;

	/**
	 * Constructs a new game object.
	 */
	public Game() {
		exitCommands = new ArrayList<String>();
		inspectCommands = new ArrayList<String>();
		inventoryCommands = new ArrayList<String>();
		moveCommands = new ArrayList<String>();
		takeCommands = new ArrayList<String>();
		useCommands = new ArrayList<String>();
		useWithCombineCommands = new ArrayList<String>();
	}

	/**
	 * Adds an exit command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addExitCommand(String cmd) {
		this.exitCommands.add(cmd.toLowerCase());
	}
	
	/**
	 * Adds an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addInspectCommand(String cmd) {
		this.inspectCommands.add(cmd.toLowerCase());
	}
	
	/**
	 * Adds an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addInventoryCommand(String cmd) {
		this.inventoryCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a move command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addMoveCommand(String cmd) {
		this.moveCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a take command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addTakeCommand(String cmd) {
		this.takeCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a use command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addUseCommand(String cmd) {
		this.useCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds an useWith/combine command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addUseWithCombineCommand(String cmd) {
		this.useWithCombineCommands.add(cmd.toLowerCase());
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
	 * @return the inspectCommands
	 */
	public List<String> getInspectCommands() {
		return inspectCommands;
	}

	/**
	 * @return the inspectionDefaultText
	 */
	public String getInspectionDefaultText() {
		return inspectionDefaultText;
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
	 * @return the inventoryText
	 */
	public String getInventoryText() {
		return inventoryText;
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
	 * @return the noSuchInventoryItemText
	 */
	public String getNoSuchInventoryItemText() {
		return noSuchInventoryItemText;
	}

	/**
	 * @return the noSuchItemText
	 */
	public String getNoSuchItemText() {
		return noSuchItemText;
	}

	/**
	 * @return the noSuchWayText
	 */
	public String getNoSuchWayText() {
		return noSuchWayText;
	}

	/**
	 * @return the notTakeableText
	 */
	public String getNotTakeableText() {
		return notTakeableText;
	}

	/**
	 * @return the notTravelableText
	 */
	public String getNotTravelableText() {
		return notTravelableText;
	}

	/**
	 * @return the notUsableText
	 */
	public String getNotUsableText() {
		return notUsableText;
	}

	/**
	 * @return the notUsableWithText
	 */
	public String getNotUsableWithText() {
		return notUsableWithText;
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
	 * @return the useCommands
	 */
	public List<String> getUseCommands() {
		return useCommands;
	}

	/**
	 * @return the usedText
	 */
	public String getUsedText() {
		return usedText;
	}

	/**
	 * @return the usedWithText
	 */
	public String getUsedWithText() {
		return usedWithText;
	}

	/**
	 * @return the useWithCombineCommands
	 */
	public List<String> getUseWithCombineCommands() {
		return useWithCombineCommands;
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
	 * Removes a take command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeTakeCommand(String cmd) {
		this.takeCommands.remove(cmd);
	}

	/**
	 * Removes a use command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeUseCommand(String cmd) {
		this.useCommands.remove(cmd);
	}

	/**
	 * Removes an useWith/combine command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeUseWithCombineCommand(String cmd) {
		this.useWithCombineCommands.remove(cmd);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param inspectionDefaultText
	 *            the inspectionDefaultText to set
	 */
	public void setInspectionDefaultText(String inspectionDefaultText) {
		this.inspectionDefaultText = inspectionDefaultText;
	}

	/**
	 * @param inventoryEmptyText
	 *            the inventoryEmptyText to set
	 */
	public void setInventoryEmptyText(String inventoryEmptyText) {
		this.inventoryEmptyText = inventoryEmptyText;
	}

	/**
	 * @param inventoryText
	 *            the inventoryText to set
	 */
	public void setInventoryText(String inventoryText) {
		this.inventoryText = inventoryText;
	}

	/**
	 * @param noCommandText
	 *            the noCommandText to set
	 */
	public void setNoCommandText(String noCommandText) {
		this.noCommandText = noCommandText;
	}

	/**
	 * @param noSuchInventoryItemText
	 *            the noSuchInventoryItemText to set
	 */
	public void setNoSuchInventoryItemText(String noSuchInventoryItemText) {
		this.noSuchInventoryItemText = noSuchInventoryItemText;
	}

	/**
	 * @param noSuchItemText
	 *            the noSuchItemText to set
	 */
	public void setNoSuchItemText(String noSuchItemText) {
		this.noSuchItemText = noSuchItemText;
	}

	/**
	 * @param noSuchWayText
	 *            the noSuchWayText to set
	 */
	public void setNoSuchWayText(String noSuchWayText) {
		this.noSuchWayText = noSuchWayText;
	}

	/**
	 * @param notTakeableText
	 *            the notTakeableText to set
	 */
	public void setNotTakeableText(String notTakeableText) {
		this.notTakeableText = notTakeableText;
	}

	/**
	 * @param notTravelableText
	 *            the notTravelableText to set
	 */
	public void setNotTravelableText(String notTravelableText) {
		this.notTravelableText = notTravelableText;
	}

	/**
	 * @param notUsableText
	 *            the notUsableText to set
	 */
	public void setNotUsableText(String notUsableText) {
		this.notUsableText = notUsableText;
	}

	/**
	 * @param notUsableWithText
	 *            the notUsableWithText to set
	 */
	public void setNotUsableWithText(String notUsableWithText) {
		this.notUsableWithText = notUsableWithText;
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

	/**
	 * @param usedText
	 *            the usedText to set
	 */
	public void setUsedText(String usedText) {
		this.usedText = usedText;
	}

	/**
	 * @param usedWithText
	 *            the usedWithText to set
	 */
	public void setUsedWithText(String usedWithText) {
		this.usedWithText = usedWithText;
	}
}