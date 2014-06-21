package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * A game that can be played. Contains all configuration and (default) texts.
 * There should be only one game per database.
 * 
 * @author Satia
 */
@Entity
public class Game {
	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;
	
	/**
	 * All commands that make the game exit. Must be lowercase.
	 */
	@ElementCollection
	private List<String> exitCommands;

	/**
	 * All commands that let the player inspect something. Must be lowercase.
	 * Must contain at least one word and exactly one parameter for the object:
	 * {@literal (.+)}
	 */
	@ElementCollection
	private List<String> inspectCommands;

	/**
	 * The text being displayed when an object is inspected that does not have
	 * an individual inspection text. Valid placeholders: {@literal <input>},
	 * {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String inspectionDefaultText;

	/**
	 * All commands that let the player look into his inventory. Must be
	 * lowercase.
	 */
	@ElementCollection
	private List<String> inventoryCommands;

	/**
	 * The text being displayed, when the player looks into his empty inventory.
	 * Valid placeholders: {@literal <input>}
	 */
	@Column(nullable = false)
	private String inventoryEmptyText;

	/**
	 * The text introducing a look into the inventory. Valid placeholders:
	 * {@literal <input>}
	 */
	@Column(nullable = false)
	private String inventoryText;

	/**
	 * All move commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the target: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> moveCommands;

	/**
	 * The text being displayed, when any entered text is not recognized as a
	 * valid command. Valid placeholders: {@literal <input>}
	 */
	@Column(nullable = false)
	private String noCommandText;

	/**
	 * The text being displayed, when the player tries to use, etc. a
	 * non-existing inventory item. Valid placeholders: {@literal <input>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchInventoryItemText;

	/**
	 * The text being displayed, when the player tries to use, take, etc. a
	 * non-existing item. Valid placeholders: {@literal <input>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchItemText;

	/**
	 * The text being displayed, when the player tries to travel by a
	 * non-existing way. Valid placeholders: {@literal <input>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchWayText;

	/**
	 * The default text, when the player tries to take a non-takeable item. May
	 * be overwritten for each individual item. Valid placeholders:
	 * {@literal <input>}, {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notTakeableText;

	/**
	 * The default text, when the player tries to travel by a non-travelable
	 * way. May be overwritten for each individual way. Valid placeholders:
	 * {@literal <input>}, {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notTravelableText;

	/**
	 * The default text, when the player tries to use a non-usable object. May
	 * be overwritten for each individual object. Valid placeholders:
	 * {@literal <input>}, {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notUsableText;

	/**
	 * The default text, when the player tries to use two incompatible object
	 * with one another. May be overwritten for each individual combination.
	 * Valid placeholders: {@literal <input>}, {@literal <name>},
	 * {@literal <identifier>}, {@literal <name2>}, {@literal <identifier2>}
	 */
	@Column(nullable = false)
	private String notUsableWithText;

	/**
	 * The starting location of the game.
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false)
	private Location startLocation;

	/**
	 * The text being displayed, when the game starts.
	 */
	@Column(nullable = false)
	private String startText;

	/**
	 * All take commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> takeCommands;

	/**
	 * The default text, when the player takes an item. May be overwritten for
	 * each individual item. Valid placeholders: {@literal <input>},
	 * {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String takenText;

	/**
	 * All use commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> useCommands;

	/**
	 * The default text, when the player uses an object. May be overwritten for
	 * each individual object. Valid placeholders: {@literal <input>},
	 * {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String usedText;

	/**
	 * The default text, when the player uses two compatible object with one
	 * another. May be overwritten for each individual combination. Valid
	 * placeholders: {@literal <input>}, {@literal <name>},
	 * {@literal <identifier>}, {@literal <name2>}, {@literal <identifier2>}
	 */
	@Column(nullable = false)
	private String usedWithText;

	/**
	 * All useWith/combine commands. Must be lowercase. Must contain at least
	 * one word and exactly two parameters for the objects: {@literal (.+)}
	 */
	@ElementCollection
	private List<String> useWithCombineCommands;
	
	/**
	 * The color that is used for text printed after a successful action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color successfullFgColor;
	/**
	 * The color that is used for text printed after a neutral action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color neutralFgColor;
	/**
	 * The color that is used for text printed after a failed action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color failedFgColor;
	
	/**
	 * The background color that is used for text printed after a successful action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color successfullBgColor;
	/**
	 * The background color that is used for text printed after a neutral action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color neutralBgColor;
	/**
	 * The background color that is used for text printed after a failed action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color failedBgColor;

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
	 * @return the successfullFgColor
	 */
	public Color getSuccessfullFgColor() {
		return successfullFgColor;
	}

	/**
	 * @return the neutralFgColor
	 */
	public Color getNeutralFgColor() {
		return neutralFgColor;
	}

	/**
	 * @return the failedFgColor
	 */
	public Color getFailedFgColor() {
		return failedFgColor;
	}

	/**
	 * @return the successfullBgColor
	 */
	public Color getSuccessfullBgColor() {
		return successfullBgColor;
	}

	/**
	 * @return the neutralBgColor
	 */
	public Color getNeutralBgColor() {
		return neutralBgColor;
	}

	/**
	 * @return the failedBgColor
	 */
	public Color getFailedBgColor() {
		return failedBgColor;
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

	/**
	 * @param successfullFgColor the successfullFgColor to set
	 */
	public void setSuccessfullFgColor(Color successfullFgColor) {
		this.successfullFgColor = successfullFgColor;
	}

	/**
	 * @param neutralFgColor the neutralFgColor to set
	 */
	public void setNeutralFgColor(Color neutralFgColor) {
		this.neutralFgColor = neutralFgColor;
	}

	/**
	 * @param failedFgColor the failedFgColor to set
	 */
	public void setFailedFgColor(Color failedFgColor) {
		this.failedFgColor = failedFgColor;
	}

	/**
	 * @param successfullBgColor the successfullBgColor to set
	 */
	public void setSuccessfullBgColor(Color successfullBgColor) {
		this.successfullBgColor = successfullBgColor;
	}

	/**
	 * @param neutralBgColor the neutralBgColor to set
	 */
	public void setNeutralBgColor(Color neutralBgColor) {
		this.neutralBgColor = neutralBgColor;
	}

	/**
	 * @param failedBgColor the failedBgColor to set
	 */
	public void setFailedBgColor(Color failedBgColor) {
		this.failedBgColor = failedBgColor;
	}
}